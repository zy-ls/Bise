package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("group_member")
public class GroupMember {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long groupId;
    private Long userId;
    /**
     * 角色: 0-普通成员, 1-群主
     */
    private Integer role;
    private Date joinTime;
}