package com.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibili.dto.FavoriteDTO;
import com.bilibili.pojo.Favorite;
import com.bilibili.vo.FavoriteVO;

import java.util.List;

/**
 * @author xck
 */
public interface FavoriteService extends IService<Favorite> {
    /**
     * 根据收藏夹id分页获取收藏内容
     * @param favoriteDTO 页大小 页数 收藏夹id
     * @return 收藏内容
     */
    List<FavoriteVO> getByPage(FavoriteDTO favoriteDTO);

    /**
     * 通过动态id获取收藏数
     * @param favoriteDTO 动态id
     * @return 收藏数
     */
    FavoriteVO getFavoriteNum(FavoriteDTO favoriteDTO);

    /**
     * 放入收藏夹
     * @param favoriteDTO 动态id 要放入的收藏夹
     */
    void putInFavorite(FavoriteDTO favoriteDTO);

    /**
     * 获取昨日新增收藏数
     * @param type 动态类型
     * @return 昨日新增收藏数
     */
    FavoriteVO getYesterdayData(Integer type);
}