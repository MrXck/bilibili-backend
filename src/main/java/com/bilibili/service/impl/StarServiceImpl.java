package com.bilibili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibili.dto.StarDTO;
import com.bilibili.exception.APIException;
import com.bilibili.mapper.DynamicCommentMapper;
import com.bilibili.mapper.DynamicMapper;
import com.bilibili.mapper.StarMapper;
import com.bilibili.mapper.UserMapper;
import com.bilibili.pojo.Dynamic;
import com.bilibili.pojo.DynamicComment;
import com.bilibili.pojo.Star;
import com.bilibili.pojo.User;
import com.bilibili.service.StarService;
import com.bilibili.utils.UserThreadLocal;
import com.bilibili.vo.StarVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xck
 */
@Service
public class StarServiceImpl extends ServiceImpl<StarMapper, Star> implements StarService {

    private final UserMapper userMapper;
    private final DynamicCommentMapper dynamicCommentMapper;
    private final DynamicMapper dynamicMapper;

    public StarServiceImpl(UserMapper userMapper, DynamicCommentMapper dynamicCommentMapper, DynamicMapper dynamicMapper) {
        this.userMapper = userMapper;
        this.dynamicCommentMapper = dynamicCommentMapper;
        this.dynamicMapper = dynamicMapper;
    }

    @Override
    public StarVO getDynamicStarNum(Long id) {
        StarVO starVO = new StarVO();

        LambdaQueryWrapper<Star> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Star::getDynamicId, id);
        starVO.setStarNum(this.count(queryWrapper));

        queryWrapper.eq(Star::getUserId, UserThreadLocal.get());
        starVO.setIsStar(this.count(queryWrapper) > 0);

        return starVO;
    }

    @Override
    public StarVO getCommentStarNum(Long id) {
        StarVO starVO = new StarVO();

        LambdaQueryWrapper<Star> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Star::getCommentId, id);
        starVO.setStarNum(this.count(queryWrapper));

        queryWrapper.eq(Star::getUserId, UserThreadLocal.get());
        starVO.setIsStar(this.count(queryWrapper) > 0);

        return starVO;
    }

    @Override
    public void star(Star star) {
        if (star.getDynamicId() == null && star.getCommentId() == null) {
            throw new APIException("点赞失败");
        }

        star.setCreateTime(LocalDateTime.now());
        star.setUserId(UserThreadLocal.get());
        this.save(star);
    }

    @Override
    public void unStar(Star star) {
        Long commentId = star.getCommentId();
        Long dynamicId = star.getDynamicId();
        if (dynamicId == null && commentId == null) {
            throw new APIException("点赞失败");
        }

        LambdaQueryWrapper<Star> queryWrapper = new LambdaQueryWrapper<>();
        if (commentId != null) {
            queryWrapper.eq(Star::getCommentId, commentId);
        } else {
            queryWrapper.eq(Star::getDynamicId, dynamicId);
        }
        queryWrapper.eq(Star::getUserId, UserThreadLocal.get());

        this.remove(queryWrapper);
    }

    @Override
    public List<StarVO> getStar(StarDTO starDTO) {
        Long userId = UserThreadLocal.get();

        Page<Star> page = new Page<>(1, starDTO.getPageSize());
        LambdaQueryWrapper<Star> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Star::getIsStaredUserId, userId);
        Page<Star> starPage = this.page(page, queryWrapper);

        List<Star> records = starPage.getRecords();

        List<Long> userIds = new ArrayList<>();
        List<Long> dynamicIds = new ArrayList<>();
        List<Long> dynamicCommentIds = new ArrayList<>();
        for (Star record : records) {
            userIds.add(record.getUserId());
            Long commentId = record.getCommentId();
            Long dynamicId = record.getDynamicId();
            if (commentId != null) {
                dynamicCommentIds.add(commentId);
            }

            if (dynamicId != null) {
                dynamicIds.add(dynamicId);
            }
        }

        Map<Long, User> userMap = getUserMap(userIds);
        Map<Long, DynamicComment> dynamicCommentMap = getDynamicCommentMap(dynamicCommentIds);
        Map<Long, Dynamic> dynamicMap = getDynamicMap(dynamicIds);

        List<StarVO> starVOS = new ArrayList<>();

        for (Star record : records) {
            Long userId1 = record.getUserId();
            Long commentId = record.getCommentId();
            Long dynamicId = record.getDynamicId();

            StarVO starVO = new StarVO();

            starVO.setUser(userMap.get(userId1));
            starVO.setStar(record);

            if (commentId != null) {
                starVO.setComment(dynamicCommentMap.get(commentId));
            }

            if (dynamicId != null) {
                starVO.setDynamic(dynamicMap.get(dynamicId));
            }

            starVOS.add(starVO);
        }

        return starVOS;
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

    private Map<Long, Dynamic> getDynamicMap(List<Long> dynamicIds) {
        Map<Long, Dynamic> dynamicMap = new HashMap<>(16);

        if (!dynamicIds.isEmpty()) {
            // 解决 n + 1 问题
            LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Dynamic::getId, dynamicIds);
            List<Dynamic> dynamicList = dynamicMapper.selectList(queryWrapper);

            for (Dynamic dynamic : dynamicList) {
                dynamicMap.put(dynamic.getId(), dynamic);
            }
        }

        return dynamicMap;
    }

    private Map<Long, DynamicComment> getDynamicCommentMap(List<Long> dynamicCommentIds) {
        Map<Long, DynamicComment> dynamicCommentMap = new HashMap<>(16);

        if (!dynamicCommentIds.isEmpty()) {
            // 解决 n + 1 问题
            LambdaQueryWrapper<DynamicComment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(DynamicComment::getId, dynamicCommentIds);
            List<DynamicComment> dynamicComments = dynamicCommentMapper.selectList(queryWrapper);

            for (DynamicComment dynamicComment : dynamicComments) {
                dynamicCommentMap.put(dynamicComment.getId(), dynamicComment);
            }
        }

        return dynamicCommentMap;
    }
}