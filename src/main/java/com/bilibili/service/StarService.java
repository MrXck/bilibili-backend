package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.dto.StarDTO;
import com.bilibili.pojo.Star;
import com.bilibili.vo.StarVO;

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
}