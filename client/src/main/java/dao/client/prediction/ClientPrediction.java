package dao.client.prediction;

import dao.GameConstants;
import dao.client.core.FacingDirection;
import dao.client.core.Intent;
import dao.client.core.Player;
import dao.client.core.PlayerState;
import dao.client.net.NettyClient;
import dao.protocol.EntityState;
import dao.protocol.Input;
import dao.protocol.Snapshot;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class ClientPrediction {
  private static final float FIXED_TICK_DURATION_SECONDS=1f/ GameConstants.TICK_RATE;
  private final Deque<PendingInput> pendingInputs = new ArrayDeque<>();
  private float accumulator;
  private int nextSequence;
  private int lastAcknowledgedSequence;

  public void update(
    Player player,
    Intent intent,
    float deltaTime,
    NettyClient nettyClient
  ){
    accumulator+=deltaTime;
    while(accumulator>=FIXED_TICK_DURATION_SECONDS){
      float moveX = intent.moveX();
      float moveY = intent.moveY();

      PendingInput input = new PendingInput(++nextSequence,moveX,moveY);
      pendingInputs.addLast(input);

      nettyClient.sendInput(input.seq(),input.moveX(),input.moveY());

      applyInput(player,input);

      accumulator-=FIXED_TICK_DURATION_SECONDS;
    }

  }
  public void reconcile(
    Player player,
    Snapshot snapshot
  ){
    Optional<EntityState> entityStateForLocalPlayer =snapshot.getEntitiesList().stream().filter(
      es->es.getId()==player.getId().value()
    ).findFirst();
    if (entityStateForLocalPlayer.isEmpty()){
      return;
    }

    float serverX=entityStateForLocalPlayer.get().getX();
    float serverY=entityStateForLocalPlayer.get().getY();

    int ackSeq=snapshot.getAckSeq();
    if(ackSeq<lastAcknowledgedSequence){
      return;
    }

    player.setPositionYPixel(serverY);
    player.setPositionXPixel(serverX);


    lastAcknowledgedSequence=ackSeq;
    while(!pendingInputs.isEmpty() && pendingInputs.peekFirst().seq()<=ackSeq){
      pendingInputs.removeFirst();
    }
    for(PendingInput input:pendingInputs){
      applyInput(player,input);
    }
  }

  private void applyInput(Player player, PendingInput input){
    float moveX = input.moveX();
    float moveY = input.moveY();

    if(!Float.isFinite(moveX) || !Float.isFinite(moveY)){
      return;
    }
    double len = Math.hypot(moveX,moveY);
    if (len == 0){
      player.stop();
      return;
    }
    if(len >1){
      moveX/=(float) len;
      moveY/=(float) len;
    }
    float step = GameConstants.PLAYER_SPEED_PX_PER_SEC*FIXED_TICK_DURATION_SECONDS;

    float nextX=player.getPositionXPixel()+moveX*step;
    float nextY=player.getPositionYPixel()+moveY*step;

    nextX = Math.clamp(nextX,GameConstants.MIN_X,GameConstants.MAX_X);
    nextY = Math.clamp(nextY,GameConstants.MIN_Y,GameConstants.MAX_Y);

    player.setState(PlayerState.MOVING);

    if(Math.abs(moveX)>=Math.abs(moveY)){
      player.setFacingDirection(moveX>0? FacingDirection.RIGHT:FacingDirection.LEFT);
    } else{
      player.setFacingDirection(moveY>0? FacingDirection.BACK:FacingDirection.FRONT);
    }

    player.setPositionXPixel(nextX);
    player.setPositionYPixel(nextY);
  }
}
