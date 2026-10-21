package dao.server.core;

public record LeaveCommand(int playerId) implements Command {
  @Override
  public void executor(World world) {
   world.removePlayer(playerId);
  }
}
