package com.bilibili.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bilibili.pojo.Play;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author xck
 */
@Mapper
public interface PlayMapper extends BaseMapper<Play> {

    @Select("select date(create_time) dat, count(*) num from play where create_time >= #{startTime} and create_time < #{endTime} and dynamic_id in (#{ids}) GROUP BY dat ORDER BY dat desc;")
    List<Map<String, Object>> getAllData(String startTime, String endTime, String ids);
}