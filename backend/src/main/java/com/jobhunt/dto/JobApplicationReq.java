package com.jobhunt.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class JobApplicationReq {
    @NotBlank(message = "公司名称不能为空")
    private String companyName;

    @NotBlank(message = "岗位名称不能为空")
    private String jobTitle;

    @NotBlank(message = "投递渠道不能为空")
    private String channel;

    @NotNull(message = "当前状态不能为空")
    private Integer status;

    @NotNull(message = "投递时间不能为空")
    private LocalDateTime applyTime;

    private String location;
    private String salaryRange;
    private Integer jobType;
    private Integer priority;
    private String hrContact;
}
