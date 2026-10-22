package dao.client.net;


import com.badlogic.gdx.Gdx;
import dao.protocol.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NettyClient {
  private final Logger log = LoggerFactory.getLogger(NettyClient.class);
  private final ConcurrentLinkedQueue<Snapshot> snapshots = new  ConcurrentLinkedQueue<>();
  private final EventLoopGroup group = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
  private volatile int myId =-1;
  private volatile Channel channel;

  private static Envelope login(String name){
    return Envelope.newBuilder()
      .setLogin(
        Login.newBuilder().setName(name)
      )
      .build();
  }

  public void connect(String host,int port, String name){
    Bootstrap bootstrap = new Bootstrap()
      .group(group)
      .channel(NioSocketChannel.class)
      .option(ChannelOption.TCP_NODELAY,true)
      .handler(
        new ChannelInitializer<SocketChannel> (){
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            FrameCodec.configure(ch.pipeline());
            ch.pipeline().addLast(new Handler());
          }
        }
      );
    bootstrap.connect(host,port).addListener(
      (ChannelFutureListener) future -> {
        if(!future.isSuccess()){
          Gdx.app.error("net", "không kết nối được server", future.cause());
          return;
        }
        channel=future.channel();
        channel.writeAndFlush(login(name));
      }
    );
  }

  public Snapshot pollSnapshot(){
    return snapshots.poll();
  }

  public int myId(){
    return myId;
  }

  public boolean isConnected(){
    Channel ch = channel;
    return ch!=null &&ch.isActive() ;
  }

  public void sendInput(int seq,float moveX,float moveY){
    Channel ch = channel;
    if(ch==null){
      return;
    }
    ch.writeAndFlush(Envelope.newBuilder().setInput(
      Input.newBuilder()
        .setSeq(seq)
        .setMoveX(moveX)
        .setMoveY(moveY)
        .build()
    ));
  }

  public void close(){
    Channel ch = channel;
    if(ch!=null){
      ch.close();
    }
    group.shutdownGracefully();
  }

  private final class Handler extends SimpleChannelInboundHandler<Envelope> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Envelope msg) {
      switch (msg.getPayloadCase()) {
        case LOGIN_OK -> myId = msg.getLoginOk().getPlayerId();
        case SNAPSHOT -> snapshots.offer(msg.getSnapshot());
        default -> {
        }
      }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      channel = null;
      Gdx.app.log("net", "mất kết nối với server");
      super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      Gdx.app.error("net", "lỗi mạng", cause);
      ctx.close();
    }
  }
}
