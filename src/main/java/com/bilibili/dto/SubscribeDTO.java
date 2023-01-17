package com.bilibili.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SubscribeDTO {

    private Long userId;

    @NotNull(message = "页数不能为空")
    @Min(message = "请输入正确的页数", value = 1)
    private Integer pageNum;

    @NotNull(message = "页大小不能为空")
    @Min(message = "请输入正确的页大小", value = 1)
    private Integer pageSize;
}
