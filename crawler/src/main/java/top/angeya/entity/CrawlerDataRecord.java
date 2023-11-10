package top.angeya.entity;

import lombok.Data;

/**
 * 爬虫信息记录
 *
 * @author: angeya
 * @date: 2023/11/10 23:12
 * @description:
 */
@Data
public class CrawlerDataRecord {

    public CrawlerDataRecord() {
    }

    public CrawlerDataRecord(String code) {
        this.code = code;
    }

    /**
     * 编码
     */
    private String code;

    /**
     * 数值
     */
    private Long value;

    /**
     * 文本值
     */
    private String textValue;

}
