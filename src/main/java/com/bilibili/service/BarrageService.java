package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.pojo.Barrage;
import com.bilibili.vo.BarrageVO;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author xck
 */
public interface BarrageService extends IService<Barrage> {
    /**
     * 通过动态id获取弹幕
     * @param id 动态id
     * @return 弹幕
     */
    List<BarrageVO> getByDynamicId(Long id);

    /**
     * 发送弹幕
     * @param barrage 弹幕信息
     */
    void sendBarrage(Barrage barrage);

    /**
     * 通过动态id获取弹幕数
     * @param id 动态id
     * @return 弹幕数
     */
    BarrageVO getBarrageNumByDynamicId(Long id);

    /**
     * 获取昨日新增弹幕数
     * @param type 动态类型
     * @return 昨日新增弹幕数
     */
    BarrageVO getYesterdayData(Integer type);

    /**
     * 获取所有弹幕数
     * @param type 动态类型
     * @return 所有弹幕数
     */
    BarrageVO getAllData(Integer type);

    /**
     * 获取近7日所有弹幕数
     * @param type 动态类型
     * @return 近7日所有弹幕数
     */
    LinkedHashMap<String, Long> getLastSevenDaysData(Integer type);
}