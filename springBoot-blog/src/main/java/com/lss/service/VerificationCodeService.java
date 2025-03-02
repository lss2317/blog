package com.lss.service;

/**
 * 验证码
 *
 * @author lss
 * @date 2025/03/01
 */
public interface VerificationCodeService {

    /**
     * 验证滑块验证码
     *
     * @param captchaVerifyParam 验证数据
     * @return 验证结果
     */
    String CheckVerificationCode(String captchaVerifyParam);
}
