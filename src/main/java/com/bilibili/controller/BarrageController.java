package com.bilibili.controller;

import com.bilibili.pojo.Barrage;
import com.bilibili.service.BarrageService;
import com.bilibili.vo.BarrageVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xck
 */
@RestController
@RequestMapping("/barrage")
public class BarrageController {

    private final BarrageService barrageService;

    public BarrageController(BarrageService barrageService) {
        this.barrageService = barrageService;
    }

    @GetMapping("/{id}")
    public List<BarrageVO> getByDynamicId(@PathVariable("id") Long id) {
        return barrageService.getByDynamicId(id);
    }

    @PostMapping("/sendBarrage")
    public String sendBarrage(@RequestBody Barrage barrage){
        barrageService.sendBarrage(barrage);
        return "";
    }

    @GetMapping("/getBarrageNum/{id}")
    public BarrageVO getBarrageNumByDynamicId(@PathVariable("id") Long id){
        return barrageService.getBarrageNumByDynamicId(id);
    }
}
