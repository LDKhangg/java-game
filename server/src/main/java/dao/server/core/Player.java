package dao.server.core;

import dao.GameConstants;
import io.netty.channel.Channel;

public class Player {
  private final int playerId;
  private final String name;
  private final Channel channel;

  private float x = GameConstants.MIN_X;
  private float y = GameConstants.MIN_Y;
  private float moveX,moveY;
  private int lastAckSeq;

  public Player(int playerId, String name, Channel channel) {
    this.playerId = playerId;
    this.name = name;
    this.channel = channel;
  }

  public Player(int playerId, String name) {
    this.playerId = playerId;
    this.name = name;
    this.channel = null;
  }

  public int Id() {
    return playerId;
  }

  public String getName() {
    return name;
  }

  public Channel getChannel() {
    return channel;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public float getMoveX() {
    return moveX;
  }

  public float getMoveY() {
    return moveY;
  }

  public int getLastAckSeq() {
    return lastAckSeq;
  }
  public void setPosition(float x, float y){
    this.x = x;
    this.y = y;
  }
  public void setInput(int seq, float moveX, float moveY){
    this.lastAckSeq=seq;
    this.moveX=moveX;
    this.moveY=moveY;
  }
}
