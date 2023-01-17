package com.bilibili.vo;

import com.bilibili.pojo.Barrage;
import lombok.Data;

/**
 * @author xck
 */
@Data
public class BarrageVO {
    private Barrage barrage;
    private Boolean isMe;
    private Integer barrageNum;
}
