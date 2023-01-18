package com.bilibili.controller;

import com.bilibili.dto.DynamicCommentDTO;
import com.bilibili.pojo.DynamicComment;
import com.bilibili.service.DynamicCommentService;
import com.bilibili.vo.DynamicCommentVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xck
 */
@RestController
@RequestMapping("/comment")
public class DynamicCommentController {

    private final DynamicCommentService dynamicCommentService;

    public DynamicCommentController(DynamicCommentService dynamicCommentService) {
        this.dynamicCommentService = dynamicCommentService;
    }

    @PostMapping("/")
    public List<DynamicCommentVO> page(@RequestBody @Validated DynamicCommentDTO dynamicCommentDTO) {
        return dynamicCommentService.getByPage(dynamicCommentDTO);
    }

    @PostMapping("/addComment")
    public DynamicComment addComment(@RequestBody DynamicComment dynamicComment) {
        return dynamicCommentService.addComment(dynamicComment);
    }

    @PostMapping("/getReply")
    public List<DynamicCommentVO> getReply(@RequestBody DynamicCommentDTO dynamicCommentDTO) {
        return dynamicCommentService.getReply(dynamicCommentDTO);
    }

    @GetMapping("/getCommentCount/{id}")
    public DynamicCommentVO getCommentCount(@PathVariable("id") Long id) {
        return dynamicCommentService.getCommentCount(id);
    }

    @GetMapping("/getYesterdayData/{type}")
    public DynamicCommentVO getYesterdayData(@PathVariable("type") Integer type) {
        return dynamicCommentService.getYesterdayData(type);
    }
}
