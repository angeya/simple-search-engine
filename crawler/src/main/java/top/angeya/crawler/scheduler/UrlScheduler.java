package top.angeya.crawler.scheduler;

import top.angeya.pojo.entity.Url;

import java.util.List;

/**
 * @Author: angeya
 * @Date: 2023/12/28 17:16
 * @Description:
 */
public interface UrlScheduler {

    /**
     * 弹出url
     * @return url
     */
    Url pop();

    /**
     * 推入url
     * @param url 链接
     */
    void push(Url url);

    /**
     * 推入url
     * @param urlList 链接列表
     */
    default void push(List<String> urlList) {
        for (String s : urlList) {
            this.push(new Url(s));
        }
    }

}
