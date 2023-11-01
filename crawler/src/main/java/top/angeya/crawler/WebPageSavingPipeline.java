package top.angeya.crawler;

import org.springframework.stereotype.Component;
import top.angeya.entity.WebPageInfo;
import top.angeya.entity.WebPageRawData;
import top.angeya.service.WebPageInfoService;
import top.angeya.service.WebPageRawDataService;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

/**
 * @author: angeya
 * @date: 2023/11/1 23:07
 * @description:
 */
@Component
public class WebPageSavingPipeline implements PageModelPipeline<WebPageInfo> {

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
    public void process(WebPageInfo webPageInfo, Task task) {
        // 网页信息记录入库
        this.webPageInfoService.save(webPageInfo);
        // 保存成功后，为原始数据对象赋值
        WebPageRawData webPageRawData = new WebPageRawData();
        webPageRawData.setInfoId(webPageInfo.getId());
        webPageRawData.setContent(webPageInfo.getId());
        webPageRawData.setCreateTime(webPageInfo.getCreateTime());
        // 原始数据入库
        this.webPageRawDataService.save(webPageRawData);
    }
}
