package com.bilibili.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bilibili.pojo.Dynamic;
import com.bilibili.pojo.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author xck
 */
@Data
public class DynamicVO {

    private Dynamic dynamic;

    private User user;

    private Page<Dynamic> page;

    private Integer dynamicNum;

    private Integer collectNum;

    private Integer playNum;

    private LocalDateTime collectTime;
}
