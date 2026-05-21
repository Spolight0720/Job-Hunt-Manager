package com.jobhunt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jobhunt.common.Result;
import com.jobhunt.dto.JobApplicationReq;
import com.jobhunt.entity.JobApplication;
import com.jobhunt.exception.RepeatApplicationException;
import com.jobhunt.security.AuthContext;
import com.jobhunt.service.IJobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications")
public class JobApplicationController {

    @Autowired
    private IJobApplicationService jobApplicationService;

    @PostMapping
    public Result<Void> addApplication(@Validated @RequestBody JobApplicationReq req) {
        try {
            jobApplicationService.addApplication(req, AuthContext.getUserId());
            return Result.success();
        } catch (RepeatApplicationException e) {
            return Result.error(409, e.getMessage()); // 409 Conflict 警告重复
        } catch (Exception e) {
            return Result.error(400, "添加失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> reqMap) {
        try {
            Integer status = reqMap.get("status");
            if (status == null) {
                return Result.error(400, "缺少 status 参数");
            }
            jobApplicationService.updateStatus(id, status, AuthContext.getUserId());
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> updateApplication(@PathVariable Long id, @Validated @RequestBody JobApplicationReq req) {
        try {
            jobApplicationService.updateApplication(id, req, AuthContext.getUserId());
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteApplication(@PathVariable Long id) {
        try {
            jobApplicationService.deleteApplication(id, AuthContext.getUserId());
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping
    public Result<Page<JobApplication>> pageQuery(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<JobApplication> page = jobApplicationService.pageQuery(current, size, keyword, status, AuthContext.getUserId());
        return Result.success(page);
    }
}
