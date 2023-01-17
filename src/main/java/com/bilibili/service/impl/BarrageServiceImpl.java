package com.bilibili.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibili.exception.APIException;
import com.bilibili.mapper.BarrageMapper;
import com.bilibili.pojo.Barrage;
import com.bilibili.service.BarrageService;
import com.bilibili.utils.UserThreadLocal;
import com.bilibili.vo.BarrageVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xck
 */
@Service
public class BarrageServiceImpl extends ServiceImpl<BarrageMapper, Barrage> implements BarrageService {

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
}