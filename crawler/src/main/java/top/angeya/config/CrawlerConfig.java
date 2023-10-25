package top.angeya.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author: wanganjie 5790
 * @Date: 2023/10/19 20:14
 * @Description:
 */
@ConfigurationProperties("crawler.base")
@Configuration
@Data
public class CrawlerConfig {

    /**
     * 线程数量
     */
    private Integer threadCount;

    /**
     * 基础页面列表
     */
    private List<String> webList;
}
