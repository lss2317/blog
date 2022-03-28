package com.lss.strategy;

import com.alibaba.fastjson.JSONObject;
import com.lss.common.Result;

/**
 * 第三方微博登录接口
 *
 * @author lss
 * @create 2022年03月25日 18:52
 */
public interface WeiBoLoginStrategy {

    /**
     * 微博登录接口
     *
     * @param object 接收参数
     * @return 用户信息
     */
    Result<?> weiboLogin(JSONObject object);

}
