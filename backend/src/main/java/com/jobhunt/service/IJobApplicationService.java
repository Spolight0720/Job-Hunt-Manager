package com.jobhunt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jobhunt.dto.JobApplicationReq;
import com.jobhunt.entity.JobApplication;

public interface IJobApplicationService extends IService<JobApplication> {
    
    /**
     * 新增岗位投递（含同岗位 90 天去重校验）
     */
    void addApplication(JobApplicationReq req, Long userId);
    
    /**
     * 更新状态
     */
    void updateStatus(Long id, Integer status, Long userId);

    /**
     * 更新台账信息
     */
    void updateApplication(Long id, JobApplicationReq req, Long userId);

    /**
     * 软删除台账
     */
    void deleteApplication(Long id, Long userId);

    /**
     * 分页查询台账（带权隔离）
     */
    Page<JobApplication> pageQuery(Long current, Long size, String keyword, Integer status, Long userId);
}
