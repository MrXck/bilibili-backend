package com.bilibili.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xck
 */
@Data
public class Play {
    private Long id;
    private Long dynamicId;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDelete;
}
