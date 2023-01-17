package com.bilibili.pojo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xck
 */
@Data
public class Message {
    private Long id;
    private Long fromId;
    private Long toId;
    private String content;
    private LocalDateTime time;
    private Integer type;
    //private Integer isLast;
}
