package dao.server.system;

import dao.protocol.EntityState;
import dao.protocol.Envelope;
import dao.protocol.Snapshot;
import dao.server.core.GameSystem;
import dao.server.core.Player;
import dao.server.core.World;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

import java.util.ArrayList;
import java.util.List;

public class SnapshotSystem implements GameSystem {
  @Override
  public void update(World world) {
    List<EntityState> entities = new ArrayList<>();
    for(Player p:world.players()){
      entities.add(EntityState.newBuilder()
        .setId(p.Id()).setX(p.getX()).setY(p.getY()).setRealmId(0).build());
    }
    for(Player p:world.players()){
      Channel ch = p.getChannel();
      if(ch == null || !ch.isWritable()) continue;
      Snapshot s = Snapshot.newBuilder()
        .setTick(world.tick())
        .setAckSeq(p.getLastAckSeq())
        .addAllEntities(entities)
        .build();
      ch.writeAndFlush(Envelope.newBuilder().setSnapshot(s).build());
    }

  }
}
