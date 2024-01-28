package top.angeya.crawler.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.angeya.constant.CommonConstant;
import top.angeya.pojo.entity.FileUrl;
import top.angeya.pojo.entity.WebPageInfo;
import top.angeya.pojo.entity.WebPageRawData;
import top.angeya.pojo.web.HtmlPage;
import top.angeya.service.FileUrlService;
import top.angeya.service.WebPageInfoService;
import top.angeya.service.WebPageRawDataService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @Author: angeya
 * @Date: 2023/12/28 18:53
 * @Description:
 */
@Slf4j
@Component
public class SimplePageHandler implements Handler {

    /**
     * 网页信息服务
     */
    private final WebPageInfoService webPageInfoService;

    /**
     * 网页原始数据服务
     */
    private final WebPageRawDataService webPageRawDataService;

    /**
     * 文件地址服务
     */
    private final FileUrlService fileUrlService;

    /**
     * 文件后缀
     */
    @Value("${crawler.file-extensions:txt,csv,doc,docx,pdf,odt,xls,xlsx,ppt,pptx,jpg,jpeg,png,gif,bmp,mp3,wav,mp4,avi,zip}")
    private String fileExtensions;

    /**
     * 文件后缀，用于匹配文件下载的url
     */
    private final Set<String> fileExtensionSet = new HashSet<>();

    @PostConstruct
    private void init() {
        // 初始化文件后缀
        String[] extensions = this.fileExtensions.split(CommonConstant.SIMPLE_SEPARATOR);
        this.fileExtensionSet.addAll(Arrays.asList(extensions));
        log.info("file extensions are: {}", fileExtensionSet);
    }


    public SimplePageHandler(WebPageInfoService webPageInfoService, WebPageRawDataService webPageRawDataService,
                             FileUrlService fileUrlService) {
        this.webPageInfoService = webPageInfoService;
        this.webPageRawDataService = webPageRawDataService;
        this.fileUrlService = fileUrlService;
    }

    @Override
    public void process(HtmlPage htmlPage) {
        String title = htmlPage.getTitle();
        String url = htmlPage.getUrl();
        String smartContent = htmlPage.getText();
        String rawContent = htmlPage.getText();

        LocalDateTime now = LocalDateTime.now();
        // 判断url是否为文件地址，如果是则保存到文件地址表
        Optional<String> fileExtensionOption = this.isFileUrl(url);
        if (fileExtensionOption.isPresent()) {
            // 文件地址title一般为空，content一般为乱码
            FileUrl fileUrl = new FileUrl();
            fileUrl.setUrl(url);
            fileUrl.setFileType(fileExtensionOption.get());
            fileUrl.setCreateTime(now);
            this.fileUrlService.save(fileUrl);
            return;
        }

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


    /**
     * 判断url是不是文件下载地址
     *
     * @param url url
     * @return 判断结果
     */
    private Optional<String> isFileUrl(String url) {
        int extIndex = url.lastIndexOf(".");
        if (extIndex < 0) {
            return Optional.empty();
        }
        String extension = url.substring(extIndex + 1).toLowerCase();
        if (this.fileExtensionSet.contains(extension)) {
            log.info("{} is a file url, file type is: {}", url, extension);
            return Optional.of(extension);
        }
        return Optional.empty();
    }
}