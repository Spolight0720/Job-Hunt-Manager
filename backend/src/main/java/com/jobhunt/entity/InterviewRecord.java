package com.jobhunt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("interview_record")
public class InterviewRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long applicationId;
    private Integer recordType;
    private LocalDateTime scheduleTime;
    private String coreQuestions;
    private String summary;
    private String failReason;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
