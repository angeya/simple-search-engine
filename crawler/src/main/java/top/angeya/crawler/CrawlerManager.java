package top.angeya.crawler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import top.angeya.config.CrawlerConfig;
import top.angeya.crawler.scheduler.SchedulerManager;
import us.codecraft.webmagic.Spider;

import java.util.List;

/**
 * 爬虫管理器
 *
 * @Author: wanganjie 5790
 * @Date: 2023/10/19 20:27
 * @Description:
 */
@Slf4j
@Component
public class CrawlerManager implements ApplicationRunner {
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
     * 项目启动完成后开始
     * 如果在PostConstruct中执行会导致有些Component还没有加载
     */
    @Override
    public void run(ApplicationArguments args) {
        this.startCrawl();
    }

    /**
     * 开始抓取
     */
    private void startCrawl() {
        List<String> webList = crawlerConfig.getWebList();
        log.info("init url list: {}", webList);
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
