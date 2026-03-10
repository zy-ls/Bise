package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("study_group")
public class StudyGroup {
    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;
    private String name;
    private String description;
    private String cover;
    private Long creatorId;
    private Date createTime;
}