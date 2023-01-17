package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.dto.HistoryDTO;
import com.bilibili.pojo.History;
import com.bilibili.vo.HistoryVO;


/**
 * @author xck
 */
public interface HistoryService extends IService<History> {
    /**
     * 获取历史记录
     * @param historyDTO 页大小
     * @return 历史记录
     */
    HistoryVO getHistory(HistoryDTO historyDTO);

    /**
     * 添加历史记录
     * @param toId 对方用户id
     * @param fromId 己方用户id
     */
    void addHistory(Long toId, Long fromId);

    /**
     * 根据历史记录id删除历史记录
     * @param id 历史记录id
     */
    void removeHistory(Long id);
}