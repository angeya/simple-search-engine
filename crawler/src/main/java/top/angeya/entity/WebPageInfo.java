package top.angeya.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网页基本信息
 * @Author: wanganjie 5790
 * @Date: 2023/11/1 20:31
 * @Description:
 */
@Data
public class WebPageInfo {

    /**
     * 主键
     */
    private String id;

    /**
     * 网页标题
     */
    private String title;

    /**
     * 网页url 唯一索引
     */
    private String url;

    /**
     * 网页内容
     */
    private String webContent;

    /**
     * 智能文本内容
     */
    private String smartContent;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
