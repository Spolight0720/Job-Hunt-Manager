package com.jobhunt.controller;

import com.jobhunt.common.Result;
import com.jobhunt.dto.InterviewRecordReq;
import com.jobhunt.entity.InterviewRecord;
import com.jobhunt.security.AuthContext;
import com.jobhunt.service.IInterviewRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interviews")
public class InterviewRecordController {

    @Autowired
    private IInterviewRecordService interviewRecordService;

    @PostMapping
    public Result<Void> addRecord(@Validated @RequestBody InterviewRecordReq req) {
        try {
            interviewRecordService.addRecord(req, AuthContext.getUserId());
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/application/{appId}")
    public Result<List<InterviewRecord>> getRecords(@PathVariable Long appId) {
        try {
            List<InterviewRecord> list = interviewRecordService.getRecordsByAppId(appId, AuthContext.getUserId());
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(403, e.getMessage());
        }
    }
}
