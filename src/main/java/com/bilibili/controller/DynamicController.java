package com.bilibili.controller;

import com.bilibili.dto.DynamicDTO;
import com.bilibili.pojo.Dynamic;
import com.bilibili.service.DynamicService;
import com.bilibili.vo.DynamicVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xck
 */
@RestController
@RequestMapping("/dynamic")
public class DynamicController {

    private final DynamicService dynamicService;

    public DynamicController(DynamicService dynamicService) {
        this.dynamicService = dynamicService;
    }

    @PostMapping("/")
    public List<DynamicVO> getDynamicByUserId(@RequestBody DynamicDTO dynamicDTO) {
        return dynamicService.getDynamicByUserId(dynamicDTO);
    }

    @PostMapping("/search")
    public List<DynamicVO> search(@RequestBody DynamicDTO dynamicDTO) {
        return dynamicService.search(dynamicDTO);
    }

    @PostMapping("/getDynamicById")
    public DynamicVO getDynamicById(@RequestBody DynamicDTO dynamicDTO) {
        return dynamicService.getDynamicById(dynamicDTO);
    }

    @PostMapping("/submit")
    public String submit(@RequestBody Dynamic dynamic) {
        dynamicService.submit(dynamic);
        return "";
    }

    @GetMapping("/getDynamicNum")
    public DynamicVO getDynamicNum() {
        return dynamicService.getDynamicNum();
    }

    @PostMapping("/getVideoDynamic")
    public List<DynamicVO> getVideoDynamic(@RequestBody DynamicDTO dynamicDTO) {
        return dynamicService.getVideoDynamic(dynamicDTO);
    }

    @GetMapping("/getVideoDynamicNum/{id}")
    public DynamicVO getVideoDynamicNum(@PathVariable("id") Long id) {
        return dynamicService.getVideoDynamicNum(id);
    }

}
