package com.bilibili.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author xck
 */
@Data
public class User {

    private Long id;
    private String username;

    @TableField(select = false)
    private String password;
    private String avatar;

    @TableField(select = false)
    private String phone;
    private String nickname;
    private String signature;
    private Integer sex;

    @TableField(select = false)
    private LocalDateTime createTime;
    @TableField(select = false)
    private LocalDateTime updateTime;
}
