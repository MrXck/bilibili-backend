package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.pojo.Later;
import com.bilibili.vo.LaterVO;

import java.util.List;

/**
 * @author xck
 */
public interface LaterService extends IService<Later> {
    /**
     * 获取所有稍后在看
     * @return 稍后在看
     */
    List<LaterVO> getAll();

    /**
     * 根据稍后在看id删除播放记录
     * @param id 稍后在看id
     */
    void delete(Long id);

    /**
     * 删除所有稍后在看
     */
    void deleteAll();

    /**
     * 删除已观看的稍后在看
     */
    void deleteView();
}