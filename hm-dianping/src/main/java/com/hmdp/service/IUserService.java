package com.hmdp.service;

import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import jakarta.servlet.http.HttpSession;


public interface IUserService {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);
}
