package com.bilibili.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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
import java.util.*;

import static com.bilibili.utils.Constant.DELETE;
import static com.bilibili.utils.Constant.NOT_DELETE;
import static com.bilibili.utils.ServiceUtils.*;

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

        Map<Long, User> userMap = getUserMap(userIds, userMapper);
        Map<Long, Dynamic> dynamicMap = getDynamicMap(dynamicIds, dynamicMapper);

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
        LambdaQueryWrapper<Play> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Play::getIsDelete, NOT_DELETE);
        queryWrapper.eq(Play::getId, id);
        Play play = this.getOne(queryWrapper);
        if (play == null) {
            throw new Exception();
        }

        if (!play.getUserId().equals(UserThreadLocal.get())) {
            throw new APIException("删除失败");
        }

        play.setUpdateTime(LocalDateTime.now());
        play.setIsDelete(DELETE);
        this.save(play);
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
            play.setIsDelete(NOT_DELETE);
            this.save(play);
        }
    }

    @Override
    public PlayVO getYesterdayData(Integer type) {
        PlayVO playVO = new PlayVO();
        playVO.setPlayNum(0);
        DateTime yesterdayDateTime = DateUtil.yesterday();
        String yesterday = yesterdayDateTime.toDateStr();
        String today = DateUtil.offset(yesterdayDateTime, DateField.DAY_OF_YEAR, 1).toDateStr();


        List<Long> dynamicIds = getDynamicIds(type, dynamicMapper);

        if (!dynamicIds.isEmpty()) {
            LambdaQueryWrapper<Play> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.ge(Play::getCreateTime, yesterday);
            queryWrapper.lt(Play::getCreateTime, today);
            queryWrapper.in(Play::getDynamicId, dynamicIds);

            playVO.setPlayNum(this.count(queryWrapper));
        }
        return playVO;
    }

    @Override
    public PlayVO getAllData(Integer type) {
        PlayVO playVO = new PlayVO();
        playVO.setPlayNum(0);

        List<Long> dynamicIds = getDynamicIds(type, dynamicMapper);

        if (!dynamicIds.isEmpty()) {
            LambdaQueryWrapper<Play> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Play::getDynamicId, dynamicIds);

            playVO.setPlayNum(this.count(queryWrapper));
        }
        return playVO;
    }

    @Override
    public LinkedHashMap<String, Long> getLastSevenDaysData(Integer type) {
        String endTime = DateUtil.yesterday().offset(DateField.DAY_OF_YEAR, 1).toDateStr();
        String startTime = DateUtil.yesterday().offset(DateField.DAY_OF_YEAR, -6).toDateStr();

        LinkedHashMap<String, Long> map = getSevenDaysMap();

        List<Long> dynamicIds = getDynamicIds(type, dynamicMapper);

        if (!dynamicIds.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dynamicIds.size(); i++) {
                if (i == 0) {
                    sb.append(dynamicIds.get(i));
                } else {
                    sb.append(",").append(dynamicIds.get(i));
                }
            }
            List<Map<String, Object>> allData = this.baseMapper.getAllData(startTime, endTime, sb.toString());

            for (Map<String, Object> allDatum : allData) {
                map.put(allDatum.get("dat").toString(), (Long) allDatum.get("num"));
            }
        }

        return map;
    }

}