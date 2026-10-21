package dao.server.net;

import dao.protocol.FrameCodec;
import dao.server.core.GameLoop;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;


public class NettyServer {
  private final static Logger log = LoggerFactory.getLogger(NettyServer.class);

  private int port;
  private GameLoop loop;

  public NettyServer(int port, GameLoop loop) {
    this.port = port;
    this.loop = loop;
  }

  public void start() throws InterruptedException{
    EventLoopGroup boss = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
    EventLoopGroup workers = new MultiThreadIoEventLoopGroup(2,NioIoHandler.newFactory());
    try{
      ServerBootstrap bootstrap = new ServerBootstrap().group(boss,workers)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG,1024)
        .childOption(ChannelOption.TCP_NODELAY,true)
        .childOption(
          ChannelOption.WRITE_BUFFER_WATER_MARK,
          new WriteBufferWaterMark(32 * 1024, 64 * 1024))
        .childHandler(
          new ChannelInitializer<SocketChannel>(){
           @Override
           protected void initChannel(SocketChannel ch)  {
          FrameCodec.configure(ch.pipeline());

          ch.pipeline().addLast("packetHandler",new PacketHandler(loop));
        }
      }
        );
      ChannelFuture bound = bootstrap.bind(port).sync();

      log.info("Server đang nghe cổng {}", port);

      bound.channel().closeFuture().sync();

    } finally {
      boss.shutdownGracefully();
      workers.shutdownGracefully();
    }
  }
}
