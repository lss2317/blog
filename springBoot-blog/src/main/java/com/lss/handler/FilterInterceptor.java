package com.lss.handler;

import com.alibaba.fastjson.JSONObject;
import com.lss.constant.RedisPrefixConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author lss
 * @create 2022年03月08日 13:32
 */
public class FilterInterceptor implements HandlerInterceptor {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String style = request.getHeader("style");
        if (style != null && style.equals("admin")) {
            //获取token
            String token = request.getHeader("token");
            String uuid = request.getHeader("uuid_token");
            Object redisToken = redisTemplate.opsForValue().get(RedisPrefixConst.PREFIX_TOKEN + uuid);
            if (redisToken == null) {
                JSONObject json = new JSONObject();
                json.put("code", 1000);
                response.getWriter().write(JSONObject.toJSONString(json));
                return false;
            }
            String newToken = redisToken.toString().replace("\\\"", "");
            if (newToken.equals(token)) {
                //重新设置token失效时间
                redisTemplate.opsForValue().set(RedisPrefixConst.PREFIX_TOKEN + uuid, token, 60 * 60, TimeUnit.SECONDS);
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
