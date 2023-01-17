package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.dto.MessageDTO;
import com.bilibili.pojo.Message;
import com.bilibili.vo.MessageVO;

/**
 * @author xck
 */
public interface MessageService extends IService<Message> {
    /**
     * 分页获取消息
     * @param messageDTO 页大小 双方用户id
     * @return 消息
     */
    MessageVO getMessage(MessageDTO messageDTO);
}