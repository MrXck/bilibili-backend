package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.dto.DynamicDTO;
import com.bilibili.pojo.Dynamic;
import com.bilibili.vo.DynamicVO;

import java.util.List;

/**
 * @author xck
 */
public interface DynamicService extends IService<Dynamic> {
    /**
     * 通过用户id分页获取动态
     * @param dynamicDTO 用户id 动态id
     * @return 动态
     */
    List<DynamicVO> getDynamicByUserId(DynamicDTO dynamicDTO);

    /**
     * 通过关键词和类型搜索
     * @param dynamicDTO 页大小 页数 关键词 类型
     * @return 搜索结果
     */
    List<DynamicVO> search(DynamicDTO dynamicDTO);

    /**
     * 通过动态id获取动态
     * @param dynamicDTO 动态id 类型
     * @return 动态 发布该动态的用户
     */
    DynamicVO getDynamicById(DynamicDTO dynamicDTO);

    /**
     * 发布/投稿动态
     * @param dynamic 动态信息
     */
    void submit(Dynamic dynamic);

    /**
     * 获取动态数
     * @return 动态数
     */
    DynamicVO getDynamicNum();

    /**
     * 通过用户id分页获取视频动态
     * @param dynamicDTO 用户id 页大小 页数
     * @return 视频动态
     */
    List<DynamicVO> getVideoDynamic(DynamicDTO dynamicDTO);

    /**
     * 根据用户id获取视频动态数
     * @param id 用户id
     * @return 视频动态数
     */
    DynamicVO getVideoDynamicNum(Long id);
}