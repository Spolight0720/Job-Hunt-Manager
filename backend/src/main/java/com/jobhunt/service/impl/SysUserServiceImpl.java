package com.jobhunt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jobhunt.dto.AuthReq;
import com.jobhunt.entity.SysUser;
import com.jobhunt.mapper.SysUserMapper;
import com.jobhunt.security.JwtUtils;
import com.jobhunt.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobhunt.mapper.JobApplicationMapper;
import com.jobhunt.mapper.InterviewRecordMapper;
import com.jobhunt.entity.JobApplication;
import com.jobhunt.entity.InterviewRecord;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JobApplicationMapper jobApplicationMapper;

    @Autowired
    private InterviewRecordMapper interviewRecordMapper;

    @Override
    public void register(AuthReq req) {
        // 校验用户名是否已存在
        Long count = this.baseMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, req.getUsername())
        );
        if (count > 0) {
            throw new RuntimeException("用户名已被注册");
        }

        SysUser sysUser = new SysUser();
        sysUser.setUsername(req.getUsername());
        // 使用 BCrypt 加密密码
        sysUser.setPassword(passwordEncoder.encode(req.getPassword()));

        this.baseMapper.insert(sysUser);
    }

    @Override
    public String login(AuthReq req) {
        SysUser sysUser = this.baseMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, req.getUsername())
        );

        if (sysUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 校验密码
        if (!passwordEncoder.matches(req.getPassword(), sysUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成 JWT Token 并返回
        return jwtUtils.generateToken(sysUser.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearUserData(Long userId) {
        // 清除面试复盘记录 (需要通过 application_id 关联找到需要删除的面试记录，或者直接用子查询)
        interviewRecordMapper.delete(
            new LambdaQueryWrapper<InterviewRecord>()
                .inSql(InterviewRecord::getApplicationId, "SELECT id FROM job_application WHERE user_id = " + userId)
        );
        // 清除岗位投递记录
        jobApplicationMapper.delete(
            new LambdaQueryWrapper<JobApplication>().eq(JobApplication::getUserId, userId)
        );
    }
}
