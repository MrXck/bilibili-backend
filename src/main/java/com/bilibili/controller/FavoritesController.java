package com.bilibili.controller;

import com.bilibili.dto.FavoritesDTO;
import com.bilibili.service.FavoritesService;
import com.bilibili.vo.FavoritesVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xck
 */
@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @GetMapping("/list/{id}")
    public List<FavoritesVO> list(@PathVariable("id") Long id){
        return favoritesService.getList(id);
    }

    @PostMapping("/updateFavorites")
    public String updateFavorites(@RequestBody FavoritesDTO favoritesDTO) throws Exception{
        favoritesService.updateFavorites(favoritesDTO);
        return "";
    }

    @PostMapping("/createFavorites")
    public String create(@RequestBody FavoritesDTO favoritesDTO){
        favoritesService.create(favoritesDTO);
        return "";
    }
}
