package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.dto.FavoritesDTO;
import com.bilibili.pojo.Favorites;
import com.bilibili.vo.FavoritesVO;

import java.util.List;

/**
 * @author xck
 */
public interface FavoritesService extends IService<Favorites> {
    /**
     * 根据用户id获取该用户的收藏夹
     * @param id 用户id
     * @return 收藏夹
     */
    List<FavoritesVO> getList(Long id);

    /**
     * 更新收藏夹信息
     * @param favoritesDTO 收藏夹信息
     * @throws Exception 抛出更新错误
     */
    void updateFavorites(FavoritesDTO favoritesDTO) throws Exception;

    /**
     * 创建收藏夹
     * @param favoritesDTO 收藏夹名称
     */
    void create(FavoritesDTO favoritesDTO);
}