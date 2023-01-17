package com.bilibili.dto;

import lombok.Data;

/**
 * @author xck
 */
@Data
public class PlayDTO {

    private Integer pageSize;
    private Integer pageNum;
    private Long playId;
    private Long dynamicId;
}
