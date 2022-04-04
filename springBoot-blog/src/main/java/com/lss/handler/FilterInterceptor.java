package com.lss.handler;

import com.alibaba.fastjson.JSONObject;
import com.lss.constant.RedisPrefixConst;
import com.lss.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * Filter拦截器
 *
 * @author lss
 * @create 2022年03月08日 13:32
 */
public class FilterInterceptor implements HandlerInterceptor {

    @Resource
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String style = request.getHeader("style");
        if (style != null && style.equals("admin")) {
            //获取token
            String token = request.getHeader("token");
            String uuid = request.getHeader("uuid_token");
            Object redisToken = redisService.get(RedisPrefixConst.PREFIX_TOKEN + uuid);
            if (redisToken == null) {
                JSONObject json = new JSONObject();
                json.put("code", 1000);
                response.getWriter().write(JSONObject.toJSONString(json));
                return false;
            }
            String newToken = redisToken.toString().replace("\\\"", "");
            if (newToken.equals(token)) {
                //重新设置token失效时间
                redisService.set(RedisPrefixConst.PREFIX_TOKEN + uuid, token, 60 * 60);
                return true;
            }
            JSONObject json = new JSONObject();
            json.put("code", 1000);
            response.getWriter().write(JSONObject.toJSONString(json));
            return false;
        } else {
            return true;
        }
    }
}
