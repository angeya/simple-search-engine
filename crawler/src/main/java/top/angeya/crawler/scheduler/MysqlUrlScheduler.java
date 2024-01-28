package top.angeya.crawler.scheduler;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import top.angeya.constant.CrawlerDataRecordType;
import top.angeya.dao.CrawlerDataRecordMapper;
import top.angeya.dao.UrlInfoMapper;
import top.angeya.dao.WebPageInfoMapper;
import top.angeya.pojo.entity.CrawlerDataRecord;
import top.angeya.pojo.entity.UrlInfo;
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
public class MysqlUrlScheduler implements UrlScheduler {

    /**
     * url地址队列
     */
    private final BlockingQueue<UrlInfo> urlInfoQueue = new LinkedBlockingQueue<>();

    /**
     * 已完成de网页URL集合，避免重复爬取
     */
    private final Set<String> uniqueUrlSet = new ConcurrentSkipListSet<>();

    /**
     * url数据库映射
     */
    private final UrlInfoMapper urlInfoMapper;

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

    public MysqlUrlScheduler() {
        this.urlInfoMapper = BeanUtil.getBean(UrlInfoMapper.class);
        this.crawlerDataRecordMapper = BeanUtil.getBean(CrawlerDataRecordMapper.class);
        this.webPageInfoMapper = BeanUtil.getBean(WebPageInfoMapper.class);
        this.init();
    }

    @Override
    public UrlInfo pop() {
        UrlInfo urlInfo;
        try {
            urlInfo = this.urlInfoQueue.take();
        } catch (InterruptedException e) {
            log.error("get url from queue error", e);
            return null;
        }
        if (urlInfo.getUrl() == null) {
            log.error("url text is null, {}", urlInfo);
            return null;
        }
        this.crawlerDataRecord.setValue(urlInfo.getId());
        this.crawlerDataRecordMapper.updateById(this.crawlerDataRecord);
        return urlInfo;
    }

    @Async
    @Override
    public void push(UrlInfo urlInfo) {
        // 如果url队列元素个数已经大于等于最大限制，则不再加入url
        if (this.urlInfoQueue.size() >= this.maxQueueSize) {
            return;
        }

        String url = urlInfo.getUrl();
        if (!Tools.isUrlValid(url) || this.uniqueUrlSet.contains(url)) {
            return;
        }
        this.uniqueUrlSet.add(url);
        // url信息入库
        try {
            this.urlInfoMapper.insert(urlInfo);
            // url加入消息队列
            this.urlInfoQueue.put(urlInfo);
        } catch (Exception e) {
            log.error("push url:{} error", url, e);
            return;
        }
        log.info("add new url [{}], queue size is {}", url, this.urlInfoQueue.size());
    }

    @Async
    @Override
    public void push(List<String> urlList) {
        for (String s : urlList) {
            this.push(new UrlInfo(s));
        }
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
        List<UrlInfo> allUrlListInfo = this.urlInfoMapper.selectList(Wrappers.emptyWrapper());
        this.uniqueUrlSet.addAll(allUrlListInfo.stream()
                .map(UrlInfo::getUrl)
                .collect(Collectors.toList()));
        log.info("get url set complete, size is {} cost {} ms", allUrlListInfo.size(), System.currentTimeMillis() - start);
        // 根据之前的爬取下标，获取待爬取的url数据
        List<UrlInfo> unProcessedUrlListInfo = this.urlInfoMapper.selectList(Wrappers.lambdaQuery(UrlInfo.class)
                .notIn(!processedUrlSet.isEmpty(), UrlInfo::getUrl, processedUrlSet));

        log.info("------  init the url queue from DB, size is {}, max size is {} -------",
                unProcessedUrlListInfo.size(), this.maxQueueSize);
        start = System.currentTimeMillis();
        // url加入队列
        unProcessedUrlListInfo.forEach(url -> {
            boolean success = this.urlInfoQueue.offer(url);
            if (success) {
                log.info(url.getUrl());
            }
        });
        log.info("------  DB url add to queue finished, cost {} ms -------", System.currentTimeMillis() - start);
    }
}
