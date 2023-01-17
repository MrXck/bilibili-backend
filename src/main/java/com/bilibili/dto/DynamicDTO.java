package com.bilibili.dto;

import lombok.Data;

@Data
public class DynamicDTO {

    private Long userId;
    private Long dynamicId;

    private Integer pageNum;
    private Integer pageSize;
    private String keyword;
    private Integer type;
}
