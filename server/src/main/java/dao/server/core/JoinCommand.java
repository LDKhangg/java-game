package dao.server.core;

import io.netty.channel.Channel;

public record JoinCommand(int id, String name, Channel ch) implements Command{
  @Override
  public void executor(World world) {
    world.addPlayer(new Player(id,name,ch));
  }
}
