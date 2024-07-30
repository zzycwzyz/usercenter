package com.example.usercenter_backend.service;

import com.example.usercenter_backend.common.BaseResponse;
import com.example.usercenter_backend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {

    long userRegister(String userAccount,String userPassword,String checkPassword);

    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    int userLogout(HttpServletRequest request);
}
