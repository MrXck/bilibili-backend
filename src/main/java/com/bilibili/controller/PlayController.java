package com.bilibili.controller;

import com.bilibili.dto.PlayDTO;
import com.bilibili.service.PlayService;
import com.bilibili.vo.PlayVO;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author xck
 */
@RestController
@RequestMapping("/play")
public class PlayController {

    private final PlayService playService;

    public PlayController(PlayService playService) {
        this.playService = playService;
    }

    @PostMapping("/page")
    public List<PlayVO> page(@RequestBody PlayDTO playDTO) {
        return playService.getByPage(playDTO);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) throws Exception {
        playService.delete(id);
        return "";
    }

    @GetMapping("/getPlayNum/{id}")
    public PlayVO getPlayNumByDynamicId(@PathVariable("id") Long id) {
        return playService.getPlayNumByDynamicId(id);
    }

    @PostMapping("/addPlay")
    public String addPlay(@RequestBody PlayDTO playDTO) {
        playService.addPlay(playDTO);
        return "";
    }

    @GetMapping("/getYesterdayData/{type}")
    public PlayVO getYesterdayData(@PathVariable("type") Integer type) {
        return playService.getYesterdayData(type);
    }

    @GetMapping("/getAllData/{type}")
    public PlayVO getAllData(@PathVariable("type") Integer type) {
        return playService.getAllData(type);
    }

    @GetMapping("/getLastSevenDaysData/{type}")
    public LinkedHashMap<String, Long> getLastSevenDaysData(@PathVariable("type") Integer type) {
        return playService.getLastSevenDaysData(type);
    }
}
