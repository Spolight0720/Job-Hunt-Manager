package com.jobhunt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jobhunt.dto.JobApplicationReq;
import com.jobhunt.entity.JobApplication;
import com.jobhunt.exception.RepeatApplicationException;
import com.jobhunt.mapper.JobApplicationMapper;
import com.jobhunt.service.IJobApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class JobApplicationServiceImpl extends ServiceImpl<JobApplicationMapper, JobApplication> implements IJobApplicationService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addApplication(JobApplicationReq req, Long userId) {
        // 核心技术要求的 "90天内防重复投递" 校验
        LocalDateTime thresholdTime = req.getApplyTime().minusDays(90);
        
        Long duplicateCount = this.baseMapper.selectCount(
                new LambdaQueryWrapper<JobApplication>()
                        .eq(JobApplication::getUserId, userId)
                        .eq(JobApplication::getCompanyName, req.getCompanyName())
                        .eq(JobApplication::getJobTitle, req.getJobTitle())
                        .ge(JobApplication::getApplyTime, thresholdTime)
        );

        if (duplicateCount > 0) {
            throw new RepeatApplicationException("系统检测到 90 天内该岗位的重复投递记录，请核实！");
        }

        JobApplication jobApp = new JobApplication();
        BeanUtils.copyProperties(req, jobApp);
        jobApp.setUserId(userId);
        
        this.baseMapper.insert(jobApp);
    }

    @Override
    public void updateStatus(Long id, Integer status, Long userId) {
        JobApplication jobApp = this.baseMapper.selectOne(
            new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getId, id)
                .eq(JobApplication::getUserId, userId)
        );
        if (jobApp == null) {
            throw new RuntimeException("台账记录不存在或无权限修改");
        }
        jobApp.setStatus(status);
        this.baseMapper.updateById(jobApp);
    }

    @Override
    public void updateApplication(Long id, JobApplicationReq req, Long userId) {
        JobApplication jobApp = this.baseMapper.selectOne(
            new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getId, id)
                .eq(JobApplication::getUserId, userId)
        );
        if (jobApp == null) {
            throw new RuntimeException("台账记录不存在或无权限修改");
        }
        BeanUtils.copyProperties(req, jobApp);
        // 防御性硬编码，防止篡改为他人的userId
        jobApp.setId(id);
        jobApp.setUserId(userId);
        this.baseMapper.updateById(jobApp);
    }

    @Override
    public void deleteApplication(Long id, Long userId) {
        int rows = this.baseMapper.delete(
            new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getId, id)
                .eq(JobApplication::getUserId, userId)
        );
        if (rows == 0) {
            throw new RuntimeException("删除失败：记录不存在或无权限");
        }
    }

    @Override
    public Page<JobApplication> pageQuery(Long current, Long size, String keyword, Integer status, Long userId) {
        Page<JobApplication> page = new Page<>(current, size);
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<JobApplication>()
            .eq(JobApplication::getUserId, userId)
            .eq(status != null, JobApplication::getStatus, status)
            .and(keyword != null && !keyword.isEmpty(), w -> w
                .like(JobApplication::getCompanyName, keyword)
                .or()
                .like(JobApplication::getJobTitle, keyword)
            )
            .orderByDesc(JobApplication::getUpdateTime); // 根据更新时间倒序
        
        return this.baseMapper.selectPage(page, wrapper);
    }
}
