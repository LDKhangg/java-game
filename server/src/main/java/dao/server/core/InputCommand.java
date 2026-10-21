package dao.server.core;

public record InputCommand(int playerId,int seq,float moveX,float moveY) implements Command {
  @Override
  public void executor(World world) {
    Player currPlayer = world.player(playerId);
    if(currPlayer!=null) currPlayer.setInput(seq,moveX,moveY);
  }
}
