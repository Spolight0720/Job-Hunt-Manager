package com.jobhunt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jobhunt.dto.InterviewRecordReq;
import com.jobhunt.entity.InterviewRecord;

import java.util.List;

public interface IInterviewRecordService extends IService<InterviewRecord> {
    
    /**
     * 新增面试/笔试记录与日程安排并关联具体的台账（带鉴权）
     */
    void addRecord(InterviewRecordReq req, Long userId);
    
    /**
     * 获取指定岗位下的所有流转节点面试记录
     */
    List<InterviewRecord> getRecordsByAppId(Long appId, Long userId);
}
