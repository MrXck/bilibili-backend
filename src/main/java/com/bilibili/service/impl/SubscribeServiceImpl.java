package com.bilibili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibili.dto.SubscribeDTO;
import com.bilibili.exception.APIException;
import com.bilibili.mapper.SubscribeMapper;
import com.bilibili.mapper.UserMapper;
import com.bilibili.pojo.Subscribe;
import com.bilibili.pojo.User;
import com.bilibili.service.SubscribeService;
import com.bilibili.utils.UserThreadLocal;
import com.bilibili.vo.SubscribeVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xck
 */
@Service
public class SubscribeServiceImpl extends ServiceImpl<SubscribeMapper, Subscribe> implements SubscribeService {

    private final UserMapper userMapper;

    public SubscribeServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public SubscribeVO getNewSubscribe() {
        Long userId = UserThreadLocal.get();

        // 获取最新 20 个关注
        Page<Subscribe> page = new Page<>(1, 20);
        LambdaQueryWrapper<Subscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subscribe::getUserId, userId);
        queryWrapper.orderByDesc(Subscribe::getCreateTime);
        List<Subscribe> records = this.page(page, queryWrapper).getRecords();

        List<Long> userIds = new ArrayList<>();

        for (Subscribe record : records) {
            userIds.add(record.getSubscribeId());
        }

        return new SubscribeVO(getUsers(userIds));
    }

    @Override
    public SubscribeVO getSubscribe(SubscribeDTO subscribeDTO) {

        Long userId = subscribeDTO.getUserId();

        if (userId == null) {
            userId = UserThreadLocal.get();
        }

        Page<Subscribe> page = new Page<>(subscribeDTO.getPageNum(), subscribeDTO.getPageSize());
        LambdaQueryWrapper<Subscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subscribe::getUserId, userId);
        queryWrapper.orderByDesc(Subscribe::getCreateTime);
        Page<Subscribe> subscribePage = this.page(page, queryWrapper);
        List<Subscribe> records = subscribePage.getRecords();
        subscribePage.setRecords(null);

        List<Long> userIds = new ArrayList<>();

        for (Subscribe record : records) {
            userIds.add(record.getSubscribeId());
        }


        return new SubscribeVO(getUsers(userIds), subscribePage);
    }

    @Override
    public SubscribeVO getFan(SubscribeDTO subscribeDTO) {
        Long userId = subscribeDTO.getUserId();

        if (userId == null) {
            userId = UserThreadLocal.get();
        }

        Page<Subscribe> page = new Page<>(subscribeDTO.getPageNum(), subscribeDTO.getPageSize());
        LambdaQueryWrapper<Subscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subscribe::getSubscribeId, userId);
        queryWrapper.orderByDesc(Subscribe::getCreateTime);
        Page<Subscribe> subscribePage = this.page(page, queryWrapper);
        List<Subscribe> records = subscribePage.getRecords();
        subscribePage.setRecords(null);

        List<Long> userIds = new ArrayList<>();

        for (Subscribe record : records) {
            userIds.add(record.getUserId());
        }


        return new SubscribeVO(getUsers(userIds), subscribePage);
    }

    @Override
    public SubscribeVO getIsSubscribe(Long id) {
        LambdaQueryWrapper<Subscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subscribe::getSubscribeId, id);
        queryWrapper.eq(Subscribe::getUserId, UserThreadLocal.get());
        SubscribeVO subscribeVO = new SubscribeVO();
        subscribeVO.setIsSubscribe(this.count(queryWrapper) > 0);
        return subscribeVO;
    }

    @Override
    public SubscribeVO getSubscribeNum(Long id) {
        LambdaQueryWrapper<Subscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subscribe::getUserId, id);
        SubscribeVO subscribeVO = new SubscribeVO();
        subscribeVO.setSubscribeNum(this.count(queryWrapper));
        return subscribeVO;
    }

    @Override
    public SubscribeVO getFanNum(Long id) {
        LambdaQueryWrapper<Subscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subscribe::getSubscribeId, id);
        SubscribeVO subscribeVO = new SubscribeVO();
        subscribeVO.setFanNum(this.count(queryWrapper));
        return subscribeVO;
    }

    @Override
    public void removeSubscribe(Long id) {
        LambdaQueryWrapper<Subscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subscribe::getUserId, UserThreadLocal.get());
        queryWrapper.eq(Subscribe::getSubscribeId, id);
        this.remove(queryWrapper);
    }

    @Override
    public void removeFan(Long id) {
        LambdaQueryWrapper<Subscribe> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Subscribe::getUserId, id);
        queryWrapper.eq(Subscribe::getSubscribeId, UserThreadLocal.get());
        this.remove(queryWrapper);
    }

    @Override
    public void subscribe(Long id) {
        boolean isSubscribe = getIsSubscribe(id).getIsSubscribe();

        if (!isSubscribe) {
            Subscribe subscribe = new Subscribe();
            subscribe.setSubscribeId(id);
            subscribe.setUserId(UserThreadLocal.get());
            subscribe.setCreateTime(LocalDateTime.now());
            this.save(subscribe);
        } else {
            throw new APIException("已经关注");
        }
    }

    @Override
    public void unsubscribe(Long id) {
        boolean isSubscribe = getIsSubscribe(id).getIsSubscribe();

        if (isSubscribe) {
            LambdaQueryWrapper<Subscribe> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Subscribe::getSubscribeId, id);
            queryWrapper.eq(Subscribe::getUserId, UserThreadLocal.get());
            this.remove(queryWrapper);
        } else {
            throw new APIException("没有关注此用户");
        }
    }

    private List<User> getUsers(List<Long> userIds) {
        if (!userIds.isEmpty()) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.in(User::getId, userIds);

            return userMapper.selectList(userLambdaQueryWrapper);
        }
        return new ArrayList<>();
    }

}