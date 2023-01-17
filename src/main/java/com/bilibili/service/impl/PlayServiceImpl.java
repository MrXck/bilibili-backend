package com.bilibili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibili.dto.PlayDTO;
import com.bilibili.exception.APIException;
import com.bilibili.mapper.DynamicMapper;
import com.bilibili.mapper.PlayMapper;
import com.bilibili.mapper.UserMapper;
import com.bilibili.pojo.Dynamic;
import com.bilibili.pojo.Play;
import com.bilibili.pojo.User;
import com.bilibili.service.PlayService;
import com.bilibili.utils.UserThreadLocal;
import com.bilibili.vo.PlayVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xck
 */
@Service
public class PlayServiceImpl extends ServiceImpl<PlayMapper, Play> implements PlayService {

    private final DynamicMapper dynamicMapper;
    private final UserMapper userMapper;

    public PlayServiceImpl(DynamicMapper dynamicMapper, UserMapper userMapper) {
        this.dynamicMapper = dynamicMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<PlayVO> getByPage(PlayDTO playDTO) {
        Page<Play> page = new Page<>(1, playDTO.getPageSize());

        Long playId = playDTO.getPlayId();

        LambdaQueryWrapper<Play> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Play::getId);
        queryWrapper.eq(Play::getUserId, UserThreadLocal.get());
        if (playId != null) {
            queryWrapper.lt(Play::getId, playId);
        }

        Page<Play> playPage = this.page(page, queryWrapper);

        List<Play> records = playPage.getRecords();
        List<Long> userIds = new ArrayList<>();
        List<Long> dynamicIds = new ArrayList<>();
        for (Play record : records) {
            Long userId = record.getUserId();
            if (!userIds.contains(userId)) {
                userIds.add(userId);
            }
            Long dynamicId = record.getDynamicId();
            if (!dynamicIds.contains(dynamicId)) {
                dynamicIds.add(dynamicId);
            }
        }

        List<PlayVO> playVOS = new ArrayList<>();

        Map<Long, User> userMap = getUserMap(userIds);
        Map<Long, Dynamic> dynamicMap = getDynamicMap(dynamicIds);

        for (Play record : records) {
            PlayVO playVO = new PlayVO();
            playVO.setUser(userMap.get(record.getUserId()));
            playVO.setDynamic(dynamicMap.get(record.getDynamicId()));
            playVO.setPlayTime(record.getCreateTime());
            playVO.setPlay(record);
            playVOS.add(playVO);
        }

        return playVOS;
    }

    @Override
    public void delete(Long id) throws Exception {
        Play play = this.getById(id);
        if (play == null) {
            throw new Exception();
        }

        if (!play.getUserId().equals(UserThreadLocal.get())) {
            throw new APIException("删除失败");
        }

        this.removeById(id);
    }

    @Override
    public PlayVO getPlayNumByDynamicId(Long id) {
        PlayVO playVO = new PlayVO();

        LambdaQueryWrapper<Play> playLambdaQueryWrapper = new LambdaQueryWrapper<>();
        playLambdaQueryWrapper.eq(Play::getDynamicId, id);
        playVO.setPlayNum(this.count(playLambdaQueryWrapper));

        return playVO;
    }

    @Override
    public void addPlay(PlayDTO playDTO) {
        Long userId = UserThreadLocal.get();
        Long dynamicId = playDTO.getDynamicId();

        LambdaQueryWrapper<Play> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Play::getUserId, userId);
        queryWrapper.eq(Play::getDynamicId, dynamicId);
        int count = this.count(queryWrapper);
        if (count < 1) {
            Play play = new Play();
            play.setUserId(userId);
            play.setDynamicId(dynamicId);
            play.setCreateTime(LocalDateTime.now());
            play.setUpdateTime(LocalDateTime.now());
            this.save(play);
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