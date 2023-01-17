package com.bilibili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibili.dto.FavoritesDTO;
import com.bilibili.exception.APIException;
import com.bilibili.mapper.FavoritesMapper;
import com.bilibili.pojo.Favorite;
import com.bilibili.pojo.Favorites;
import com.bilibili.service.FavoriteService;
import com.bilibili.service.FavoritesService;
import com.bilibili.utils.UserThreadLocal;
import com.bilibili.vo.FavoritesVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bilibili.utils.Constant.PUBLIC_FAVORITES;

/**
 * @author xck
 */
@Service
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements FavoritesService {

    private final FavoriteService favoriteService;

    public FavoritesServiceImpl(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Override
    public List<FavoritesVO> getList(Long id) {
        Long userId = UserThreadLocal.get();

        LambdaQueryWrapper<Favorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorites::getUserId, id);
        queryWrapper.orderByAsc(Favorites::getCreateTime);

        if (!userId.equals(id)) {
            queryWrapper.eq(Favorites::getIsPublic, PUBLIC_FAVORITES);
        }

        List<FavoritesVO> favoritesVOS = new ArrayList<>();

        List<Favorites> list = this.list(queryWrapper);

        List<Long> ids = new ArrayList<>();

        for (Favorites favorites : list) {
            ids.add(favorites.getId());

            FavoritesVO favoritesVO = new FavoritesVO();
            favoritesVO.setFavorites(favorites);

            favoritesVOS.add(favoritesVO);
        }

        if (!ids.isEmpty()){
            LambdaQueryWrapper<Favorite> favoriteLambdaQueryWrapper = new LambdaQueryWrapper<>();
            favoriteLambdaQueryWrapper.in(Favorite::getFavoritesId, ids);

            List<Favorite> favoriteList = favoriteService.list(favoriteLambdaQueryWrapper);

            Map<Long, Integer> map = new HashMap<>(16);

            for (Favorite favorite : favoriteList) {
                Long favoritesId = favorite.getFavoritesId();
                if (map.containsKey(favoritesId)){
                    map.put(favoritesId, map.get(favoritesId) + 1);
                } else {
                    map.put(favoritesId, 1);
                }
            }

            for (FavoritesVO favoritesVO : favoritesVOS) {
                Long favoritesId = favoritesVO.getFavorites().getId();
                favoritesVO.setNum(map.getOrDefault(favoritesId, 0));
            }
        }

        return favoritesVOS;
    }

    @Override
    public void updateFavorites(FavoritesDTO favoritesDTO) throws Exception {
        Long userId = UserThreadLocal.get();

        Favorites favorites = this.getById(favoritesDTO.getFavorites().getId());

        Long userId1 = favorites.getUserId();

        if (userId1.equals(userId)){
            if ("".equals(favoritesDTO.getFavorites().getName())) {
                throw new APIException("修改失败");
            }


            favorites.setName(favoritesDTO.getFavorites().getName());
            favorites.setIsPublic(favoritesDTO.getFavorites().getIsPublic());
            favorites.setUpdateTime(LocalDateTime.now());
            this.updateById(favorites);
        } else {
            throw new Exception();
        }


    }

    @Override
    public void create(FavoritesDTO favoritesDTO) {
        String name = favoritesDTO.getFavorites().getName();
        if ("".equals(name) || name == null) {
            throw new APIException("创建失败");
        }


        Favorites favorites = favoritesDTO.getFavorites();
        favorites.setUserId(UserThreadLocal.get());
        favorites.setCreateTime(LocalDateTime.now());
        favorites.setUpdateTime(LocalDateTime.now());

        this.save(favorites);
    }
}