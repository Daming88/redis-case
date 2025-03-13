package com.hmdp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.hmdp.dto.Result;
import com.hmdp.mapper.UserRepository;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 校验手机号码
        if (RegexUtils.isPhoneInvalid(phone)){
            return Result.fail("手机号码格式错误");
        }
        String code = RandomUtil.randomNumbers(6);
        // 保存验证码到session
        session.setAttribute("code",code);
        // 发送验证码
        log.debug("发送短信验证码成功，验证码：{}",code);
        return Result.ok();
    }
}
