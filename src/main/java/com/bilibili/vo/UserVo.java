package com.bilibili.vo;


import com.bilibili.pojo.User;
import lombok.Data;

/**
 * @author xck
 */
@Data
public class UserVo {

    private User user;

    private Integer subscribeNum;

    private Integer fanNum;

    private String token;
}
