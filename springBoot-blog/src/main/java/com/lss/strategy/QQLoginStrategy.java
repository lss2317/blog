package com.lss.strategy;

import com.alibaba.fastjson.JSONObject;
import com.lss.common.Result;

/**
 * 第三方QQ登录
 *
 * @author lss
 * @create 2022年03月07日 14:47
 */

public interface QQLoginStrategy {

    /**
     * 使用QQ登录
     *
     * @param json qq登录接口json参数
     */
    Result<?> qqLogin(JSONObject json);
}
