package top.angeya.crawler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import top.angeya.config.CrawlerConfig;
import top.angeya.crawler.pipeline.SimplePageHandler;
import top.angeya.crawler.scheduler.MysqlScheduler;

import java.util.List;

/**
 * 爬虫管理器
 *
 * @Author: angeya
 * @Date: 2023/10/19 20:27
 * @Description:
 */
@Slf4j
@Component
public class CrawlerManager implements ApplicationRunner {
    private final CrawlerConfig crawlerConfig;

    private final SimplePageHandler simplePageHandler;



    public CrawlerManager(CrawlerConfig crawlerConfig, SimplePageHandler simplePageHandler) {
        this.crawlerConfig = crawlerConfig;
        this.simplePageHandler = simplePageHandler;
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
        Crawler.create()
                .addUrlList(webList)
                .threads(threadCount)
                .setSchedule(new MysqlScheduler())
                .addPipeline(simplePageHandler)
                .start();
    }


}
