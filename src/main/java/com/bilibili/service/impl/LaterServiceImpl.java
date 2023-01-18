package com.bilibili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibili.dto.LaterDTO;
import com.bilibili.mapper.DynamicMapper;
import com.bilibili.mapper.LaterMapper;
import com.bilibili.mapper.PlayMapper;
import com.bilibili.mapper.UserMapper;
import com.bilibili.pojo.Dynamic;
import com.bilibili.pojo.Later;
import com.bilibili.pojo.Play;
import com.bilibili.pojo.User;
import com.bilibili.service.LaterService;
import com.bilibili.utils.UserThreadLocal;
import com.bilibili.vo.LaterVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xck
 */
@Service
public class LaterServiceImpl extends ServiceImpl<LaterMapper, Later> implements LaterService {

    private final UserMapper userMapper;

    private final DynamicMapper dynamicMapper;

    private final PlayMapper playMapper;

    public LaterServiceImpl(UserMapper userMapper, DynamicMapper dynamicMapper, PlayMapper playMapper) {
        this.userMapper = userMapper;
        this.dynamicMapper = dynamicMapper;
        this.playMapper = playMapper;
    }

    @Override
    public List<LaterVO> getAll() {
        Long userId = UserThreadLocal.get();
        LambdaQueryWrapper<Later> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Later::getUserId, userId);
        queryWrapper.orderByDesc(Later::getId);
        List<Later> laterList = this.list(queryWrapper);

        List<Long> userIds = new ArrayList<>();
        List<Long> dynamicIds = new ArrayList<>();

        for (Later later : laterList) {
            userIds.add(later.getDynamicUserId());
            dynamicIds.add(later.getDynamicId());
        }

        Map<Long, User> userMap = getUserMap(userIds);
        Map<Long, Dynamic> dynamicMap = getDynamicMap(dynamicIds);


        List<LaterVO> list = new ArrayList<>();

        for (Later later : laterList) {
            LaterVO laterVO = new LaterVO();
            laterVO.setLater(later);
            laterVO.setDynamic(dynamicMap.get(later.getDynamicId()));
            laterVO.setUser(userMap.get(later.getDynamicUserId()));

            LambdaQueryWrapper<Play> playLambdaQueryWrapper = new LambdaQueryWrapper<>();
            playLambdaQueryWrapper.eq(Play::getDynamicId, later.getDynamicId());
            playLambdaQueryWrapper.eq(Play::getUserId, userId);
            laterVO.setIsPlay(playMapper.selectCount(playLambdaQueryWrapper) > 0);

            list.add(laterVO);
        }

        return list;
    }

    @Override
    public void delete(Long id) {
        Long userId = UserThreadLocal.get();
        LambdaQueryWrapper<Later> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Later::getUserId, userId);
        queryWrapper.eq(Later::getId, id);
        this.remove(queryWrapper);
    }

    @Override
    public void deleteAll() {
        LambdaQueryWrapper<Later> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Later::getUserId, UserThreadLocal.get());
        this.remove(queryWrapper);
    }

    @Override
    public void deleteView() {
        Long userId = UserThreadLocal.get();
        LambdaQueryWrapper<Later> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Later::getUserId, userId);
        List<Later> laterList = this.list(queryWrapper);

        List<Long> dynamicIds = new ArrayList<>();

        for (Later later : laterList) {
            dynamicIds.add(later.getDynamicId());
        }

        if (!dynamicIds.isEmpty()) {
            LambdaQueryWrapper<Play> playLambdaQueryWrapper = new LambdaQueryWrapper<>();
            playLambdaQueryWrapper.eq(Play::getUserId, userId);
            playLambdaQueryWrapper.in(Play::getDynamicId, dynamicIds);
            List<Play> playList = playMapper.selectList(playLambdaQueryWrapper);

            dynamicIds = new ArrayList<>();
            for (Play play : playList) {
                dynamicIds.add(play.getDynamicId());
            }
            if (!dynamicIds.isEmpty()) {
                queryWrapper.in(Later::getDynamicId, dynamicIds);
                this.remove(queryWrapper);
            }
        }
    }

    @Override
    public void addLater(LaterDTO laterDTO) {
        Long userId = UserThreadLocal.get();
        Long dynamicId = laterDTO.getDynamicId();
        Long dynamicUserId = laterDTO.getDynamicUserId();

        LambdaQueryWrapper<Later> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Later::getUserId, userId);
        queryWrapper.eq(Later::getDynamicId, dynamicId);
        queryWrapper.eq(Later::getDynamicUserId, dynamicUserId);
        int count = this.count(queryWrapper);
        if (count < 1) {
            Later later = new Later();
            later.setDynamicId(dynamicId);
            later.setUserId(userId);
            later.setDynamicUserId(dynamicUserId);
            this.save(later);
        }
    }


    private Map<Long, User> getUserMap(List<Long> userIds) {
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

    private Map<Long, Dynamic> getDynamicMap(List<Long> dynamicIds) {
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
}