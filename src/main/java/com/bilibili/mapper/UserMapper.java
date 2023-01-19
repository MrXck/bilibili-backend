package com.bilibili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bilibili.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xck
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
