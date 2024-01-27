package top.angeya.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * url队列存储类型
 *
 * @Author: angeya
 * @Date: 2023/11/9 15:21
 * @Description:
 */
@AllArgsConstructor
@Getter
public enum UrlQueueStorageType {

    /**
     * 本地文件
     */
    FILE("file"),

    /**
     * mysql
     */
    MYSQL("mysql"),

    /**
     * redis
     */
    REDIS("redis");

    /**
     * 编码
     */
    private final String code;

}
