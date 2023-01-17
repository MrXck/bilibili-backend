package com.bilibili.vo;

import com.bilibili.pojo.User;
import lombok.Data;

import java.util.List;

/**
 * @author xck
 */
@Data
public class HistoryVO {

    private List<User> users;
}
