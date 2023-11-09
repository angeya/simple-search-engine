package top.angeya.crawler;

import org.springframework.stereotype.Component;
import top.angeya.config.CrawlerConfig;
import top.angeya.crawler.scheduler.SchedulerManager;
import us.codecraft.webmagic.Spider;

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

    private final SchedulerManager schedulerManager;

    public CrawlerManager(CrawlerConfig crawlerConfig, CommonPageProcessor commonPageProcessor,
                          WebPageSavingPipeline webPageSavingPipeline, SchedulerManager schedulerManager) {
        this.crawlerConfig = crawlerConfig;
        this.commonPageProcessor = commonPageProcessor;
        this.webPageSavingPipeline = webPageSavingPipeline;
        this.schedulerManager = schedulerManager;
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
                .setScheduler(this.schedulerManager.getSchedule())
                .addPipeline(webPageSavingPipeline)
                .thread(threadCount)
                .run();
    }


}
