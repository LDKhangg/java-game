package dao.client.net;

import dao.client.core.*;
import dao.protocol.EntityState;
import dao.protocol.Snapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SnapshotApplier {
  private static final float MOVE_EPSILON = 0.01f;

  private SnapshotApplier(){}

  public static void apply(Snapshot snapshot, World world){
    Set<Integer> idsInSnapshot = new HashSet<>();

    for(EntityState entity : snapshot.getEntitiesList()){
      idsInSnapshot.add(entity.getId());
      PlayerId id = new PlayerId(entity.getId());
      Player player = world.getPlayer(id);

      if(player ==null){
        player = new Player(id,"P"+entity.getId(),entity.getRealmId());
        player.setPositionXPixel(entity.getX());
        player.setPositionYPixel(entity.getY());
        world.addPlayer(player);
        player.setInterpolationTarget(entity.getX(),entity.getY());
      } else if(world.isLocalPlayer(id)){
        updateMotion(player,entity.getX(),entity.getY());
      } else{
        updateRemoteMotion(player,entity.getX(),entity.getY());
      }
    }

    for(Player player :new ArrayList<>(world.getPlayers().values())){
      if(!idsInSnapshot.contains(player.getId().value())){
        world.removePlayer(player.getId());
      }
    }
  }
  private static void updateMotion(Player player, float newX, float newY){
    float dx = newX-player.getPositionXPixel();
    float dy = newY-player.getPositionYPixel();

    if( Math.abs(dx)<MOVE_EPSILON && Math.abs(dy)<MOVE_EPSILON){
      player.stop();
    } else {
      player.setState(PlayerState.MOVING);
      if (Math.abs(dx) >= Math.abs(dy)) {
        player.setFacingDirection(dx > 0 ? FacingDirection.RIGHT : FacingDirection.LEFT);
      } else {
        player.setFacingDirection(dy > 0 ? FacingDirection.BACK : FacingDirection.FRONT);
      }
    }
    player.setPositionXPixel(newX);
    player.setPositionYPixel(newY);
  }
  private static void updateRemoteMotion(Player player,float newX,float newY){
    float dx = newX-player.getInterpolationTargetX();
    float dy = newY-player.getInterpolationTargetY();

    if( Math.abs(dx)<MOVE_EPSILON && Math.abs(dy)<MOVE_EPSILON){
      player.stop();
    } else {
      player.setState(PlayerState.MOVING);
      if (Math.abs(dx) >= Math.abs(dy)) {
        player.setFacingDirection(dx > 0 ? FacingDirection.RIGHT : FacingDirection.LEFT);
      } else {
        player.setFacingDirection(dy > 0 ? FacingDirection.BACK : FacingDirection.FRONT);
      }
    }
    player.setInterpolationTarget(newX,newY);
  }
}
