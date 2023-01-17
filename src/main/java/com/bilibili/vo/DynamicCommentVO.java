package com.bilibili.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bilibili.pojo.DynamicComment;
import com.bilibili.pojo.User;
import lombok.Data;


/**
 * @author xck
 */
@Data
public class DynamicCommentVO {

    private DynamicComment comment;
    private User user;
    private User replyUser;
    private DynamicCommentVO replyCommentVO;
    private Page<DynamicComment> page;
    private Integer commentNum;
}
