package com.bilibili.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xck
 */
@Data
public class Favorites {
    private Long id;

    private String name;

    private Long userId;

    private Integer isPublic;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
