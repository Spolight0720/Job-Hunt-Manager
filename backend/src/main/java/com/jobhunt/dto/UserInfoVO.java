package com.jobhunt.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private LocalDateTime createTime;
}
