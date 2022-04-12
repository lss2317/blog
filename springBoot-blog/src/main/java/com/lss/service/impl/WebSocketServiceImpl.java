package com.lss.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lss.controller.CosController;
import com.lss.entity.ChatRecord;
import com.lss.mapper.ChatRecordMapper;
import com.lss.utils.HTMLUtils;
import com.lss.utils.IpUtils;
import lombok.Data;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * websocket服务
 *
 * @author lss
 * @create 2022年04月12日 11:30
 */
@Data
@Service
@ServerEndpoint(value = "/live", configurator = WebSocketServiceImpl.ChatConfigurator.class)
public class WebSocketServiceImpl {

    /**
     * 用户session
     */
    private Session session;

    /**
     * 用户session集合
     */
    private static CopyOnWriteArraySet<WebSocketServiceImpl> webSocketSet = new CopyOnWriteArraySet<>();


    @Resource
    public void setChatRecordMapper(ChatRecordMapper chatRecordMapper) {
        WebSocketServiceImpl.chatRecordMapper = chatRecordMapper;
    }

    @Resource
    public void setCosUpload(CosController cosController) {
        WebSocketServiceImpl.cosUpload = cosController;
    }

    private static ChatRecordMapper chatRecordMapper;

    private static CosController cosUpload;

    /**
     * 获取客户端真实ip
     */
    public static class ChatConfigurator extends ServerEndpointConfig.Configurator {

        public static String HEADER_NAME = "X-Real-IP";

        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            try {
                String firstFoundHeader = request.getHeaders().get(HEADER_NAME.toLowerCase()).get(0);
                sec.getUserProperties().put(HEADER_NAME, firstFoundHeader);
            } catch (Exception e) {
                sec.getUserProperties().put(HEADER_NAME, "未知ip");
            }
        }
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // 加入连接
        this.session = session;
        webSocketSet.add(this);
        // 更新在线人数
        updateOnlineCount();
        // 加载历史聊天记录
        ChatRecord chatRecord = listChartRecords(endpointConfig);
        chatRecord.setType(2);
        try {
            //返回连接成功信息
            synchronized (session) {
                session.getBasicRemote().sendText(JSONObject.toJSONString(chatRecord));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        JSONObject json = JSONObject.parseObject(message);
        switch (Integer.parseInt(json.getString("type"))) {
            case 3:
                // 发送消息
                ChatRecord chatRecord = JSONObject.parseObject( json.getString("data"),ChatRecord.class);
                // 过滤html标签
                chatRecord.setContent(HTMLUtils.deleteTag(chatRecord.getContent()));
                chatRecord.setCreateTime(new Date());
                chatRecordMapper.insert(chatRecord);
                json.put("data", chatRecord);
                // 广播消息
                broadcastMessage(json);
                break;
            case 4:
                // 撤回消息
                ChatRecord recall = JSON.parseObject(JSON.toJSONString(json.get("data")), ChatRecord.class);
                // 删除记录
                chatRecordMapper.deleteById(recall.getId());
                // 广播消息
                broadcastMessage(json);
                break;
            case 6:
                // 心跳消息
                json.put("data", "pong");
                session.getBasicRemote().sendText(json.toJSONString());
            default:
                break;
        }
    }

    /**
     * 发送语音
     *
     * @param chatRecord 语音路径
     */
    public void sendVoice(ChatRecord chatRecord) {
        // 上传语音文件
        String content = cosUpload.uploadVoice(chatRecord.getFile());
        chatRecord.setContent(content);
        // 保存记录
        chatRecord.setCreateTime(new Date());
        chatRecordMapper.insert(chatRecord);
        // 发送消息
        JSONObject json = new JSONObject();
        json.put("type", 5);
        json.put("data", chatRecord);
        // 广播消息
        broadcastMessage(json);
    }

    /**
     * 加载历史聊天记录
     *
     * @param endpointConfig 配置
     * @return 加载历史聊天记录
     */
    private ChatRecord listChartRecords(EndpointConfig endpointConfig) {
        // 获取聊天历史记录
        List<ChatRecord> recordList = chatRecordMapper.selectList(new LambdaQueryWrapper<ChatRecord>()
                .ge(ChatRecord::getCreateTime, DateUtil.offsetDay(new Date(), -1)));
        // 获取当前用户ip
        String ipAddress = endpointConfig.getUserProperties().get(ChatConfigurator.HEADER_NAME).toString();
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setIpAddress(ipAddress);
        chatRecord.setIpSource(IpUtils.getIpSource(ipAddress));
        chatRecord.setChatRecordList(recordList);
        return chatRecord;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        // 更新在线人数
        webSocketSet.remove(this);
        updateOnlineCount();
    }

    /**
     * 更新在线人数
     */
    @Async
    public void updateOnlineCount() {
        JSONObject json = new JSONObject();
        // 获取当前在线人数
        json.put("data", webSocketSet.size());
        json.put("type", 1);
        broadcastMessage(json);
    }

    /**
     * 广播消息
     *
     * @param json 数据
     */
    private void broadcastMessage(JSONObject json) {
        for (WebSocketServiceImpl webSocketService : webSocketSet) {
            synchronized (webSocketService.session) {
                try {
                    webSocketService.session.getBasicRemote().sendText(json.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
