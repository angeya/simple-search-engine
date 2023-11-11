package top.angeya.crawler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.List;

/**
 * @Author: wanganjie 5790
 * @Date: 2023/10/19 19:59
 * @Description:
 */
@Slf4j
@Component
public class CommonPageProcessor implements PageProcessor {


    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        Document document = html.getDocument();
        Elements elements = document.getElementsByTag("title");
        String title = elements.isEmpty() ? "" : elements.get(0).text();
        String url = page.getUrl().get();

        // 获取所有链接
        List<String> urlList = page.getHtml().links().all();
        // 内部会简单的做非url校验
        page.addTargetRequests(urlList);
        log.info("processing url: [{}] - [{}]", url, title);
        // 设置网页数据
        page.putField("title", title);
        page.putField("url", url);
        page.putField("smartContent", html.smartContent().get());
        page.putField("rawContent", html.get());
    }

    @Override
    public Site getSite() {
        // 设置重试次数
        return Site.me().setRetryTimes(3).setSleepTime(1000);
    }
}
