package com.jobhunt.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class InterviewRecordReq {
    @NotNull(message = "关联岗位ID不能为空")
    private Long applicationId;
    
    @NotNull(message = "记录类型不能为空")
    private Integer recordType;
    
    @NotNull(message = "日程时间不能为空")
    private LocalDateTime scheduleTime;
    
    private String coreQuestions;
    private String summary;
    private String failReason;
}
