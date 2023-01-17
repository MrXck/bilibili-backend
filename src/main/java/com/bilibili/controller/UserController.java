package com.bilibili.controller;

import com.bilibili.pojo.User;
import com.bilibili.service.UserService;
import com.bilibili.utils.NoAuthorization;
import com.bilibili.vo.UserVo;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @NoAuthorization
    @PostMapping("login")
    private UserVo login(@RequestBody User user) throws Exception {
        return userService.login(user);
    }

    @NoAuthorization
    @PostMapping("/register")
    private String register(@RequestBody User user, String code) throws Exception {
        return userService.registerBySms(user, code);
    }

    @NoAuthorization
    @PostMapping("/sendMsg")
    public String sendMsg(@RequestBody User user) throws Exception {
        return userService.sendMsg(user);
    }

    @NoAuthorization
    @PostMapping("/loginBySms")
    public UserVo loginBySms(@RequestBody User user, String code) throws Exception {
        return userService.loginBySms(user, code);
    }

    @PostMapping("/update")
    public User update(@RequestBody User user) throws Exception {
        return userService.updateByUserId(user);
    }

    @GetMapping("/getUser/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.getById(userId);
    }
}
