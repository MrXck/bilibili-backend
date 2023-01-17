package com.bilibili.vo;

import com.bilibili.pojo.Dynamic;
import com.bilibili.pojo.Play;
import com.bilibili.pojo.User;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xck
 */
@Data
public class PlayVO {

    private Dynamic dynamic;
    private LocalDateTime playTime;
    private User user;
    private Play play;
    private Integer playNum;
}
