package com.bilibili.controller;

import com.bilibili.dto.StarDTO;
import com.bilibili.pojo.Star;
import com.bilibili.service.StarService;
import com.bilibili.vo.StarVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xck
 */
@RestController
@RequestMapping("/star")
public class StarController {

    private final StarService starService;

    public StarController(StarService starService) {
        this.starService = starService;
    }

    @GetMapping("/getDynamicStarNum/{id}")
    public StarVO getDynamicStarNum(@PathVariable("id") Long id){
        return starService.getDynamicStarNum(id);
    }

    @GetMapping("/getCommentStarNum/{id}")
    public StarVO getCommentStarNum(@PathVariable("id") Long id){
        return starService.getCommentStarNum(id);
    }

    @PostMapping("/star")
    public String star(@RequestBody Star star){
        starService.star(star);
        return "";
    }

    @PostMapping("/unStar")
    public String unStar(@RequestBody Star star){
        starService.unStar(star);
        return "";
    }

    @PostMapping("/getStar")
    public List<StarVO> getStar(@RequestBody StarDTO starDTO) {
        return starService.getStar(starDTO);
    }

}
