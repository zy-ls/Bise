package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_sensitive_word")
public class SensitiveWord {
    @TableId
    private Long wordId;
    private String word;
}