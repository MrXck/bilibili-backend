package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.dto.PlayDTO;
import com.bilibili.pojo.Play;
import com.bilibili.vo.PlayVO;

import java.util.List;

/**
 * @author xck
 */
public interface PlayService extends IService<Play> {
    /**
     * 分页获取播放记录
     * @param playDTO 页大小
     * @return 播放记录 动态
     */
    List<PlayVO> getByPage(PlayDTO playDTO);

    /**
     * 删除播放记录
     * @param id 播放记录id
     * @throws Exception 抛出删除失败错误
     */
    void delete(Long id) throws Exception;

    /**
     * 通过动态id获取播放数
     * @param id 动态id
     * @return 播放数
     */
    PlayVO getPlayNumByDynamicId(Long id);

    /**
     * 根据动态id增加播放记录
     * @param playDTO 动态id
     */
    void addPlay(PlayDTO playDTO);
}