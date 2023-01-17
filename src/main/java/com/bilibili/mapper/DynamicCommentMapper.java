package com.bilibili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bilibili.pojo.DynamicComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DynamicCommentMapper extends BaseMapper<DynamicComment> {
}