package com.bilibili.controller;

import com.bilibili.dto.LaterDTO;
import com.bilibili.service.LaterService;
import com.bilibili.vo.LaterVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xck
 */
@RestController
@RequestMapping("/later")
public class LaterController {

    private final LaterService laterService;

    public LaterController(LaterService laterService) {
        this.laterService = laterService;
    }

    @GetMapping("/list")
    public List<LaterVO> list() {
        return laterService.getAll();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        laterService.delete(id);
        return "";
    }

    @GetMapping("/deleteAll")
    public String deleteAll() {
        laterService.deleteAll();
        return "";
    }

    @GetMapping("/deleteView")
    public String deleteView() {
        laterService.deleteView();
        return "";
    }

    @PostMapping("/add")
    public String addLater(@RequestBody LaterDTO laterDTO){
        laterService.addLater(laterDTO);
        return "";
    }
}
