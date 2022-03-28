package com.lss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * COS配置属性
 *
 * @author lss
 * @create 2022年03月10日 21:11
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cos")
public class COSConfigProperties {

    /**
     * 桶的名称
     */
    private String bucketName;

    /**
     * 区域
     */
    private String region;

    /**
     * APPID
     */
    private String appId;

    /**
     * 初始化用户身份信息(secretId, secretKey)
     * SecretId 是用于标识 API 调用者的身份
     */
    private String SecretId;

    /**
     * SecretKey是用于加密签名字符串和服务器端验证签名字符串的密钥
     */
    private String SecretKey;

    /**
     * 访问路径前缀
     */
    private String url;
}
