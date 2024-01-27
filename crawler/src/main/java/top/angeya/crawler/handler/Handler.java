package top.angeya.crawler.handler;

import top.angeya.pojo.web.HtmlPage;

/**
 * @Author: angeya
 * @Date: 2023/12/28 18:50
 * @Description:
 */
public interface Handler {

    /**
     * 处理网页
     * @param htmlPage 网页信息
     */
    void process(HtmlPage htmlPage);

}
