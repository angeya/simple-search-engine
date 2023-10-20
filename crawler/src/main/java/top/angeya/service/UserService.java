package top.angeya.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.angeya.dao.UserMapper;
import top.angeya.entity.User;

/**
 * @author wanganjie 5790
 * @since 2023/10/13 16:00
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Autowired
    private UserMapper userMapper;

}
