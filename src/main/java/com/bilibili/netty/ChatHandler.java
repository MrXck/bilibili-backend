package com.bilibili.netty;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.bilibili.mapper.MessageMapper;
import com.bilibili.pojo.Message;
import com.bilibili.service.HistoryService;
import com.bilibili.service.UserService;
import com.bilibili.utils.SpringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.bilibili.utils.Constant.*;

public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //用于记录和管理所有客户端的channel
    private static final ChannelGroup CLIENTS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static final Map<Long, Channel> USER_CHANNEL = new ConcurrentHashMap<>();
    private static final Map<Channel, Long> CHANNEL_USER = new ConcurrentHashMap<>();


    //客户端创建的时候触发，当客户端连接上服务端之后，就可以获取该channel，然后放到channelGroup中进行统一管理
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        CLIENTS.add(ctx.channel());
        super.handlerAdded(ctx);
    }

    //客户端销毁的时候触发，
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Long userId = CHANNEL_USER.get(channel);
        USER_CHANNEL.remove(userId);
        CHANNEL_USER.remove(channel);
        CLIENTS.remove(channel);
        super.handlerRemoved(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //客户端传递过来的消息
        String content = msg.text();
        Map map = JSON.parseObject(content, HashMap.class);
        System.out.println(map);
        Message message = JSON.parseObject(content, Message.class);
        Integer type = message.getType();
        Long id = message.getId();
        message.setTime(LocalDateTime.now());
        Channel channel = ctx.channel();
        if (type != null) {
            if (LINK_TYPE.equals(type) && id != null) {
                USER_CHANNEL.put(id, channel);
                CHANNEL_USER.put(channel, id);
            } else {
                MessageMapper messageMapper = SpringUtils.getBean(MessageMapper.class);
                HistoryService historyService = SpringUtils.getBean(HistoryService.class);
                UserService userService = SpringUtils.getBean(UserService.class);
                String token = (String) map.get("token");
                if (!StringUtils.isEmpty(token)) {
                    String[] split = userService.checkToken(token).split(":");
                    Long userId = Long.valueOf(split[1]);
                    message.setFromId(userId);
                    messageMapper.insert(message);

                    historyService.addHistory(message.getToId(), userId);
                    historyService.addHistory(userId, message.getToId());
                    Channel channel1 = USER_CHANNEL.get(message.getToId());

                    Map<String, Object> response = new HashMap<>(16);
                    response.put("content", message.getContent());
                    response.put("fromId", message.getFromId().toString());
                    response.put("toId", message.getToId().toString());
                    response.put("time", message.getTime());
                    response.put("id", message.getId().toString());
                    if (message.getType().equals(SEND_TEXT_TYPE)) {
                        response.put("type", SEND_TEXT_TYPE);
                    } else {
                        response.put("type", SEND_IMAGE_TYPE);
                    }
                    if (channel1 != null) {
                        channel1.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(response)));
                    }
                }
            }
        }
    }
}

