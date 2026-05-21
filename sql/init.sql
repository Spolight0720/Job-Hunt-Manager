-- 数据库初始化脚本

CREATE DATABASE IF NOT EXISTS `job_hunt_manager` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `job_hunt_manager`;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
  `username` VARCHAR(64) NOT NULL COMMENT '登录用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账号创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '账号更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基本信息表';

-- 2. 岗位投递台账表
CREATE TABLE IF NOT EXISTS `job_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
  `company_name` VARCHAR(128) NOT NULL COMMENT '公司名称',
  `job_title` VARCHAR(128) NOT NULL COMMENT '岗位名称',
  `channel` VARCHAR(64) NOT NULL COMMENT '投递渠道 (如: Boss直聘, 牛客, 官网)',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '当前状态 (0:待投递, 1:已投递, 2:简历初筛, 3:笔试, 4:一面, 5:二面, 6:终面, 7:Offer, 8:落选/放弃)',
  `apply_time` DATETIME NOT NULL COMMENT '投递时间',
  `location` VARCHAR(128) DEFAULT NULL COMMENT '工作地点',
  `salary_range` VARCHAR(64) DEFAULT NULL COMMENT '薪资范围',
  `job_type` TINYINT DEFAULT 1 COMMENT '岗位类型 (0:实习, 1:校招, 2:社招)',
  `priority` TINYINT DEFAULT 1 COMMENT '优先级 (0:低, 1:中, 2:高)',
  `hr_contact` VARCHAR(255) DEFAULT NULL COMMENT 'HR联系方式',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志(0:正常, 1:删除)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_company_status` (`user_id`, `company_name`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位投递台账表';

-- 3. 面试及复盘记录表
CREATE TABLE IF NOT EXISTS `interview_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `application_id` BIGINT NOT NULL COMMENT '关联岗位投递表 ID',
  `record_type` TINYINT NOT NULL COMMENT '记录类型 (1:笔试, 2:面试)',
  `schedule_time` DATETIME NOT NULL COMMENT '日程时间 (笔试/面试开始时间)',
  `core_questions` TEXT DEFAULT NULL COMMENT '核心题目/面试提问',
  `summary` TEXT DEFAULT NULL COMMENT '复盘总结 (不足、后续优化)',
  `fail_reason` VARCHAR(255) DEFAULT NULL COMMENT '挂掉原因 (仅当该轮未通过时填写)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_application_id` (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试及复盘记录表';
