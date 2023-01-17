package com.bilibili.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xck
 */
@Data
public class Barrage {
    private Long id;
    private String content;
    private Long userId;
    private Long dynamicId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer time;
}
