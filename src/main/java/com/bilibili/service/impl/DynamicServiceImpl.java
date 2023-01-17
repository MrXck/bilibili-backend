package com.bilibili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibili.dto.DynamicDTO;
import com.bilibili.exception.APIException;
import com.bilibili.mapper.*;
import com.bilibili.pojo.*;
import com.bilibili.service.DynamicService;
import com.bilibili.utils.UserThreadLocal;
import com.bilibili.vo.DynamicVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bilibili.utils.Constant.*;

/**
 * @author xck
 */
@Service
public class DynamicServiceImpl extends ServiceImpl<DynamicMapper, Dynamic> implements DynamicService {

    private final SubscribeMapper subscribeMapper;
    private final UserMapper userMapper;
    private final PlayMapper playMapper;
    private final FavoriteMapper favoriteMapper;

    public DynamicServiceImpl(SubscribeMapper subscribeMapper, UserMapper userMapper, PlayMapper playMapper, FavoriteMapper favoriteMapper) {
        this.subscribeMapper = subscribeMapper;
        this.userMapper = userMapper;
        this.playMapper = playMapper;
        this.favoriteMapper = favoriteMapper;
    }

    @Override
    public List<DynamicVO> getDynamicByUserId(DynamicDTO dynamicDTO) {
        Long userId = dynamicDTO.getUserId();
        Long dynamicId = dynamicDTO.getDynamicId();

        // 用户id 为 null 则获取所有目前用户关注列表中的所有最新动态的前 20 条
        // 动态id是否存在 存在则根据 动态id 取该用户在当前 动态id 发布之前的 20 条动态
        Page<Dynamic> page = new Page<>(1, 10);
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            queryWrapper.eq(Dynamic::getUserId, userId);
        } else {
            LambdaQueryWrapper<Subscribe> subscribeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            subscribeLambdaQueryWrapper.eq(Subscribe::getUserId, UserThreadLocal.get());
            List<Subscribe> subscribes = subscribeMapper.selectList(subscribeLambdaQueryWrapper);
            List<Long> subscribeIds = subscribes.stream().map(Subscribe::getSubscribeId).collect(Collectors.toList());
            subscribeIds.add(UserThreadLocal.get());
            queryWrapper.in(Dynamic::getUserId, subscribeIds);
        }

        if (dynamicId != null) {
            queryWrapper.lt(Dynamic::getId, dynamicId);
        }
        queryWrapper.orderByDesc(Dynamic::getCreateTime);

        List<Long> userIds = new ArrayList<>();

        // 通过查询出来的 Dynamic 去生成 DynamicVO
        List<DynamicVO> dynamicVOS = this.page(page, queryWrapper).getRecords().stream().map(item -> {
            DynamicVO dynamicVO = new DynamicVO();
            dynamicVO.setDynamic(item);

            userIds.add(item.getUserId());

            return dynamicVO;
        }).collect(Collectors.toList());


        if (!userIds.isEmpty()) {

            Map<Long, User> userMap = getUserMap(userIds);
            for (DynamicVO dynamicVO : dynamicVOS) {
                Long userId1 = dynamicVO.getDynamic().getUserId();
                if (userMap.containsKey(userId1)) {
                    dynamicVO.setUser(userMap.get(userId1));
                }
            }
        }

        return dynamicVOS;
    }

    @Override
    public List<DynamicVO> search(DynamicDTO dynamicDTO) {
        DynamicVO dynamicVO = new DynamicVO();
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();

        Integer type = dynamicDTO.getType();

        // 不传 type 则搜索视频和动态
        if (type != null) {
            queryWrapper.eq(Dynamic::getType, type);
        }

        Integer pageNum = dynamicDTO.getPageNum();
        Integer pageSize = dynamicDTO.getPageSize();
        String keyword = dynamicDTO.getKeyword();

        Page<Dynamic> page = new Page<>(pageNum, pageSize);

        // 关键词开头为 ID: 则额外搜索user
        if (keyword.startsWith(SEARCH_USER_PREFIX)) {
            String[] split = keyword.split(":");
            User user = userMapper.selectById(split[split.length - 1]);
            user.setPassword("");
            dynamicVO.setUser(user);
        }

        queryWrapper.like(Dynamic::getTitle, keyword);
        queryWrapper.orderByDesc(Dynamic::getCreateTime);
        Page<Dynamic> dynamicPage = this.page(page, queryWrapper);
        List<Dynamic> records = dynamicPage.getRecords();
        dynamicVO.setPage(dynamicPage);

        List<DynamicVO> dynamicVOS = new ArrayList<>();
        dynamicVOS.add(dynamicVO);

        List<Long> userIds = new ArrayList<>();

        // 生成 dynamicVO
        for (Dynamic record : records) {
            DynamicVO dynamicVO1 = new DynamicVO();
            dynamicVO1.setDynamic(record);
            userIds.add(record.getUserId());
            dynamicVOS.add(dynamicVO1);
        }

        if (!userIds.isEmpty()) {
            // 查询每条动态的用户信息
            Map<Long, User> userMap = getUserMap(userIds);

            // 解决 n + 1 问题
            for (DynamicVO vo : dynamicVOS) {
                if (vo.getDynamic() == null) {
                    continue;
                }
                Long userId = vo.getDynamic().getUserId();
                if (userMap.containsKey(userId)) {
                    vo.setUser(userMap.get(userId));
                }
            }
        }
        return dynamicVOS;
    }

    @Override
    public DynamicVO getDynamicById(DynamicDTO dynamicDTO) {

        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        Integer type = dynamicDTO.getType();
        if (type != null) {
            queryWrapper.eq(Dynamic::getType, type);
        }
        queryWrapper.eq(Dynamic::getId, dynamicDTO.getDynamicId());
        Dynamic dynamic = this.getOne(queryWrapper);

        User user = userMapper.selectById(dynamic.getUserId());
        DynamicVO dynamicVO = new DynamicVO();
        dynamicVO.setDynamic(dynamic);
        dynamicVO.setUser(user);
        return dynamicVO;
    }

    @Override
    public void submit(Dynamic dynamic) {
        String content = dynamic.getContent();
        String title = dynamic.getTitle();
        String src = dynamic.getSrc();
        Integer type = dynamic.getType();

        if ("".equals(title) || title == null) {
            throw new APIException("发布失败");
        }

        if (TEXT_TYPE.equals(type)) {
            if ("".equals(content) || content == null) {
                throw new APIException("发布失败");
            }
        } else if (DYNAMIC_TYPE.equals(type)) {

        } else if (VIDEO_TYPE.equals(type)) {
            if ("".equals(src) || src == null) {
                throw new APIException("发布失败");
            }
        } else {
            throw new APIException("发布失败");
        }

        dynamic.setCreateTime(LocalDateTime.now());
        dynamic.setUserId(UserThreadLocal.get());
        this.save(dynamic);
    }

    @Override
    public DynamicVO getDynamicNum() {
        DynamicVO dynamicVO = new DynamicVO();
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getUserId, UserThreadLocal.get());
        dynamicVO.setDynamicNum(this.count(queryWrapper));
        return dynamicVO;
    }

    @Override
    public List<DynamicVO> getVideoDynamic(DynamicDTO dynamicDTO) {
        Long userId = dynamicDTO.getUserId();

        User user = userMapper.selectById(userId);

        Page<Dynamic> page = new Page<>(dynamicDTO.getPageNum(), dynamicDTO.getPageSize());

        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getUserId, userId);
        queryWrapper.eq(Dynamic::getType, VIDEO_TYPE);

        Page<Dynamic> dynamicPage = this.page(page, queryWrapper);

        List<DynamicVO> dynamicVOS = new ArrayList<>();

        List<Dynamic> records = dynamicPage.getRecords();

        List<Long> dynamicIds = new ArrayList<>();

        for (Dynamic record : records) {
            dynamicIds.add(record.getId());
        }

        if (!dynamicIds.isEmpty()) {
            LambdaQueryWrapper<Play> playLambdaQueryWrapper = new LambdaQueryWrapper<>();
            playLambdaQueryWrapper.in(Play::getDynamicId, dynamicIds);
            List<Play> plays = playMapper.selectList(playLambdaQueryWrapper);

            Map<Long, Integer> playMap = new HashMap<>(16);
            for (Play play : plays) {
                Long dynamicId = play.getDynamicId();
                if (playMap.containsKey(dynamicId)) {
                    playMap.put(dynamicId, playMap.get(dynamicId) + 1);
                } else {
                    playMap.put(dynamicId, 1);
                }
            }

            LambdaQueryWrapper<Favorite> favoriteLambdaQueryWrapper = new LambdaQueryWrapper<>();
            favoriteLambdaQueryWrapper.in(Favorite::getDynamicId, dynamicIds);
            List<Favorite> favorites = favoriteMapper.selectList(favoriteLambdaQueryWrapper);

            Map<Long, Integer> favoritesMap = new HashMap<>(16);
            for (Favorite favorite : favorites) {
                Long dynamicId = favorite.getDynamicId();
                if (favoritesMap.containsKey(dynamicId)) {
                    favoritesMap.put(dynamicId, playMap.get(dynamicId) + 1);
                } else {
                    favoritesMap.put(dynamicId, 1);
                }
            }

            for (Dynamic record : records) {
                DynamicVO dynamicVO = new DynamicVO();
                dynamicVO.setDynamic(record);
                dynamicVO.setUser(user);
                dynamicVO.setPlayNum(playMap.get(record.getId()));
                dynamicVO.setCollectNum(favoritesMap.get(record.getId()));

                dynamicVOS.add(dynamicVO);
            }
        }


        return dynamicVOS;
    }

    @Override
    public DynamicVO getVideoDynamicNum(Long id) {
        DynamicVO dynamicVO = new DynamicVO();
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getUserId, id);
        queryWrapper.eq(Dynamic::getType, VIDEO_TYPE);
        dynamicVO.setDynamicNum(this.count(queryWrapper));
        return dynamicVO;
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
}