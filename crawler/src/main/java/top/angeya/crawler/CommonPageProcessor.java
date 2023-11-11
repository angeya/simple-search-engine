package top.angeya.crawler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import top.angeya.util.Tools;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

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
        String webInfo = title + "-------" + url + "\n";

        // 获取所有链接
        List<String> allUurlList = page.getHtml().links().all();
        List<String> valideUrlList = allUurlList.stream()
                .filter(webUrl -> {
                    if (Tools.isUrlValid(webUrl)) {
                        return true;
                    } else {
                        log.warn("web url [{}] is invalid", webUrl);
                        return false;
                    }
                }).collect(Collectors.toList());

        page.addTargetRequests(valideUrlList);
        log.info("processing url: [{}]", url);
        // 设置网页数据
        page.putField("title", title);
        page.putField("url", url);
        page.putField("smartContent", html.smartContent().get());
        page.putField("rawContent", html.get());

        this.afterOnePageFinished(page);
    }

    @Override
    public Site getSite() {
        // 设置重试次数
        return Site.me().setRetryTimes(3).setSleepTime(1000);
    }

    /**
     * 当一个网页处理完成
     *
     * @param page 网页
     */
    private void afterOnePageFinished(Page page) {
        String url = page.getUrl().get();
        this.unFinishedUrlSet.remove(url);
    }

}
