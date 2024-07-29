package com.example.usercenter_backend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.usercenter_backend.common.ErrorCode;
import com.example.usercenter_backend.constant.UserConstant;
import com.example.usercenter_backend.exception.BusinessException;
import com.example.usercenter_backend.model.domain.User;
import com.example.usercenter_backend.model.request.UserLoginRequest;
import com.example.usercenter_backend.model.request.UserRegisterRequest;
import com.example.usercenter_backend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.usercenter_backend.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = request.getUserAccount();
        String userPassword = request.getUserPassword();
        String checkPassword = request.getCheckPassword();
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            return null;
        }
        return userService.userLogin(userAccount, userPassword, request);
    }

    @GetMapping("/current")
    public User geCurrentUser(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if(user == null || (user.getId() <= 0)){
            return null;
        }
        User ret = userService.getById(user.getId());
        ret.setUserPassword("");
        return ret;
    }

    @GetMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        // 脱敏
        return userList.stream().map(user -> {
            user.setUserPassword("");
            return user;
        }).collect(Collectors.toList());
    }

    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (id <= 0 || !isAdmin(request)) {
            return false;
        }
        return userService.removeById(id);
    }

    /**
     * 判断是否是管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    @GetMapping("/logout")
    public Integer userLogout(HttpServletRequest request){
        if(request == null) return null;
        return userService.userLogout(request);
    }
}
