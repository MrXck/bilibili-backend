package com.bilibili.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xck
 */
@Data
public class Dynamic {
    private Long id;
    private String content;
    private Long userId;
    private LocalDateTime createTime;
    private String title;
    private String introduce;
    private Integer type;
    private String src;
    private String coverSrc;
}
