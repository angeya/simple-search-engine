package top.angeya.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.angeya.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanganjie 5790
 * @since 2023/10/13 16:02
 */
//@SpringBootTest
class UserServiceTest {

    @Autowired
    private CommonWebDataService commonWebDataService;

    @Autowired
    private UserService userService;

    @Test
    public void testList() {
        List<User> userList = userService.list();
        userList.forEach(System.out::println);
    }

    @Test
    public void testListByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张");
        List<User> userList = userService.listByMap(map);
        userList.forEach(System.out::println);
    }

    @Test
    public void testPage() {
        Page<User> page = new Page<>(1, 3);
        Page<User> page2 = userService.page(page, Wrappers.lambdaQuery(User.class).like(User::getId, "1"));
        System.out.println(page2);
    }
}