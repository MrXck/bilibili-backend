package com.bilibili.dto;

import lombok.Data;

/**
 * @author xck
 */
@Data
public class FavoriteDTO {
    private Long favoritesId;
    private Long dynamicId;

    private Integer pageSize;

    private Integer pageNum;

    private Long[] favorites;
}
