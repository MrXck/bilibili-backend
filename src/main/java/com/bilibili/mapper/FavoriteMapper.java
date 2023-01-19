package com.bilibili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bilibili.pojo.Favorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xck
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}