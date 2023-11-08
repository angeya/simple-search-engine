package top.angeya.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 未处理的地址
 *
 * @author: angeya
 * @date: 2023/11/8 23:46
 * @description:
 */
@Data
public class UnprocessedUrl {

    /**
     * id
     */
    private String id;

    /**
     * 网页地址
     */
    private String url;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
