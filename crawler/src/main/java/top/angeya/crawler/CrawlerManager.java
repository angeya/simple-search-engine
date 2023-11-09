package top.angeya.crawler;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import top.angeya.config.CrawlerConfig;
import top.angeya.enums.UrlQueueStorageType;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 爬虫管理器
 *
 * @Author: wanganjie 5790
 * @Date: 2023/10/19 20:27
 * @Description:
 */
@Component
public class CrawlerManager {
    private final CrawlerConfig crawlerConfig;

    private final CommonPageProcessor commonPageProcessor;

    private final WebPageSavingPipeline webPageSavingPipeline;

    @Value("${crawler.url-file-path}")
    private String urlCacheFilePath;

    @Value("${crawler.url-queue-storage-type}")
    private String urlQueueStorageType;

    private RedisProperties redisProperties;

    public CrawlerManager(CrawlerConfig crawlerConfig, CommonPageProcessor commonPageProcessor,
                          WebPageSavingPipeline webPageSavingPipeline) {
        this.crawlerConfig = crawlerConfig;
        this.commonPageProcessor = commonPageProcessor;
        this.webPageSavingPipeline = webPageSavingPipeline;
    }

    /**
     * 开始抓取
     */
    @PostConstruct
    private void startCrawl() {
        List<String> webList = crawlerConfig.getWebList();
        String[] webs = new String[webList.size()];
        webList.toArray(webs);

        // 通过new的CommonPageProcessor方式，无法获取里面依赖的Bean
        int threadCount = crawlerConfig.getThreadCount();
        Spider.create(this.commonPageProcessor)
                .addUrl(webs)
                .setScheduler(this.getSchedule())
                .addPipeline(webPageSavingPipeline)
                .thread(threadCount)
                .run();
    }

    /**
     * 获取调度器 默认使用文件调度器
     *
     * @return 调度器
     */
    private Scheduler getSchedule() {
        if (UrlQueueStorageType.MYSQL.getCode().equals(this.urlQueueStorageType)) {
            return new MysqlScheduler();
        } else if (UrlQueueStorageType.REDIS.getCode().equals(this.urlQueueStorageType)) {
            JedisPool jedisPool = new JedisPool(new GenericObjectPoolConfig<>(), redisProperties.getHost(),
                    redisProperties.getPort(), (int) redisProperties.getTimeout().getSeconds(),
                    redisProperties.getPassword());
            return new RedisScheduler(jedisPool);
        }
        return new FileCacheQueueScheduler(this.urlCacheFilePath);
    }
}
