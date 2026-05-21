package com.jobhunt.controller;

import com.jobhunt.common.Result;
import com.jobhunt.dto.UserInfoVO;
import com.jobhunt.entity.SysUser;
import com.jobhunt.security.AuthContext;
import com.jobhunt.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private ISysUserService sysUserService;

    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        Long userId = AuthContext.getUserId();
        SysUser sysUser = sysUserService.getById(userId);
        if (sysUser == null) {
            return Result.error(404, "当前用户不存在");
        }
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(sysUser, vo);
        return Result.success(vo);
    }

    @PostMapping("/clear-data")
    public Result<Void> clearUserData() {
        Long userId = AuthContext.getUserId();
        sysUserService.clearUserData(userId);
        return Result.success();
    }
}
