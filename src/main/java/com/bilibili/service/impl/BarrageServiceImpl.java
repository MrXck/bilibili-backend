package com.bilibili.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibili.exception.APIException;
import com.bilibili.mapper.BarrageMapper;
import com.bilibili.mapper.DynamicMapper;
import com.bilibili.pojo.Barrage;
import com.bilibili.pojo.Dynamic;
import com.bilibili.service.BarrageService;
import com.bilibili.utils.UserThreadLocal;
import com.bilibili.vo.BarrageVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.bilibili.utils.Constant.VIDEO_TYPE;

/**
 * @author xck
 */
@Service
public class BarrageServiceImpl extends ServiceImpl<BarrageMapper, Barrage> implements BarrageService {

    private final DynamicMapper dynamicMapper;

    public BarrageServiceImpl(DynamicMapper dynamicMapper) {
        this.dynamicMapper = dynamicMapper;
    }

    @Override
    public List<BarrageVO> getByDynamicId(Long id) {

        LambdaQueryWrapper<Barrage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Barrage::getDynamicId, id);
        queryWrapper.orderByAsc(Barrage::getId);

        List<Barrage> list = this.list(queryWrapper);

        List<BarrageVO> barrageVOS = new ArrayList<>();

        for (Barrage barrage : list) {
            BarrageVO barrageVO = new BarrageVO();
            barrageVO.setBarrage(barrage);
            barrageVO.setIsMe(barrage.getUserId().equals(UserThreadLocal.get()));
            barrageVOS.add(barrageVO);
        }


        return barrageVOS;
    }

    @Override
    public void sendBarrage(Barrage barrage) {
        if (barrage.getDynamicId() == null) {
            throw new APIException("发送失败");
        }


        barrage.setUserId(UserThreadLocal.get());
        barrage.setCreateTime(LocalDateTime.now());
        barrage.setUpdateTime(LocalDateTime.now());
        this.save(barrage);
    }

    @Override
    public BarrageVO getBarrageNumByDynamicId(Long id) {
        BarrageVO barrageVO = new BarrageVO();

        LambdaQueryWrapper<Barrage> barrageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        barrageLambdaQueryWrapper.eq(Barrage::getDynamicId, id);
        barrageVO.setBarrageNum(this.count(barrageLambdaQueryWrapper));

        return barrageVO;
    }

    @Override
    public BarrageVO getYesterdayData(Integer type) {
        BarrageVO barrageVO = new BarrageVO();
        barrageVO.setBarrageNum(0);
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
            LambdaQueryWrapper<Barrage> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.ge(Barrage::getCreateTime, yesterday);
            queryWrapper.lt(Barrage::getCreateTime, today);
            queryWrapper.in(Barrage::getDynamicId, dynamicIds);

            barrageVO.setBarrageNum(this.count(queryWrapper));
        }
        return barrageVO;
    }
}