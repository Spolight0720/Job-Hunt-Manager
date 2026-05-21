package com.jobhunt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jobhunt.dto.InterviewRecordReq;
import com.jobhunt.entity.InterviewRecord;
import com.jobhunt.entity.JobApplication;
import com.jobhunt.mapper.InterviewRecordMapper;
import com.jobhunt.mapper.JobApplicationMapper;
import com.jobhunt.service.IInterviewRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewRecordServiceImpl extends ServiceImpl<InterviewRecordMapper, InterviewRecord> implements IInterviewRecordService {

    @Autowired
    private JobApplicationMapper jobApplicationMapper;

    @Override
    public void addRecord(InterviewRecordReq req, Long userId) {
        // 先校验对应的岗位台账是否属于该用户
        Long count = jobApplicationMapper.selectCount(
            new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getId, req.getApplicationId())
                .eq(JobApplication::getUserId, userId)
        );
        if (count == 0) {
            throw new RuntimeException("非法操作，未找到该岗位记录或无权限操作");
        }

        InterviewRecord record = new InterviewRecord();
        BeanUtils.copyProperties(req, record);
        this.baseMapper.insert(record);
    }

    @Override
    public List<InterviewRecord> getRecordsByAppId(Long appId, Long userId) {
        // 同样要先校验数据所有权，防止水平越权查看他人的面试复盘数据
        Long count = jobApplicationMapper.selectCount(
            new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getId, appId)
                .eq(JobApplication::getUserId, userId)
        );
        if (count == 0) {
            throw new RuntimeException("查询拒绝，此岗位无法访问");
        }
        
        return this.baseMapper.selectList(
            new LambdaQueryWrapper<InterviewRecord>()
                .eq(InterviewRecord::getApplicationId, appId)
                .orderByAsc(InterviewRecord::getScheduleTime) // 按照发生时间正序返回轨迹
        );
    }
}
