package dao.server.net;

import dao.protocol.Envelope;
import dao.protocol.Input;
import dao.protocol.LoginOk;
import dao.protocol.Pong;
import dao.server.core.GameLoop;
import dao.server.core.InputCommand;
import dao.server.core.JoinCommand;
import dao.server.core.LeaveCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;


public class PacketHandler extends SimpleChannelInboundHandler<Envelope> {
  private static final AtomicInteger nextId = new AtomicInteger(0);
  private final Logger log = LoggerFactory.getLogger(PacketHandler.class);
  private final GameLoop loop;
  private int playerId=-1;

  public PacketHandler(GameLoop gameLoop){
    loop=gameLoop;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Envelope msg) throws Exception {
    log.info("nhận {} từ {}", msg.getPayloadCase(), ctx.channel().remoteAddress());
    switch (msg.getPayloadCase()){
      case LOGIN ->{
        playerId = nextId.incrementAndGet();
        loop.submit(new JoinCommand(playerId,msg.getLogin().getName(),ctx.channel()));
        LoginOk ok = LoginOk.newBuilder()
          .setPlayerId(playerId)
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
      case INPUT -> {
        if (playerId == -1) return;
        Input in = msg.getInput();
        loop.submit(new InputCommand(playerId, in.getSeq(), in.getMoveX(), in.getMoveY()));
      }
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    if(playerId!=-1){
      loop.submit(new LeaveCommand(playerId));
    }
    super.channelInactive(ctx);
  }
}
