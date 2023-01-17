package com.bilibili.controller;

import com.bilibili.dto.SubscribeDTO;
import com.bilibili.service.SubscribeService;
import com.bilibili.vo.SubscribeVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscribe")
public class SubscribeController {

    private final SubscribeService subscribeService;

    public SubscribeController(SubscribeService subscribeService) {
        this.subscribeService = subscribeService;
    }

    @GetMapping("/")
    public SubscribeVO getNewSubscribe() {
        return subscribeService.getNewSubscribe();
    }

    @PostMapping("/getSubscribe")
    public SubscribeVO getSubscribe(@RequestBody SubscribeDTO subscribeDTO) {
        return subscribeService.getSubscribe(subscribeDTO);
    }

    @PostMapping("/getFan")
    public SubscribeVO getFan(@RequestBody SubscribeDTO subscribeDTO) {
        return subscribeService.getFan(subscribeDTO);
    }

    @GetMapping("/getIsSubscribe/{id}")
    public SubscribeVO getIsSubscribe(@PathVariable("id") Long id) {
        return subscribeService.getIsSubscribe(id);
    }

    @GetMapping("/getSubscribeNum/{id}")
    public SubscribeVO getSubscribeNum(@PathVariable("id") Long id) {
        return subscribeService.getSubscribeNum(id);
    }

    @GetMapping("/getFanNum/{id}")
    public SubscribeVO getFanNum(@PathVariable("id") Long id) {
        return subscribeService.getFanNum(id);
    }

    @GetMapping("/removeSubscribe/{id}")
    public String removeSubscribe(@PathVariable("id") Long id) {
        subscribeService.removeSubscribe(id);
        return "";
    }

    @GetMapping("/removeFan/{id}")
    public String removeFan(@PathVariable("id") Long id) {
        subscribeService.removeFan(id);
        return "";
    }

    @GetMapping("/subscribe/{id}")
    public String subscribe(@PathVariable("id") Long id) {
        subscribeService.subscribe(id);
        return "";
    }

    @GetMapping("/unsubscribe/{id}")
    public String unsubscribe(@PathVariable("id") Long id) {
        subscribeService.unsubscribe(id);
        return "";
    }
}
