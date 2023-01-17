package com.bilibili.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xck
 */
@Data
public class Subscribe {
    private Long id;
    private Long userId;
    private Long subscribeId;
    private LocalDateTime createTime;
}
