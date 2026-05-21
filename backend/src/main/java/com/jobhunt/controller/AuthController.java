package com.jobhunt.controller;

import com.jobhunt.common.Result;
import com.jobhunt.dto.AuthReq;
import com.jobhunt.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private ISysUserService sysUserService;

    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody AuthReq req) {
        try {
            sysUserService.register(req);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@Validated @RequestBody AuthReq req) {
        try {
            String token = sysUserService.login(req);
            Map<String, String> data = new HashMap<>();
            data.put("token", "Bearer " + token);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(401, e.getMessage());
        }
    }
}
