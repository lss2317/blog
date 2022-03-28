package com.lss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lss.common.Result;
import com.lss.entity.Tag;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 标签服务类
 *
 * @author lss
 * @create 2022年02月11日 15:58
 */
public interface TagService extends IService<Tag> {

    /**
     * 分页查询
     *
     * @param pageSize    每页展示总数
     * @param currentPage 页码
     */
    List<Tag> listTag(@Param("pageSize") Integer pageSize, @Param("currentPage") Integer currentPage);

    /**
     * 分页查询，根据关键词
     *
     * @param pageSize    每页展示总数
     * @param currentPage 页码
     * @param keywords    标签名关键词
     */
    List<Tag> listTagByTagName(@Param("pageSize") Integer pageSize, @Param("currentPage") Integer currentPage, @Param("keywords") String keywords);

    /**
     * 修改标签
     *
     * @param tag     标签实体类
     * @param request 请求，获取token
     */
    Result<?> modifyTag(Tag tag, HttpServletRequest request);

    /**
     * 添加标签
     *
     * @param tag     标签实体类
     * @param request 请求，获取token
     */
    Result<?> addTag(Tag tag, HttpServletRequest request);

    /**
     * 删除标签
     *
     * @param id      标签id
     * @param request 请求，用来获取token
     */
    Result<?> deleteTag(@Param("id") Integer id, HttpServletRequest request);

    /**
     * 批量删除标签
     *
     * @param tagIdList 删除的标签id集合
     * @param request   请求，用来获取token
     */
    Result<?> deleteTags(List<Integer> tagIdList, HttpServletRequest request);
}
