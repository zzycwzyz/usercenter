package com.example.usercenter_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenter_backend.constant.UserConstant;
import com.example.usercenter_backend.model.domain.User;
import com.example.usercenter_backend.service.UserService;
import com.example.usercenter_backend.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    @Resource
    private UserMapper userMapper;

    private static final String SALT = "ttk";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1.校验
        if(StringUtils.isAllBlank(userAccount,userPassword,checkPassword)){
            return -1;
        }
        if(userAccount.length() < 4){
            return -1;
        }
        if(userPassword.length() < 8 || checkPassword.length() < 8){
            return -1;
        }
        if(!userPassword.equals(checkPassword)){
            return -1;
        }
        // 用户名不能包含特殊字符。使用正则表达式：允许字母、数字和下划线，不允许其他特殊字符
        final String USERACCOUNT_PATTERN = "[^a-zA-Z0-9_]";
        Pattern pattern = Pattern.compile(USERACCOUNT_PATTERN);
        Matcher matcher = pattern.matcher(userAccount);
        if(matcher.find()){
            return -1;
        }
        // 判断账户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            return -1;
        }
        // 2.加密
        String digestPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3.插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(digestPassword);
        boolean save = this.save(user);
        // 这里方法返回long，判断一下防止返回null拆箱失败
        if(!save){
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        if(StringUtils.isAllBlank(userAccount,userPassword)){
            return null;
        }
        if(userAccount.length() < 4){
            return null;
        }
        if(userPassword.length() < 8){
            return null;
        }
        // 用户名不能包含特殊字符。使用正则表达式：允许字母、数字和下划线，不允许其他特殊字符
        final String USERACCOUNT_PATTERN = "[^a-zA-Z0-9_]";
        Pattern pattern = Pattern.compile(USERACCOUNT_PATTERN);
        Matcher matcher = pattern.matcher(userAccount);
        if(matcher.find()){
            return null;
        }
        // 2.查询数据库
        String digestPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", digestPassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            log.info("User login failed, userAccount cannot match userPassword.");
            return null;
        }
        // 3.记录登录态
        user.setUserPassword("");
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return user;
    }
}




