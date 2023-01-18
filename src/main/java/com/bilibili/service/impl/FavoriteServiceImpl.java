package com.bilibili.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibili.dto.FavoriteDTO;
import com.bilibili.exception.APIException;
import com.bilibili.mapper.*;
import com.bilibili.pojo.*;
import com.bilibili.service.FavoriteService;
import com.bilibili.utils.UserThreadLocal;
import com.bilibili.vo.BarrageVO;
import com.bilibili.vo.FavoriteVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bilibili.utils.Constant.VIDEO_TYPE;

/**
 * @author xck
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final DynamicMapper dynamicMapper;
    private final PlayMapper playMapper;
    private final UserMapper userMapper;
    private final FavoritesMapper favoritesMapper;

    public FavoriteServiceImpl(DynamicMapper dynamicMapper, PlayMapper playMapper, UserMapper userMapper, FavoritesMapper favoritesMapper) {
        this.dynamicMapper = dynamicMapper;
        this.playMapper = playMapper;
        this.userMapper = userMapper;
        this.favoritesMapper = favoritesMapper;
    }

    @Override
    public List<FavoriteVO> getByPage(FavoriteDTO favoriteDTO) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getFavoritesId, favoriteDTO.getFavoritesId());
        queryWrapper.orderByDesc(Favorite::getCreateTime);

        Page<Favorite> page = new Page<>(favoriteDTO.getPageNum(), favoriteDTO.getPageSize());

        Page<Favorite> favoritePage = this.page(page, queryWrapper);

        List<Favorite> records = favoritePage.getRecords();
        favoritePage.setRecords(null);

        List<FavoriteVO> favoriteVOS = new ArrayList<>();

        List<Long> ids = new ArrayList<>();

        for (Favorite record : records) {
            if (!ids.contains(record.getDynamicId())) {
                ids.add(record.getDynamicId());
            }
        }
        FavoriteVO favoriteVO = new FavoriteVO();
        favoriteVO.setPage(page);
        favoriteVOS.add(favoriteVO);

        if (!ids.isEmpty()) {
            LambdaQueryWrapper<Dynamic> dynamicLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dynamicLambdaQueryWrapper.in(Dynamic::getId, ids);
            List<Dynamic> dynamics = dynamicMapper.selectList(dynamicLambdaQueryWrapper);


            LambdaQueryWrapper<Favorite> favoriteLambdaQueryWrapper = new LambdaQueryWrapper<>();
            favoriteLambdaQueryWrapper.in(Favorite::getDynamicId, ids);
            List<Favorite> favorites = this.list(favoriteLambdaQueryWrapper);

            Map<Long, Integer> collectMap = new HashMap<>(16);

            for (Favorite favorite : favorites) {
                Long dynamicId = favorite.getDynamicId();
                if (collectMap.containsKey(dynamicId)) {
                    collectMap.put(dynamicId, collectMap.get(dynamicId) + 1);
                } else {
                    collectMap.put(dynamicId, 1);
                }
            }

            LambdaQueryWrapper<Play> playLambdaQueryWrapper = new LambdaQueryWrapper<>();
            playLambdaQueryWrapper.in(Play::getDynamicId, ids);
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

            List<Long> userIds = new ArrayList<>();

            Map<Long, Dynamic> dynamicMap = new HashMap<>(16);

            for (Dynamic dynamic : dynamics) {
                if (!userIds.contains(dynamic.getUserId())) {
                    userIds.add(dynamic.getUserId());
                }
                dynamicMap.put(dynamic.getId(), dynamic);
            }

            Map<Long, User> userMap = getUserMap(userIds);

            for (Favorite favorite : records) {
                favoriteVO = new FavoriteVO();
                favoriteVO.setDynamic(dynamicMap.get(favorite.getDynamicId()));
                favoriteVO.setCollectNum(collectMap.getOrDefault(favorite.getDynamicId(), 1));
                favoriteVO.setPlayNum(playMap.getOrDefault(favorite.getDynamicId(), 0));
                favoriteVO.setUser(userMap.get(dynamicMap.get(favorite.getDynamicId()).getUserId()));
                favoriteVO.setCollectTime(favorite.getCreateTime());
                favoriteVOS.add(favoriteVO);
            }
        }
        return favoriteVOS;
    }

    @Override
    public FavoriteVO getFavoriteNum(FavoriteDTO favoriteDTO) {
        Long dynamicId = favoriteDTO.getDynamicId();

        if (dynamicId == null) {
            throw new APIException("获取失败");
        }

        FavoriteVO favoriteVO = new FavoriteVO();

        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getDynamicId, dynamicId);

        List<Favorite> favorites = this.list(queryWrapper);

        favoriteVO.setCollectNum(favorites.size());
        favoriteVO.setIsCollect(false);

        List<Long> favoritesIds = new ArrayList<>();

        for (Favorite favorite : favorites) {
            favoritesIds.add(favorite.getFavoritesId());
        }

        List<Long> favoritesIdsList = new ArrayList<>();

        if (!favoritesIds.isEmpty()) {
            LambdaQueryWrapper<Favorites> favoritesLambdaQueryWrapper = new LambdaQueryWrapper<>();
            favoritesLambdaQueryWrapper.in(Favorites::getId, favoritesIds);
            List<Favorites> favoritesList = favoritesMapper.selectList(favoritesLambdaQueryWrapper);


            for (Favorites favorites1 : favoritesList) {
                if (favorites1.getUserId().equals(UserThreadLocal.get())) {
                    favoritesIdsList.add(favorites1.getId());
                }
            }

            if (!favoritesIdsList.isEmpty()) {
                favoriteVO.setIsCollect(true);
            }
        }
        favoriteVO.setFavoritesIds(favoritesIdsList);

        return favoriteVO;
    }

    @Override
    public void putInFavorite(FavoriteDTO favoriteDTO) {
        Long[] favorites = favoriteDTO.getFavorites();
        Long dynamicId = favoriteDTO.getDynamicId();

        if (favorites == null || dynamicId == null) {
            throw new APIException("收藏失败");
        }


        LambdaQueryWrapper<Favorites> favoritesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoritesLambdaQueryWrapper.eq(Favorites::getUserId, UserThreadLocal.get());
        List<Favorites> favoritess = favoritesMapper.selectList(favoritesLambdaQueryWrapper);

        List<Long> ids = new ArrayList<>();
        for (Favorites favoritess1 : favoritess) {
            ids.add(favoritess1.getId());
        }


        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Favorite::getFavoritesId, ids);
        this.remove(queryWrapper);

        List<Favorite> favoriteList = new ArrayList<>();

        for (Long id : favorites) {
            if (!ids.contains(id)) {
                continue;
            }
            Favorite favorite = new Favorite();
            favorite.setDynamicId(dynamicId);
            favorite.setFavoritesId(id);
            favorite.setCreateTime(LocalDateTime.now());
            favorite.setUpdateTime(LocalDateTime.now());
            favoriteList.add(favorite);
        }

        this.saveBatch(favoriteList);
    }

    @Override
    public FavoriteVO getYesterdayData(Integer type) {
        FavoriteVO favoriteVO = new FavoriteVO();
        favoriteVO.setCollectNum(0);
        DateTime yesterdayDateTime = DateUtil.yesterday();
        String yesterday = yesterdayDateTime.toDateStr();
        String today = DateUtil.offset(yesterdayDateTime, DateField.DAY_OF_YEAR, 1).toDateStr();


        LambdaQueryWrapper<Dynamic> dynamicLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dynamicLambdaQueryWrapper.eq(Dynamic::getUserId, UserThreadLocal.get());
        dynamicLambdaQueryWrapper.eq(Dynamic::getType, type);
        List<Dynamic> dynamicList = dynamicMapper.selectList(dynamicLambdaQueryWrapper);

        List<Long> dynamicIds = new ArrayList<>();

        for (Dynamic dynamic : dynamicList) {
            dynamicIds.add(dynamic.getId());
        }

        if (!dynamicIds.isEmpty()) {
            LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.ge(Favorite::getCreateTime, yesterday);
            queryWrapper.lt(Favorite::getCreateTime, today);
            queryWrapper.in(Favorite::getDynamicId, dynamicIds);

            favoriteVO.setCollectNum(this.count(queryWrapper));
        }
        return favoriteVO;
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