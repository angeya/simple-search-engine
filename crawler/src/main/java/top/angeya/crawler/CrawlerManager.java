package top.angeya.crawler;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import top.angeya.config.CrawlerConfig;
import top.angeya.service.CommonWebDataService;
import us.codecraft.webmagic.Spider;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author: wanganjie 5790
 * @Date: 2023/10/19 20:27
 * @Description:
 */
@Component
public class CrawlerManager implements ApplicationRunner{
    private final CrawlerConfig crawlerConfig;

    private final CommonPageProcessor commonPageProcessor;

    public CrawlerManager(CrawlerConfig crawlerConfig, CommonPageProcessor commonPageProcessor) {
        this.crawlerConfig = crawlerConfig;
        this.commonPageProcessor = commonPageProcessor;
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
        // 通过new的CommonPageProcessor方式，无法获取里面以来的Bean
        Spider.create(this.commonPageProcessor)
                .addUrl(webs)
                .thread(4)
                .run();
    }
}
