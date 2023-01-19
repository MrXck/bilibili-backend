package com.bilibili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bilibili.pojo.Subscribe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author xck
 */
@Mapper
public interface SubscribeMapper extends BaseMapper<Subscribe> {

    @Select("select date(create_time) dat, count(*) num from subscribe where create_time >= #{startTime} and create_time < #{endTime} and subscribe_id = #{subscribeId} GROUP BY dat ORDER BY dat desc;")
    List<Map<String, Object>> getLastSevenDaysFanData(String startTime, String endTime, Long subscribeId);

    @Select("select date(create_time) dat, count(*) num from subscribe where create_time >= #{startTime} and create_time < #{endTime} and user_id = #{subscribeId} GROUP BY dat ORDER BY dat desc;")
    List<Map<String, Object>> getLastSevenDaysSubscribeData(String startTime, String endTime, Long subscribeId);
}