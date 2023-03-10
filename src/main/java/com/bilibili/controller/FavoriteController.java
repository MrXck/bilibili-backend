package com.bilibili.controller;

import com.bilibili.dto.FavoriteDTO;
import com.bilibili.service.FavoriteService;
import com.bilibili.vo.FavoriteVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xck
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/page")
    public List<FavoriteVO> page(@RequestBody FavoriteDTO favoriteDTO) {
        return favoriteService.getByPage(favoriteDTO);
    }

    @PostMapping("/getFavoriteNum")
    public FavoriteVO getFavoriteNum(@RequestBody FavoriteDTO favoriteDTO) {
        return favoriteService.getFavoriteNum(favoriteDTO);
    }

    @PostMapping("/putInFavorite")
    public String putInFavorite(@RequestBody FavoriteDTO favoriteDTO) {
        favoriteService.putInFavorite(favoriteDTO);
        return "";
    }

    @GetMapping("/getYesterdayData/{type}")
    public FavoriteVO getYesterdayData(@PathVariable("type") Integer type) {
        return favoriteService.getYesterdayData(type);
    }
}
