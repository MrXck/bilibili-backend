package com.bilibili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bilibili.pojo.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    @Select("SELECT id,from_id,to_id,content,time,type FROM message WHERE id < #{id} AND ((from_id = #{fromId} AND to_id = #{toId}) OR (from_id = #{toId} AND to_id = #{fromId})) ORDER BY id desc limit #{pageSize}")
    List<Message> selectByPage(@Param("id") Long id, @Param("fromId") Long fromId, @Param("toId") Long toId, @Param("pageSize") Integer pageSize);
}