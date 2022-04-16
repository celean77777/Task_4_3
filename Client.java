package ru.gb.storage.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import ru.gb.storage.commons.handler.JsonDecoder;
import ru.gb.storage.commons.handler.JsonEncoder;
import ru.gb.storage.commons.message.AuthMessage;
import ru.gb.storage.commons.message.DateMessage;
import ru.gb.storage.commons.message.Message;
import ru.gb.storage.commons.message.TextMessage;

import java.time.LocalDateTime;
import java.util.Date;

public class Client {

    public static void main(String[] args) {
        new Client().start();
    }

    public void start() {
        final NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    new LengthFieldPrepender(3),
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new SimpleChannelInboundHandler<Message>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
                                            System.out.println("receive msg " + msg);
                                        }
                                    }
                            );
                        }
                    });

            System.out.println("Client started");

            Channel channel = bootstrap.connect("localhost", 9000).sync().channel();


                TextMessage textMessage = new TextMessage();
                textMessage.setText(String.format("[%s] %s", LocalDateTime.now(), Thread.currentThread().getName()));
                System.out.println("Try to send message: " + textMessage);
                channel.writeAndFlush(textMessage);

                AuthMessage authMessage = new AuthMessage();
                authMessage.setAuth("Sergey", "d123");
                System.out.println("Try to send message: " + authMessage);
                channel.writeAndFlush(authMessage);


                DateMessage dateMessage = new DateMessage();
                dateMessage.setDate(new Date());
                channel.write(dateMessage);
                System.out.println("Try to send message: " + dateMessage);
                channel.flush();



            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}