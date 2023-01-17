package com.bilibili.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bilibili.pojo.Dynamic;
import com.bilibili.pojo.Favorite;
import com.bilibili.pojo.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xck
 */
@Data
public class FavoriteVO {

    private Page<Favorite> page;

    private Dynamic dynamic;

    private Integer collectNum;

    private User user;

    private Integer playNum;

    private LocalDateTime collectTime;

    private Boolean isCollect;

    private Long collectId;

    private List<Long> favoritesIds;
}
