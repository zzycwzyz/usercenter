package com.example.usercenter_backend.mapper;

import com.example.usercenter_backend.model.domain.User;
import com.example.usercenter_backend.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    @Resource
    private UserService userService;

    @Test
    void insert() {
        User user = new User();
        user.setUsername("张三");
        user.setUserAccount("zhangsan");
        user.setAvatarUrl("https://zyz240328-1315421160.cos.ap-nanjing.myqcloud.com/%E9%80%9A%E7%94%A8/%E5%B0%8F%E9%BB%91%E5%AD%90.png");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("18334912428");
        user.setEmail("2657232526@qq.com");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);
    }

    @Test
    void userRegister(){
        String userAccount = "lisi";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        assertNotEquals(-1, result);
    }

    @Test
    void test(){
    }
}