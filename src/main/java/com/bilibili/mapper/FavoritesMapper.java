package com.bilibili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bilibili.pojo.Favorites;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xck
 */
@Mapper
public interface FavoritesMapper extends BaseMapper<Favorites> {
}