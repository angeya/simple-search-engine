package top.angeya.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

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
    @TableId
    private String code;

    /**
     * 数值
     */
    private Long value;

    /**
     * 文本值
     */
    private String textValue;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
