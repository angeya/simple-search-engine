package top.angeya.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author wanganjie 5790
 * @since 2023/10/13 15:24
 */
@TableName("user")
@Data
public class User  {

    public User() {
    }

    public User(String id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    private String id;

    private String name;

    private Integer age;

    private String city;

    private String uniId;
}
