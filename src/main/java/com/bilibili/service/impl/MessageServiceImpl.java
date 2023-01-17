package com.bilibili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibili.dto.MessageDTO;
import com.bilibili.mapper.MessageMapper;
import com.bilibili.mapper.UserMapper;
import com.bilibili.pojo.Message;
import com.bilibili.service.MessageService;
import com.bilibili.utils.UserThreadLocal;
import com.bilibili.vo.MessageVO;
import org.springframework.stereotype.Service;

/**
 * @author xck
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final UserMapper userMapper;

    public MessageServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public MessageVO getMessage(MessageDTO messageDTO) {

        Long userId = UserThreadLocal.get();
        Long withId = messageDTO.getWithId();
        Long id = messageDTO.getId();

        MessageVO messageVO = new MessageVO();
        messageVO.setUser(userMapper.selectById(withId));

        if (id != null) {
            messageVO.setMessage(this.baseMapper.selectByPage(id, userId, withId, messageDTO.getPageSize()));
        } else {
            LambdaQueryWrapper<Message> messageLambdaQueryWrapper = new LambdaQueryWrapper<>();
            messageLambdaQueryWrapper
                    .and(i -> i.eq(Message::getFromId, userId).eq(Message::getToId, withId))
                    .or(i -> i.eq(Message::getFromId, withId).eq(Message::getToId, userId));
            Page<Message> page = new Page<>(1, messageDTO.getPageSize());
            messageLambdaQueryWrapper.orderByDesc(Message::getId);
            Page<Message> page1 = this.page(page, messageLambdaQueryWrapper);
            messageVO.setMessage(page1.getRecords());
        }

        return messageVO;
    }
}