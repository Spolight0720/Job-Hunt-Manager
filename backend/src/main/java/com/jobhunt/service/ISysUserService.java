package com.jobhunt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jobhunt.dto.AuthReq;
import com.jobhunt.entity.SysUser;

public interface ISysUserService extends IService<SysUser> {
    void register(AuthReq req);
    String login(AuthReq req);
    void clearUserData(Long userId);
}
