package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.pojo.User;
import com.bilibili.vo.UserVo;


/**
 * @author xck
 */
public interface UserService extends IService<User> {

    /**
     * 登录
     * @param user 用户名 密码
     * @return user信息和token
     * @throws Exception 抛出登录失败错误
     */
    UserVo login(User user) throws Exception;

    /**
     * 注册
     * @param user 用户名 密码
     * @return 注册结果
     * @throws Exception 抛出注册失败错误
     */
    String register(User user) throws Exception;

    /**
     * 校验token
     * @param token 用户携带的token
     * @return 返回校验结果
     */
    String checkToken(String token);

    /**
     * 发送短信
     * @param user 电话号码
     * @return 返回发送结果
     * @throws Exception 抛出发送错误
     */
    String sendMsg(User user) throws Exception;

    /**
     * 通过短信登录
     * @param user 电话号码
     * @param code 验证码
     * @return user信息和token
     * @throws Exception 抛出登录失败错误
     */
    UserVo loginBySms(User user, String code) throws Exception;

    /**
     * 通过电话号码找到用户
     * @param phone 电话号码
     * @return 用户信息
     */
    User findByPhone(String phone);

    /**
     * 通过短信注册
     * @param user 用户信息
     * @param code 验证码
     * @return 返回注册结果
     * @throws Exception 抛出注册失败错误
     */
    String registerBySms(User user, String code) throws Exception;

    /**
     * 通过用户id修改用户信息
     * @param user 用户信息
     * @return 用户信息
     * @throws Exception 抛出失败错误
     */
    User updateByUserId(User user) throws Exception;
}
