package com.lss.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.sdk.service.captcha20230305.AsyncClient;
import com.aliyun.sdk.service.captcha20230305.models.VerifyIntelligentCaptchaRequest;
import com.aliyun.sdk.service.captcha20230305.models.VerifyIntelligentCaptchaResponse;
import com.lss.service.VerificationCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * 阿里云滑块验证码
 *
 * @author lss
 * @date 2025/03/01
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Resource
    private AsyncClient client;

    @Override
    public String CheckVerificationCode(String captchaVerifyParam) {
        //API请求的参数设置
        JSONObject parse = JSON.parseObject(captchaVerifyParam, JSONObject.class);
        String verifyParam = parse.getString("captchaVerifyParam");
        VerifyIntelligentCaptchaRequest verifyIntelligentCaptchaRequest = VerifyIntelligentCaptchaRequest.builder()
                .captchaVerifyParam(verifyParam)
                .build();
        //异步获取API请求的返回值
        CompletableFuture<VerifyIntelligentCaptchaResponse> response = client.verifyIntelligentCaptcha(verifyIntelligentCaptchaRequest);
        // 同步获取API请求的返回值
        VerifyIntelligentCaptchaResponse resp;
        try {
            resp = response.get();
        } catch (Exception e) {
            return "error";
        }
        return resp.getBody().getResult().getVerifyResult() + "";
    }
}
