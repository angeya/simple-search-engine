package top.angeya.crawler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.angeya.entity.CommonWebData;
import top.angeya.service.CommonWebDataService;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: wanganjie 5790
 * @Date: 2023/10/19 19:59
 * @Description:
 */
@Slf4j
@Component
public class CommonPageProcessor implements PageProcessor {

    @Autowired
    private CommonWebDataService commonWebDataService;

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        Document document = html.getDocument();
        String title = document.getElementsByTag("title").get(0).text();
        String url = page.getUrl().get();
        String webInfo = title + "--------------" + url + "\n";

        // 存在的网页也加入待爬取队列，避免重启后无法继续爬取
        List<String> urlList = page.getHtml().links().all();
        page.addTargetRequests(urlList);

        // 判断是否存在
        if (this.commonWebDataService.isWebExist(url)) {
            log.warn("网页已经存在: " + webInfo);
            return;
        }
        log.info(webInfo);
        // 创建网页数据对象
        CommonWebData webData = new CommonWebData();
        webData.setTitle(title);
        webData.setUrl(url);
        webData.setWebContent(html.get());
        webData.setSmartContent(html.smartContent().get());
        webData.setCreateTime(LocalDateTime.now());

        // 数据入库
        this.commonWebDataService.saveWebData(webData);
    }

    @Override
    public Site getSite() {
        // 设置重试次数
        return Site.me().setRetryTimes(2).setSleepTime(1000);
    }

}
