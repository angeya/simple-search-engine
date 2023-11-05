package top.angeya.crawler;

import org.springframework.stereotype.Component;
import top.angeya.entity.WebPageInfo;
import top.angeya.entity.WebPageRawData;
import top.angeya.service.WebPageInfoService;
import top.angeya.service.WebPageRawDataService;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.time.LocalDateTime;

/**
 * @author: angeya
 * @date: 2023/11/1 23:07
 * @description:
 */
@Component
public class WebPageSavingPipeline implements Pipeline {

    /**
     * 网页信息服务
     */
    private final WebPageInfoService webPageInfoService;

    /**
     * 网页原始数据服务
     */
    private final WebPageRawDataService webPageRawDataService;


    public WebPageSavingPipeline(WebPageInfoService webPageInfoService, WebPageRawDataService webPageRawDataService) {
        this.webPageInfoService = webPageInfoService;
        this.webPageRawDataService = webPageRawDataService;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        // 获取网页数据
        String title = resultItems.get("title");
        String url = resultItems.get("url");
        String smartContent = resultItems.get("smartContent");
        String rawContent = resultItems.get("rawContent");
        // 当前时间
        LocalDateTime now = LocalDateTime.now();

        // 网页信息记录入库
        WebPageInfo webPageInfo = new WebPageInfo();
        webPageInfo.setTitle(title);
        webPageInfo.setUrl(url);
        webPageInfo.setSmartContent(smartContent);
        webPageInfo.setCreateTime(now);
        this.webPageInfoService.save(webPageInfo);

        // 保存成功后，为原始数据对象赋值
        WebPageRawData webPageRawData = new WebPageRawData();
        webPageRawData.setInfoId(webPageInfo.getId());
        webPageRawData.setContent(rawContent);
        webPageRawData.setCreateTime(now);
        this.webPageRawDataService.save(webPageRawData);
    }
}
