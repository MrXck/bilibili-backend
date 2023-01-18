package com.bilibili.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibili.dto.DynamicCommentDTO;
import com.bilibili.exception.APIException;
import com.bilibili.mapper.DynamicCommentMapper;
import com.bilibili.mapper.DynamicMapper;
import com.bilibili.mapper.UserMapper;
import com.bilibili.pojo.Dynamic;
import com.bilibili.pojo.DynamicComment;
import com.bilibili.pojo.User;
import com.bilibili.service.DynamicCommentService;
import com.bilibili.utils.UserThreadLocal;
import com.bilibili.vo.DynamicCommentVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bilibili.utils.Constant.VIDEO_TYPE;

/**
 * @author xck
 */
@Service
public class DynamicCommentServiceImpl extends ServiceImpl<DynamicCommentMapper, DynamicComment> implements DynamicCommentService {

    private final UserMapper userMapper;
    private final DynamicMapper dynamicMapper;

    public DynamicCommentServiceImpl(UserMapper userMapper, DynamicMapper dynamicMapper) {
        this.userMapper = userMapper;
        this.dynamicMapper = dynamicMapper;
    }

    @Override
    public List<DynamicCommentVO> getByPage(DynamicCommentDTO dynamicCommentDTO) {
        Page<DynamicComment> page = new Page<>(dynamicCommentDTO.getPageNum(), dynamicCommentDTO.getPageSize());
        LambdaQueryWrapper<DynamicComment> queryWrapper = new LambdaQueryWrapper<>();

        Long dynamicId = dynamicCommentDTO.getDynamicId();
        Long rootId = dynamicCommentDTO.getRootId();
        Long replyId = dynamicCommentDTO.getReplyId();

        queryWrapper.eq(DynamicComment::getDynamicId, dynamicId);

        // rootId 为动态下的第一层 评论
        if (rootId != null) {
            queryWrapper.eq(DynamicComment::getRootId, rootId);
        } else {
            queryWrapper.isNull(DynamicComment::getRootId);
        }

        // replyId 为动态下的第二层 评论
        if (replyId != null) {
            queryWrapper.eq(DynamicComment::getReplyId, replyId);
        }

        List<Long> userIds = new ArrayList<>();

        Page<DynamicComment> dynamicCommentPage = this.page(page, queryWrapper);
        List<DynamicCommentVO> list = dynamicCommentPage.getRecords().stream().map(item -> {
            DynamicCommentVO commentVO = new DynamicCommentVO();
            if (!userIds.contains(item.getUserId())) {
                userIds.add(item.getUserId());
            }

            if (!userIds.contains(item.getToUserId()) && item.getReplyId() != null) {
                userIds.add(item.getToUserId());
            }

            commentVO.setComment(item);
            return commentVO;
        }).collect(Collectors.toList());

        getDynamicCommentVOS(getUserMap(userIds), list);

        DynamicCommentVO dynamicCommentVO = new DynamicCommentVO();
        dynamicCommentVO.setPage(dynamicCommentPage);
        list.add(0, dynamicCommentVO);
        return list;
    }

    @Override
    public DynamicComment addComment(DynamicComment dynamicComment) {
        if (dynamicComment.getDynamicId() == null) {
            throw new APIException("发送失败");
        }

        Long userId = UserThreadLocal.get();
        dynamicComment.setUserId(userId);
        dynamicComment.setCreateTime(LocalDateTime.now());
        this.save(dynamicComment);
        return dynamicComment;
    }

    @Override
    public List<DynamicCommentVO> getReply(DynamicCommentDTO dynamicCommentDTO) {
        Long userId = UserThreadLocal.get();

        User user = userMapper.selectById(userId);

        // 分页查询回复
        LambdaQueryWrapper<DynamicComment> queryWrapper = new LambdaQueryWrapper<>();
        Long commentId = dynamicCommentDTO.getCommentId();
        if (commentId != null) {
            queryWrapper.lt(DynamicComment::getId, commentId);
        }

        queryWrapper.eq(DynamicComment::getToUserId, userId);
        queryWrapper.orderByDesc(DynamicComment::getCreateTime);
        Page<DynamicComment> page = new Page<>(1, dynamicCommentDTO.getPageSize());
        Page<DynamicComment> dynamicCommentPage = this.page(page, queryWrapper);


        List<Long> userIds = new ArrayList<>();
        List<Long> dynamicCommentIds = new ArrayList<>();


        List<DynamicCommentVO> dynamicCommentVOList = dynamicCommentPage.getRecords().stream().map(item -> {
            DynamicCommentVO dynamicCommentVO = new DynamicCommentVO();
            dynamicCommentVO.setComment(item);
            if (!userIds.contains(item.getUserId())) {
                userIds.add(item.getUserId());
            }

            if (item.getReplyId() != null && !dynamicCommentIds.contains(item.getReplyId())) {
                dynamicCommentIds.add(item.getReplyId());
            }

            return dynamicCommentVO;
        }).collect(Collectors.toList());

        Map<Long, User> userMap = null;

        if (!dynamicCommentIds.isEmpty()) {
            LambdaQueryWrapper<DynamicComment> dynamicCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dynamicCommentLambdaQueryWrapper.in(DynamicComment::getId, dynamicCommentIds);
            List<DynamicComment> dynamicComments = this.list(dynamicCommentLambdaQueryWrapper);

            dynamicComments.stream().peek(item -> {
                if (!userIds.contains(item.getToUserId())) {
                    userIds.add(item.getToUserId());
                }
            });

            userMap = getUserMap(userIds);

            for (DynamicCommentVO dynamicCommentVO : dynamicCommentVOList) {
                for (DynamicComment dynamicComment : dynamicComments) {
                    if (dynamicComment.getId().equals(dynamicCommentVO.getComment().getReplyId())) {
                        DynamicCommentVO commentVO = new DynamicCommentVO();
                        commentVO.setComment(dynamicComment);
                        commentVO.setUser(user);
                        commentVO.setReplyUser(userMap.get(dynamicComment.getToUserId()));
                        dynamicCommentVO.setReplyCommentVO(commentVO);
                    }

                }
            }
        }

        if (userMap == null) {
            userMap = getUserMap(userIds);
        }


        getDynamicCommentVOS(userMap, dynamicCommentVOList, user);

        return dynamicCommentVOList;
    }

    @Override
    public DynamicCommentVO getCommentCount(Long id) {
        DynamicCommentVO dynamicCommentVO = new DynamicCommentVO();
        LambdaQueryWrapper<DynamicComment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(DynamicComment::getDynamicId, id);

        dynamicCommentVO.setCommentNum(this.count(commentLambdaQueryWrapper));
        return dynamicCommentVO;
    }

    @Override
    public DynamicCommentVO getYesterdayData(Integer type) {
        DynamicCommentVO dynamicCommentVO = new DynamicCommentVO();
        dynamicCommentVO.setCommentNum(0);
        DateTime yesterdayDateTime = DateUtil.yesterday();
        String yesterday = yesterdayDateTime.toDateStr();
        String today = DateUtil.offset(yesterdayDateTime, DateField.DAY_OF_YEAR, 1).toDateStr();


        LambdaQueryWrapper<Dynamic> dynamicLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dynamicLambdaQueryWrapper.eq(Dynamic::getUserId, UserThreadLocal.get());
        dynamicLambdaQueryWrapper.eq(Dynamic::getType, type);
        List<Dynamic> dynamicList = dynamicMapper.selectList(dynamicLambdaQueryWrapper);

        List<Long> dynamicIds = new ArrayList<>();

        for (Dynamic dynamic : dynamicList) {
            dynamicIds.add(dynamic.getId());
        }

        if (!dynamicIds.isEmpty()) {
            LambdaQueryWrapper<DynamicComment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.ge(DynamicComment::getCreateTime, yesterday);
            queryWrapper.lt(DynamicComment::getCreateTime, today);
            queryWrapper.in(DynamicComment::getDynamicId, dynamicIds);

            dynamicCommentVO.setCommentNum(this.count(queryWrapper));
        }
        return dynamicCommentVO;
    }

    private void getDynamicCommentVOS(Map<Long, User> userMap, List<DynamicCommentVO> dynamicCommentVOList) {
        for (DynamicCommentVO dynamicCommentVO : dynamicCommentVOList) {
            Long userId = dynamicCommentVO.getComment().getUserId();
            if (userMap.containsKey(userId)) {
                dynamicCommentVO.setUser(userMap.get(userId));
            }
            Long toUserId = dynamicCommentVO.getComment().getToUserId();
            if (dynamicCommentVO.getComment().getReplyId() != null && userMap.containsKey(toUserId)) {
                dynamicCommentVO.setReplyUser(userMap.get(toUserId));
            }
        }
    }

    private void getDynamicCommentVOS(Map<Long, User> userMap, List<DynamicCommentVO> dynamicCommentVOList, User user) {
        for (DynamicCommentVO dynamicCommentVO : dynamicCommentVOList) {
            Long userId = dynamicCommentVO.getComment().getUserId();
            if (userMap.containsKey(userId)) {
                dynamicCommentVO.setUser(userMap.get(userId));
            }
            dynamicCommentVO.setReplyUser(user);
        }
    }

    private Map<Long, User> getUserMap(List<Long> userIds) {
        Map<Long, User> userMap = new HashMap<>(16);

        if (!userIds.isEmpty()) {
            // 解决 n + 1 问题
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.in(User::getId, userIds);
            List<User> userList = userMapper.selectList(userLambdaQueryWrapper);

            for (User user : userList) {
                userMap.put(user.getId(), user);
            }
        }

        return userMap;
    }

}