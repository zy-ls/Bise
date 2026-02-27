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
 * 私信消息表
 * </p>
 *
 * @author LiuTong
 * @since 2025-12-29
 */
@Getter
@Setter
@TableName("chat_message")
@ApiModel(value = "ChatMessage对象", description = "私信消息表")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "msg_id", type = IdType.AUTO)
    private Long msgId;

    @ApiModelProperty("发送者ID")
    @TableField("sender_id")
    private Long senderId;

    @ApiModelProperty("接收者ID")
    @TableField("receiver_id")
    private Long receiverId;

    @ApiModelProperty("消息内容")
    @TableField("content")
    private String content;

    @ApiModelProperty("是否已读: 0-未读, 1-已读")
    @TableField("is_read")
    private Byte isRead;

    @TableField("create_time")
    private LocalDateTime createTime;
}
