package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.dto.SubscribeDTO;
import com.bilibili.pojo.Subscribe;
import com.bilibili.vo.SubscribeVO;

import java.util.LinkedHashMap;

/**
 * @author xck
 */
public interface SubscribeService extends IService<Subscribe> {
    /**
     * 获取最新的20个关注
     * @return 最新的20个关注
     */
    SubscribeVO getNewSubscribe();

    /**
     * 获取关注用户
     * @param subscribeDTO 分页信息
     * @return 关注用户
     */
    SubscribeVO getSubscribe(SubscribeDTO subscribeDTO);

    /**
     * 获取粉丝
     * @param subscribeDTO 分页信息
     * @return 粉丝
     */
    SubscribeVO getFan(SubscribeDTO subscribeDTO);

    /**
     * 获取是否关注
     * @param id 用户id
     * @return 是否关注
     */
    SubscribeVO getIsSubscribe(Long id);

    /**
     * 获取关注数量
     * @param id 用户id
     * @return 关注数
     */
    SubscribeVO getSubscribeNum(Long id);

    /**
     * 获取粉丝数量
     * @param id 用户id
     * @return 粉丝数
     */
    SubscribeVO getFanNum(Long id);

    /**
     * 取消关注
     * @param id 用户id
     */
    void removeSubscribe(Long id);

    /**
     * 取消粉丝的关注
     * @param id 用户id
     */
    void removeFan(Long id);

    /**
     * 关注
     * @param id 用户id
     */
    void subscribe(Long id);

    /**
     * 取消关注
     * @param id 用户id
     */
    void unsubscribe(Long id);

    /**
     * 获取昨天新增粉丝数
     * @return 昨天新增粉丝数
     */
    SubscribeVO getYesterdayData();

    /**
     * 获取近7日新增粉丝数
     * @param type 动态类型
     * @return 近7日新增粉丝数
     */
    LinkedHashMap<String, Long> getLastSevenDaysData(Integer type);
}