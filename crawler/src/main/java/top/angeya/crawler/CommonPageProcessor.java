package top.angeya.crawler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.angeya.entity.CommonWebData;
import top.angeya.service.CommonWebDataService;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: wanganjie 5790
 * @Date: 2023/10/19 19:59
 * @Description:
 */
@Slf4j
@Component
public class CommonPageProcessor implements PageProcessor {

    /**
     * 网页数据服务
     */
    @Autowired
    private CommonWebDataService commonWebDataService;

    /**
     * 网页URL集合，避免重复爬取
     */
    private Set<String> urlSet;

    /**
     * 初始化工作
     */
    @PostConstruct
    private void init() {
        urlSet = this.commonWebDataService.getUrlSetFromDb();
    }

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        Document document = html.getDocument();
        Elements elements = document.getElementsByTag("title");
        String title = elements.isEmpty() ? "" : elements.get(0).text();
        String url = page.getUrl().get();
        String webInfo = title + "--------------" + url + "\n";

        // 存在的网页也加入待爬取队列，避免重启后无法继续爬取
        List<String> urlList = page.getHtml().links().all();
        List<String> newUrlList = urlList.stream()
                .filter(webUrl -> {
                    if (!urlSet.contains(webUrl)) {
                        return true;
                    }
                    log.warn("web is exists: " + webInfo);
                    return false;
                }).collect(Collectors.toList());

        page.addTargetRequests(newUrlList);
        urlSet.addAll(newUrlList);

        log.info(webInfo);
        // 创建网页数据对象
        CommonWebData webData = new CommonWebData();
        webData.setTitle(title);
        webData.setUrl(url);
        webData.setWebContent(html.get());
        webData.setSmartContent(html.smartContent().get());
        webData.setCreateTime(LocalDateTime.now());

        // 加入数据队列
        this.commonWebDataService.addWebDataToQueue(webData);
    }

    @Override
    public Site getSite() {
        // 设置重试次数
        return Site.me().setRetryTimes(3).setSleepTime(1000);
    }

}
