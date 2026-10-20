package dao.server.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class World {
  private final Map<Integer,Player> players = new HashMap<>();
  private long tick = 0;

  public long tick(){
    return this.tick;
  }
  public void advanceTick(){
    tick++;
  }
  public void addPlayer(Player player){
    players.put(player.id(),player);
  }

  public void removePlayer(int playerId){
    players.remove(playerId);
  }
  public int playerCount(){
    return players.size();
  }

  public Collection<Player> players(){
    return players.values();
  }
}
