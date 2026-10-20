package dao.server.net;

import dao.protocol.Envelope;
import dao.protocol.LoginOk;
import dao.protocol.Pong;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import static dao.protocol.Envelope.PayloadCase.LOGIN;


public class PacketHandler extends SimpleChannelInboundHandler<Envelope> {
  private static final AtomicInteger nextId = new AtomicInteger(0);
  private final Logger log = LoggerFactory.getLogger(PacketHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Envelope msg) throws Exception {
    log.info("nhận {} từ {}", msg.getPayloadCase(), ctx.channel().remoteAddress());
    switch (msg.getPayloadCase()){
      case LOGIN ->{
        LoginOk ok = LoginOk.newBuilder()
          .setPlayerId(nextId.incrementAndGet())
          .setServerTick(0)
          .setTickRate(20)
          .build();
        ctx.writeAndFlush(Envelope.newBuilder().setLoginOk(ok).build());
      }
      case PING ->{
        Pong ok = Pong.newBuilder()
          .setClientTimeMs(msg.getPing().getClientTimeMs())
          .setServerTimeMs(System.currentTimeMillis())
          .build();
        ctx.writeAndFlush(Envelope.newBuilder().setPong(ok).build());
      }
    }
  }
}
