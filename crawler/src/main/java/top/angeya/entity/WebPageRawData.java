package top.angeya.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网页原始数据
 *
 * @Author: wanganjie 5790
 * @Date: 2023/11/1 20:31
 * @Description:
 */
@Data
public class WebPageRawData {

    /**
     * 主键
     */
    private String id;

    /**
     * 网页信息id
     */
    private String infoId;

    /**
     * 网页原始内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
