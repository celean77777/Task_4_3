package ru.gb.storage.commons.handler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gb.storage.commons.message.Message;

import java.util.List;
public class JsonDecoder extends MessageToMessageDecoder<String> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        System.out.println("Input: " + msg);
        Message message = OBJECT_MAPPER.readValue(msg, Message.class);
        out.add(message);
    }
}