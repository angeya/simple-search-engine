package top.angeya.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: angeya
 * @date: 2023/11/10 21:59
 * @description:
 */
@Data
public class UrlInfo {

    public UrlInfo() {
    }

    public UrlInfo(String url) {
        this.url = url;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 主键 自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * url内容
     */
    private String url;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
