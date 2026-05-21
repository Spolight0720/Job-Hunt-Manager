package com.jobhunt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobhunt.entity.JobApplication;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobApplicationMapper extends BaseMapper<JobApplication> {
}
