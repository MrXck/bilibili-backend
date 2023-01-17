package com.bilibili.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xck
 */
@Data
public class DynamicComment {
    private Long id;
    private String content;
    private Long rootId;
    private Long replyId;
    private Long userId;
    private LocalDateTime createTime;
    private Long dynamicId;
    private Long toUserId;
}
