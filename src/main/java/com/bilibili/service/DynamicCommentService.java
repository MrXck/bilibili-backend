package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.dto.DynamicCommentDTO;
import com.bilibili.pojo.DynamicComment;
import com.bilibili.vo.DynamicCommentVO;

import java.util.List;

/**
 * @author xck
 */
public interface DynamicCommentService extends IService<DynamicComment> {
    /**
     * 通过动态id分页获取评论
     * @param dynamicCommentDTO 动态id 页大小 页数
     * @return 评论
     */
    List<DynamicCommentVO> getByPage(DynamicCommentDTO dynamicCommentDTO);

    /**
     * 根据动态id增加评论
     * @param dynamicComment 评论信息
     * @return 评论
     */
    DynamicComment addComment(DynamicComment dynamicComment);

    /**
     * 通过评论id分页获取该评论下的评论
     * @param dynamicCommentDTO 评论id 页大小
     * @return 评论
     */
    List<DynamicCommentVO> getReply(DynamicCommentDTO dynamicCommentDTO);

    /**
     * 根据动态id获取评论数
     * @param id 动态id
     * @return 评论数
     */
    DynamicCommentVO getCommentCount(Long id);
}