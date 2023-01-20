package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.dto.StarDTO;
import com.bilibili.pojo.Star;
import com.bilibili.vo.StarVO;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author xck
 */
public interface StarService extends IService<Star> {
    /**
     * 获取动态的点赞数
     * @param id 动态id
     * @return 点赞数
     */
    StarVO getDynamicStarNum(Long id);

    /**
     * 获取评论的点赞数
     * @param id 评论id
     * @return 点赞数
     */
    StarVO getCommentStarNum(Long id);

    /**
     * 点赞
     * @param star 要点赞的id
     */
    void star(Star star);

    /**
     * 取消点赞
     * @param star 要取消点赞的id
     */
    void unStar(Star star);

    /**
     * 获取谁点赞了我
     * @param starDTO 分页信息
     * @return 谁点赞了我
     */
    List<StarVO> getStar(StarDTO starDTO);

    /**
     * 获取昨日新增点赞数
     * @param type 动态类型
     * @return 昨日新增点赞数
     */
    StarVO getYesterdayData(Integer type);

    /**
     * 获取所有点赞数
     * @param type 动态类型
     * @return 点赞数
     */
    StarVO getAllData(Integer type);

    /**
     * 获取近7日所有点赞数
     * @param type 动态类型
     * @return 近7日所有点赞数
     */
    LinkedHashMap<String, Long> getLastSevenDaysData(Integer type);

    /**
     * 通过用户id获取这个用户的获赞数
     * @param id 用户id
     * @return 获赞数
     */
    StarVO getAllStar(Long id);
}