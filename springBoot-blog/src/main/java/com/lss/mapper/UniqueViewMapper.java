package com.lss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lss.entity.UniqueView;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 网站访问量
 *
 * @author lss
 * @create 2022年03月30日 22:23
 */
public interface UniqueViewMapper extends BaseMapper<UniqueView> {

    /**
     * 获取7天用户量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 用户量
     */
    List<UniqueView> listUniqueViews(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
