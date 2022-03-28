package com.lss.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lss.entity.Talk;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 说说交互层
 *
 * @author lss
 * @create 2022年03月10日 21:00
 */
public interface TalkMapper extends BaseMapper<Talk> {

    /**
     * 获取说说列表，按照发布时间从晚到早
     *
     * @param currentPage 页码
     * @param pageSize    每页展示个数
     * @param status      状态 1.公开、2.私有
     */
    List<Talk> listTalks(@Param("currentPage") Integer currentPage, @Param("pageSize") Integer pageSize, @Param("status") Integer status);
}
