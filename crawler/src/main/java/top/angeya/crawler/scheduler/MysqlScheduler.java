package top.angeya.crawler.scheduler;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import top.angeya.constant.CrawlerDataRecordType;
import top.angeya.dao.CrawlerDataRecordMapper;
import top.angeya.dao.UrlMapper;
import top.angeya.entity.CrawlerDataRecord;
import top.angeya.entity.Url;
import top.angeya.util.BeanUtil;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author: angeya
 * @date: 2023/11/7 23:32
 * @description:
 */
@Slf4j
public class MysqlScheduler extends DuplicateRemovedScheduler {

    /**
     * 读锁
     */
    private final Object readLock = new Object();

    /**
     * 写锁
     */
    private final Object writeLock = new Object();

    /**
     * url地址队列
     */
    private final BlockingQueue<Url> urlQueue = new LinkedBlockingQueue<>();

    /**
     * 已完成de网页URL集合，避免重复爬取
     */
    private final Set<String> uniqueUrlSet = new ConcurrentSkipListSet<>();

    /**
     * url数据库映射
     */
    private final UrlMapper urlMapper;

    /**
     * 爬虫数据记录数据库映射
     */
    private final CrawlerDataRecordMapper crawlerDataRecordMapper;

    /**
     * 爬虫数据记录
     */
    private final CrawlerDataRecord crawlerDataRecord = new CrawlerDataRecord(CrawlerDataRecordType.URL_QUEUE_INDEX);

    public MysqlScheduler() {
        this.urlMapper = BeanUtil.getBean(UrlMapper.class);
        this.crawlerDataRecordMapper = BeanUtil.getBean(CrawlerDataRecordMapper.class);
        this.init();
    }


    @Override
    public Request poll(Task task) {
        Url url;
        // 同步更新数据库记录
        synchronized (this.readLock) {
            url = this.urlQueue.poll();
            if (url == null || url.getText() == null) {
                log.error("url or its property is null, {}", url);
                return null;
            }
            this.crawlerDataRecord.setValue(url.getId());
            this.crawlerDataRecordMapper.updateById(this.crawlerDataRecord);
        }
        return new Request(url.getText());
    }

    @Override
    public void push(Request request, Task task) {
        String urlText = request.getUrl();
        if (this.uniqueUrlSet.contains(urlText)) {
            return;
        }
        this.uniqueUrlSet.add(urlText);
        // url信息入库
        Url url = new Url(urlText);
        try {
            synchronized (this.writeLock) {
                this.urlMapper.insert(url);
                // url加入消息队列
                this.urlQueue.offer(url);
            }
        } catch (Exception e) {
            log.error("push url:{} error", urlText, e);
            return;
        }
        log.info("add new url [{}]", urlText);
    }

    /**
     * 初始化工作
     */
    private void init() {
        // 获取url的爬取记录
        CrawlerDataRecord dbDataRecord = this.crawlerDataRecordMapper.selectOne(Wrappers.lambdaQuery(CrawlerDataRecord.class)
                .eq(CrawlerDataRecord::getCode, CrawlerDataRecordType.URL_QUEUE_INDEX));
        if (dbDataRecord == null) {
            // 如果数据库没有值，则设置初始值为0并保存
            this.crawlerDataRecord.setValue(0L);
            this.crawlerDataRecordMapper.insert(this.crawlerDataRecord);
        } else {
            this.crawlerDataRecord.setValue(dbDataRecord.getValue());
        }

        long start = System.currentTimeMillis();
        log.info("start get url set");
        // 获取所有已经爬取过的url 加入set集合去重
        List<Url> allUrlList = this.urlMapper.selectList(Wrappers.emptyWrapper());
        this.uniqueUrlSet.addAll(allUrlList.stream()
                .map(Url::getText)
                .collect(Collectors.toList()));
        log.info("get url set complete, size is {} cost {} ms", allUrlList.size(), System.currentTimeMillis() - start);
        // 根据之前的爬取下标，获取待爬取的url数据
        long lastTimeUrlIndex = this.crawlerDataRecord.getValue();
        List<Url> unProcceUrlList = this.urlMapper.selectList(Wrappers.lambdaQuery(Url.class)
                .gt(Url::getId, lastTimeUrlIndex));
        log.info("------  init the url queue from DB, size is {} -------", unProcceUrlList.size());
        // url加入队列
        unProcceUrlList.forEach(url -> {
            boolean success = this.urlQueue.offer(url);
            if (success) {
                log.info(url.getText());
            }
        });
        log.info("------  DB url add to queue finished -------");
    }
}
