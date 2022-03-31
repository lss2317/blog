package com.lss.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lss.constant.RedisPrefixConst;
import com.lss.entity.UniqueView;
import com.lss.mapper.UniqueViewMapper;
import com.lss.service.RedisService;
import com.lss.service.UniqueViewService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * @author lss
 * @create 2022年03月30日 22:24
 */
@Service
public class UniqueViewServiceImpl extends ServiceImpl<UniqueViewMapper, UniqueView> implements UniqueViewService {

    @Resource
    RedisService redisService;
    @Resource
    UniqueViewMapper uniqueViewMapper;

    /**
     * 每日自动添加网站访问记录
     */
    @Scheduled(cron = " 0 0 0 * * ?", zone = "Asia/Shanghai")
    public void saveUniqueView() {
        // 获取每天用户量
        Long count = redisService.sSize(RedisPrefixConst.UNIQUE_VISITOR);
        // 获取昨天日期插入数据
        UniqueView view = new UniqueView();
        if (count == null) {
            view.setViewsCount(0);
        } else {
            view.setViewsCount(count.intValue());
        }
        view.setCreateTime(LocalDateTimeUtil.offset(LocalDateTime.now(ZoneId.of("Asia/Shanghai")), -1, ChronoUnit.DAYS));
        this.save(view);
    }

    /**
     * 清空访问数据
     */
    @Scheduled(cron = " 0 1 0 * * ?", zone = "Asia/Shanghai")
    public void clear() {
        // 清空redis访客记录
        redisService.del(RedisPrefixConst.UNIQUE_VISITOR);
    }

    @Override
    public List<UniqueView> listUniqueViews() {
        DateTime startTime = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), -7));
        DateTime endTime = DateUtil.endOfDay(new Date());
        return uniqueViewMapper.listUniqueViews(startTime,endTime);
    }
}
