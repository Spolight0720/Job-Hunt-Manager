package com.jobhunt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("job_application")
public class JobApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String companyName;
    private String jobTitle;
    private String channel;
    private Integer status;
    private LocalDateTime applyTime;
    private String location;
    private String salaryRange;
    private Integer jobType;
    private Integer priority;
    private String hrContact;
    
    @TableLogic
    private Integer isDeleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
