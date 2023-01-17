package com.bilibili.controller;

import com.bilibili.dto.MessageDTO;
import com.bilibili.service.MessageService;
import com.bilibili.vo.MessageVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/page")
    public MessageVO page(@RequestBody @Validated MessageDTO messageDTO) {
        return messageService.getMessage(messageDTO);
    }
}
