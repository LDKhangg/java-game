package dao.client.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class World {
  private final Map<PlayerId,Player> players = new LinkedHashMap<>();
  private PlayerId playerId;

  public void addPlayer(Player player){
    players.put(player.getId(),player);
  }
  public void removePlayer(PlayerId value){
    players.remove(value);
  }
  public Player getPlayer(PlayerId playerId){
    return players.get(playerId);
  }

  public Player getLocalPlayer(){
    return players.isEmpty() ? null : players.get(playerId);
  }
  public void setLocalPlayerId(PlayerId value){
    playerId=value;
  }

  public Map<PlayerId, Player> getPlayers() {
    return Collections.unmodifiableMap(players);
  }

  public int playerCount(){
    return players.size();
  }
}
