package top.angeya.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * @Author: wanganjie 5790
 * @Date: 2023/10/19 19:52
 * @Description: 网页信息
 */
@Data
public class CommonWebData {

    /**
     * 主键 自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 文本内容
     */
    private String smartContent;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
