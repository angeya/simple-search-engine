package top.angeya.crawler;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import top.angeya.config.CrawlerConfig;
import top.angeya.service.WebPageInfoService;
import us.codecraft.webmagic.Spider;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author: wanganjie 5790
 * @Date: 2023/10/19 20:27
 * @Description:
 */
@Component
public class CrawlerManager implements ApplicationRunner {
    private final CrawlerConfig crawlerConfig;

    private final CommonPageProcessor commonPageProcessor;

    private final WebPageInfoService webPageInfoService;

    private final WebPageSavingPipeline webPageSavingPipeline;

    public CrawlerManager(CrawlerConfig crawlerConfig, CommonPageProcessor commonPageProcessor,
                          WebPageInfoService webPageInfoService, WebPageSavingPipeline webPageSavingPipeline) {
        this.crawlerConfig = crawlerConfig;
        this.commonPageProcessor = commonPageProcessor;
        this.webPageInfoService = webPageInfoService;
        this.webPageSavingPipeline = webPageSavingPipeline;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        this.startCrawl();
    }

    /**
     * 开始抓取
     */
    @PostConstruct
    private void startCrawl() {
        List<String> webList = crawlerConfig.getWebList();
        String[] webs = new String[webList.size()];
        webList.toArray(webs);

        // 使用新线程开始搬运数据
        //CommonThreadPool.EXECUTOR_SERVICE.submit(this.webDataService::startCarryWebData);

        // 通过new的CommonPageProcessor方式，无法获取里面以来的Bean
        int threadCount = crawlerConfig.getThreadCount();
        Spider.create(this.commonPageProcessor)
                .addUrl(webs)
                .addPipeline(webPageSavingPipeline)
                .thread(threadCount)
                .run();
    }
}
