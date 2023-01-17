package com.bilibili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibili.dto.HistoryDTO;
import com.bilibili.exception.APIException;
import com.bilibili.mapper.HistoryMapper;
import com.bilibili.mapper.UserMapper;
import com.bilibili.pojo.History;
import com.bilibili.pojo.User;
import com.bilibili.service.HistoryService;
import com.bilibili.utils.UserThreadLocal;
import com.bilibili.vo.HistoryVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xck
 */
@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History> implements HistoryService {

    private final UserMapper userMapper;

    private final ReentrantLock lock = new ReentrantLock();

    public HistoryServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public HistoryVO getHistory(HistoryDTO historyDTO) {
        Long userId = UserThreadLocal.get();

        Long id = historyDTO.getId();
        LambdaQueryWrapper<History> historyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (id != null) {
            historyLambdaQueryWrapper.lt(History::getId, id);
        }

        Page<History> page = new Page<>(1, historyDTO.getPageSize());

        historyLambdaQueryWrapper.eq(History::getUserId, userId);
        historyLambdaQueryWrapper.orderByDesc(History::getCreateTime);

        Page<History> historyPage = this.page(page, historyLambdaQueryWrapper);


        List<Long> userIds = new ArrayList<>();

        for (History record : historyPage.getRecords()) {
            if (!userIds.contains(record.getWithId())) {
                userIds.add(record.getWithId());
            }
        }

        HistoryVO historyVO = new HistoryVO();
        historyVO.setUsers(getUser(userIds));

        return historyVO;
    }

    @Override
    public void addHistory(Long toId, Long fromId) {
        LambdaQueryWrapper<History> queryWrapper = new LambdaQueryWrapper<>();
        if (fromId == null) {
            fromId = UserThreadLocal.get();
        }
        queryWrapper.eq(History::getUserId, fromId);
        queryWrapper.eq(History::getWithId, toId);
        lock.lock();
        try {
            History one = this.getOne(queryWrapper);
            if (one == null) {
                History history = new History();
                history.setUserId(fromId);
                history.setWithId(toId);
                history.setCreateTime(LocalDateTime.now());
                this.save(history);
            }
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void removeHistory(Long id) {
        Long userId = UserThreadLocal.get();

        LambdaQueryWrapper<History> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(History::getWithId, id);
        queryWrapper.eq(History::getUserId, userId);

        History history = this.getOne(queryWrapper);
        if (history != null) {
            this.removeById(history.getId());
        } else {
            throw new APIException("删除失败");
        }
    }

    private List<User> getUser(List<Long> userIds) {
        List<User> userList = new ArrayList<>();
        if (!userIds.isEmpty()) {
            // 解决 n + 1 问题
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.in(User::getId, userIds);
            userList = userMapper.selectList(userLambdaQueryWrapper);

        }
        return userList;
    }
}