package com.bilibili.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xck
 */
@Data
public class Favorite {
    private Long id;

    private Long favoritesId;

    private Long dynamicId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
