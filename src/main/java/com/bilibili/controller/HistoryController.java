package com.bilibili.controller;

import com.bilibili.dto.HistoryDTO;
import com.bilibili.service.HistoryService;
import com.bilibili.vo.HistoryVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/page")
    public HistoryVO page(@RequestBody @Validated HistoryDTO historyDTO) {
        return historyService.getHistory(historyDTO);
    }

    @PostMapping("/addHistory")
    public String addHistory(@RequestBody HistoryDTO historyDTO) {
        historyService.addHistory(historyDTO.getToId(), null);
        return "";
    }

    @PostMapping("/removeHistory")
    public String removeHistory(@RequestBody HistoryDTO historyDTO) {
        historyService.removeHistory(historyDTO.getId());
        return "";
    }
}
