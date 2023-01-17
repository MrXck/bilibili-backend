package com.bilibili.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xck
 */
@Data
public class Star {
    private Long id;
    private Long dynamicId;
    private Long commentId;
    private Long userId;
    private LocalDateTime createTime;
    private Long isStaredUserId;
}
