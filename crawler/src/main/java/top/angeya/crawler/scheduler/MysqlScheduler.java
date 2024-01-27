package top.angeya.crawler.scheduler;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import top.angeya.constant.CrawlerDataRecordType;
import top.angeya.dao.CrawlerDataRecordMapper;
import top.angeya.dao.UrlMapper;
import top.angeya.dao.WebPageInfoMapper;
import top.angeya.pojo.entity.CrawlerDataRecord;
import top.angeya.pojo.entity.Url;
import top.angeya.pojo.entity.WebPageInfo;
import top.angeya.util.BeanUtil;
import top.angeya.util.Tools;

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
public class MysqlScheduler implements UrlScheduler {

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
     * 网页信息数据库映射
     */
    private final WebPageInfoMapper webPageInfoMapper;

    /**
     * 爬虫数据记录数据库映射
     */
    private final CrawlerDataRecordMapper crawlerDataRecordMapper;

    /**
     * 爬虫数据记录
     */
    private final CrawlerDataRecord crawlerDataRecord = new CrawlerDataRecord(CrawlerDataRecordType.URL_QUEUE_INDEX);

    /**
     * url队列最大长度，避免读取速度慢，导致数据堆积内存过大
     */
    @Value("${crawler.queue.max-size:1000}")
    private final int maxQueueSize = 2000;

    public MysqlScheduler() {
        this.urlMapper = BeanUtil.getBean(UrlMapper.class);
        this.crawlerDataRecordMapper = BeanUtil.getBean(CrawlerDataRecordMapper.class);
        this.webPageInfoMapper = BeanUtil.getBean(WebPageInfoMapper.class);
        this.init();
    }

    @Override
    public Url pop() {
        Url url;
        try {
            url = this.urlQueue.take();
        } catch (InterruptedException e) {
            log.error("get url from queue error", e);
            return null;
        }
        if (url.getText() == null) {
            log.error("url text is null, {}", url);
            return null;
        }
        this.crawlerDataRecord.setValue(url.getId());
        this.crawlerDataRecordMapper.updateById(this.crawlerDataRecord);
        return url;
    }

    @Override
    public void push(Url url) {
        // 如果url队列元素个数已经大于等于最大限制，则不再加入url
        if (this.urlQueue.size() >= this.maxQueueSize) {
            return;
        }

        String urlText = url.getText();
        if (!Tools.isUrlValid(urlText) || this.uniqueUrlSet.contains(urlText)) {
            return;
        }
        this.uniqueUrlSet.add(urlText);
        // url信息入库
        try {
            this.urlMapper.insert(url);
            // url加入消息队列
            this.urlQueue.put(url);
        } catch (Exception e) {
            log.error("push url:{} error", urlText, e);
            return;
        }
        log.info("add new url [{}], queue size is {}", urlText, this.urlQueue.size());
    }

    /**
     * 初始化工作
     */
    private void init() {
        long start = System.currentTimeMillis();
        log.info("start get url set");

        List<WebPageInfo> webPageInfoList = this.webPageInfoMapper.selectList(Wrappers.lambdaQuery(WebPageInfo.class)
                .select(WebPageInfo::getUrl));
        Set<String> processedUrlSet = webPageInfoList.stream().map(WebPageInfo::getUrl).collect(Collectors.toSet());

        // 获取所有已经爬取过的url 加入set集合去重
        List<Url> allUrlList = this.urlMapper.selectList(Wrappers.emptyWrapper());
        this.uniqueUrlSet.addAll(allUrlList.stream()
                .map(Url::getText)
                .collect(Collectors.toList()));
        log.info("get url set complete, size is {} cost {} ms", allUrlList.size(), System.currentTimeMillis() - start);
        // 根据之前的爬取下标，获取待爬取的url数据
        List<Url> unProceseUrlList = this.urlMapper.selectList(Wrappers.lambdaQuery(Url.class)
                .notIn(Url::getText, processedUrlSet));

        log.info("------  init the url queue from DB, size is {}, max size is {} -------",
                unProceseUrlList.size(), this.maxQueueSize);
        start = System.currentTimeMillis();
        // url加入队列
        unProceseUrlList.forEach(url -> {
            boolean success = this.urlQueue.offer(url);
            if (success) {
                log.info(url.getText());
            }
        });
        log.info("------  DB url add to queue finished, cost {} ms -------", System.currentTimeMillis() - start);
    }
}
