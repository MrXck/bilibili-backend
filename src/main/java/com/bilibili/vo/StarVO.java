package com.bilibili.vo;

import com.bilibili.pojo.Dynamic;
import com.bilibili.pojo.DynamicComment;
import com.bilibili.pojo.Star;
import com.bilibili.pojo.User;
import lombok.Data;

/**
 * @author xck
 */
@Data
public class StarVO {
    private Integer starNum;
    private Boolean isStar;

    private DynamicComment comment;
    private Dynamic dynamic;
    private User user;
    private Star star;
}
