package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.lss.annotation.OptLog;
import com.lss.common.Result;
import com.lss.constant.OptTypeConst;
import com.lss.entity.Message;
import com.lss.service.MessageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 留言控制层
 *
 * @author lss
 * @create 2022年03月22日 14:53
 */
@RestController
@RequestMapping("message")
public class MessageController {

    @Resource
    MessageService messageService;

    /**
     * 添加保存留言
     *
     * @param message 接收留言信息实体类
     */
    @PostMapping("saveMessage")
    public void saveMessage(@RequestBody Message message) {
        messageService.saveMessage(message);
    }

    /**
     * 前台获取留言列表
     *
     * @return 留言数据
     */
    @GetMapping("blogListMessage")
    public Result<?> blogListMessage() {
        return messageService.blogListMessage();
    }

    @GetMapping("listMessage")
    public Result<?> listMessage(@RequestParam("currentPage") Integer currentPage,
                                 @RequestParam("pageSize") Integer pageSize,
                                 @RequestParam(value = "isReview", defaultValue = "-1") Integer isReview,
                                 String keywords) {
        return messageService.listMessage(currentPage, pageSize, isReview, keywords);
    }

    /**
     * 审核通过留言
     *
     * @param json json接收参数
     */
    @PutMapping("restoreMessage")
    @OptLog(ApiModule = "留言模块", ApiDesc = "审核通过留言", optType = OptTypeConst.UPDATE)
    public Result<?> restoreMessage(@RequestBody JSONObject json) {
        int id = Integer.parseInt(json.get("id").toString());
        return messageService.restoreMessage(id);
    }

    /**
     * 删除留言
     *
     * @param deleteIdList 删除留言id集合
     */
    @Transactional
    @DeleteMapping("deleteMessage")
    @OptLog(ApiModule = "留言模块", ApiDesc = "删除留言", optType = OptTypeConst.REMOVE)
    public Result<?> deleteMessage(@RequestBody List<Integer> deleteIdList) {
        return messageService.deleteMessage(deleteIdList);
    }

    /**
     * 批量审核留言
     */
    @PutMapping("checkMessage")
    @OptLog(ApiModule = "留言模块", ApiDesc = "批量审核留言", optType = OptTypeConst.UPDATE)
    public Result<?> checkMessages(@RequestBody JSONObject json) {
        Object checkMessage = json.get("checkMessage");
        List<Integer> list = JSONObject.parseObject(checkMessage.toString(), List.class);
        return messageService.checkMessages(list);
    }
}
