package com.bilibili.vo;

import com.bilibili.pojo.Dynamic;
import com.bilibili.pojo.Later;
import com.bilibili.pojo.User;
import lombok.Data;

/**
 * @author xck
 */
@Data
public class LaterVO {
    private Later later;
    private Dynamic dynamic;
    private User user;
    private Boolean isPlay;
}
