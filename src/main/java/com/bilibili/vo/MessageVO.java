package com.bilibili.vo;

import com.bilibili.pojo.Message;
import com.bilibili.pojo.User;
import lombok.Data;

import java.util.List;

/**
 * @author xck
 */
@Data
public class MessageVO {
    private User user;
    private List<Message> message;
}
