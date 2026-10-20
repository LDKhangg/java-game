package dao.bot;

import dao.protocol.Envelope;
import dao.protocol.FrameCodec;
import dao.protocol.Login;
import dao.protocol.Ping;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.time.Instant;

public class SmokeBot {
  public static void main(String[] args) throws InterruptedException {

    EventLoopGroup group = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());

    try{
      Bootstrap bootstrap = new Bootstrap().group(group)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.TCP_NODELAY,true)
        .handler(
          new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              FrameCodec.configure(ch.pipeline());
              ch.pipeline().addLast(new BotHandler());
            }
          }
        );
      Channel channel = bootstrap.connect("localhost", 7777).sync().channel();

      Envelope login =
        Envelope.newBuilder().setLogin(Login.newBuilder().setName("bot-1")).build();
      channel.writeAndFlush(login).sync();

      Envelope ping=
        Envelope.newBuilder().setPing(Ping.newBuilder().setClientTimeMs(System.currentTimeMillis())).build();
      channel.writeAndFlush(ping).sync();


      Thread.sleep(1000);
      Envelope ping2=
        Envelope.newBuilder().setPing(Ping.newBuilder().setClientTimeMs(System.currentTimeMillis())).build();
      channel.writeAndFlush(ping2).sync();

      Thread.sleep(1000);
      channel.close().sync();

    } finally {
      group.shutdownGracefully();
    }
  }

  private static class  BotHandler extends SimpleChannelInboundHandler<Envelope>{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Envelope msg) {
      System.out.println("Bot nhận: " + msg.getPayloadCase());
      switch (msg.getPayloadCase()) {
        case LOGIN_OK -> System.out.println("Bot nhận LOGIN_OK, id = " + msg.getLoginOk().getPlayerId());
        case PONG -> System.out.println("Bot nhận PONG, RTT ~ "
          + (System.currentTimeMillis() - msg.getPong().getClientTimeMs()) + " ms");
        default -> System.out.println("Bot nhận gói khác: " + msg.getPayloadCase());
      }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      cause.printStackTrace();
      ctx.close();
    }

  }
}
