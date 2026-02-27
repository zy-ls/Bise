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
 * 内容审核记录表
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Getter
@Setter
@TableName("audit_log")
@ApiModel(value = "AuditLog对象", description = "内容审核记录表")
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @ApiModelProperty("审核对象ID(笔记ID)")
    @TableField("target_id")
    private Long targetId;

    @ApiModelProperty("审核员ID(管理员)")
    @TableField("auditor_id")
    private Long auditorId;

    @ApiModelProperty("结果: 1-通过, 0-驳回")
    @TableField("audit_result")
    private Byte auditResult;

    @ApiModelProperty("驳回原因")
    @TableField("reject_reason")
    private String rejectReason;

    @TableField("audit_time")
    private LocalDateTime auditTime;
}
