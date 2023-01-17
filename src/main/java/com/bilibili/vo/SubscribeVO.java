package com.bilibili.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bilibili.pojo.Subscribe;
import com.bilibili.pojo.User;
import lombok.Data;

import java.util.List;

/**
 * @author xck
 */
@Data
public class SubscribeVO {
    private List<User> users;

    private Page<Subscribe> page;
    private Boolean isSubscribe;
    private Integer subscribeNum;
    private Integer fanNum;

    public SubscribeVO(){

    }

    public SubscribeVO(List<User> users, Page<Subscribe> page) {
        this.users = users;
        this.page = page;
    }

    public SubscribeVO(List<User> users) {
        this.users = users;
    }
}
