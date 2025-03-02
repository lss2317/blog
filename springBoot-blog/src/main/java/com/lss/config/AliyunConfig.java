package com.lss.config;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.captcha20230305.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云配置
 *
 * @author lss
 * @date 2025/03/01
 */
@Configuration
public class AliyunConfig {

    @Value("${aliyun.accessKeyId}")
    String accessKeyId;

    @Value("${aliyun.accessKeySecret}")
    String accessKeySecret;

    @Bean
    public AsyncClient asyncClient() {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());
        // 配置客户端
        return AsyncClient.builder()
                .region("cn-shanghai") // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration(ClientOverrideConfiguration.create()
                        .setEndpointOverride("captcha.cn-shanghai.aliyuncs.com"))
                .build();
    }
}
