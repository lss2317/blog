package com.lss.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lss.constant.MQPrefixConst;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 通知邮箱
 *
 * @author lss
 * @create 2022年04月01日 18:01
 */
@Component
@RabbitListener(queues = MQPrefixConst.EMAIL_QUEUE)
public class EmailConsumer {

    /**
     * 邮箱号
     */
    @Value("${spring.mail.username}")
    private String email;

    @Resource
    private JavaMailSender javaMailSender;

    @RabbitHandler
    public void process(byte[] data) {
        JSONObject json = JSON.parseObject(new String(data), JSONObject.class);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(json.getString("email"));
        message.setSubject(json.getString("subject"));
        message.setText(json.getString("content"));
        javaMailSender.send(message);
    }
}
