package com.bilibili.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bilibili.mapper.DynamicMapper;
import com.bilibili.mapper.UserMapper;
import com.bilibili.pojo.Dynamic;
import com.bilibili.pojo.User;

import java.util.*;

/**
 * @author xck
 */
public class ServiceUtils {

    public static List<Long> getDynamicIds(Integer type, DynamicMapper dynamicMapper) {
        LambdaQueryWrapper<Dynamic> dynamicLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dynamicLambdaQueryWrapper.eq(Dynamic::getUserId, UserThreadLocal.get());
        dynamicLambdaQueryWrapper.eq(Dynamic::getType, type);
        List<Dynamic> dynamicList = dynamicMapper.selectList(dynamicLambdaQueryWrapper);

        List<Long> dynamicIds = new ArrayList<>();

        for (Dynamic dynamic : dynamicList) {
            dynamicIds.add(dynamic.getId());
        }

        return dynamicIds;
    }

    public static Map<Long, User> getUserMap(List<Long> userIds, UserMapper userMapper) {
        Map<Long, User> userMap = new HashMap<>(16);

        if (!userIds.isEmpty()) {
            // 解决 n + 1 问题
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.in(User::getId, userIds);
            List<User> userList = userMapper.selectList(userLambdaQueryWrapper);

            for (User user : userList) {
                userMap.put(user.getId(), user);
            }
        }

        return userMap;
    }

    public static Map<Long, Dynamic> getDynamicMap(List<Long> dynamicIds, DynamicMapper dynamicMapper) {
        Map<Long, Dynamic> dynamicMap = new HashMap<>(16);

        if (!dynamicIds.isEmpty()) {
            // 解决 n + 1 问题
            LambdaQueryWrapper<Dynamic> dynamicLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dynamicLambdaQueryWrapper.in(Dynamic::getId, dynamicIds);
            List<Dynamic> dynamicList = dynamicMapper.selectList(dynamicLambdaQueryWrapper);

            for (Dynamic dynamic : dynamicList) {
                dynamicMap.put(dynamic.getId(), dynamic);
            }
        }

        return dynamicMap;
    }

    public static LinkedHashMap<String, Long> getSevenDaysMap() {
        DateTime yesterdayDateTime = DateUtil.yesterday();
        LinkedHashMap<String, Long> map = new LinkedHashMap<>();
        map.put(yesterdayDateTime.offset(DateField.DAY_OF_YEAR, -6).toDateStr(), 0L);
        for (int i = 0; i < 6; i++) {
            map.put(yesterdayDateTime.offset(DateField.DAY_OF_YEAR, 1).toDateStr(), 0L);
        }
        return map;
    }

}
