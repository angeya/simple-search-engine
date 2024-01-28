package top.angeya.crawler.scheduler;

import top.angeya.pojo.entity.UrlInfo;

import java.util.List;

/**
 * URL管理器
 * @Author: angeya
 * @Date: 2023/12/28 17:16
 * @Description:
 */
public interface UrlScheduler {

    /**
     * 弹出url
     * @return url
     */
    UrlInfo pop();

    /**
     * 推入url
     * @param urlInfo 链接
     */
    void push(UrlInfo urlInfo);

    /**
     * 推入url
     * @param urlList 链接列表
     */
    void push(List<String> urlList);

}
