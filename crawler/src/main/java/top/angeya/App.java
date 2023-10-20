package top.angeya;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: angeya
 * @Date: 2023/9/2 12:51
 * @Description: 启动类
 */
@SpringBootApplication
@MapperScan("top.angeya.dao")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("项目启动成功");
    }
}
