package com.liutong.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Getter
@Setter
@TableName("sys_user")
@ApiModel(value = "User对象", description = "用户信息表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @ApiModelProperty("用户名/账号")
    @TableField("username")
    private String username;

    @ApiModelProperty("加密后的密码")
    @TableField("password")
    private String password;

    @ApiModelProperty("昵称")
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty("角色: STUDENT-学生, ADMIN-管理员")
    @TableField("role")
    private String role;

    @ApiModelProperty("邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty("头像URL")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty("注册时间")
    @TableField("create_time")
    private LocalDateTime createTime;
}
