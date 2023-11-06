package top.angeya.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件下载地址
 *
 * @author: angeya
 * @date: 2023/11/6 23:14
 * @description:
 */
@Data
public class FileUrl {

    /**
     * 主键
     */
    private String id;

    /**
     * 地址
     */
    private String url;

    /**
     * 文件类型 一般为文件后缀
     */
    private String fileType;

    /**
     * 创建时间
     */
    LocalDateTime createTime;

}
