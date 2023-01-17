package com.bilibili.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xck
 */
@Data
public class History {
    private Long id;
    private Long userId;
    private Long withId;
    private LocalDateTime createTime;
}
