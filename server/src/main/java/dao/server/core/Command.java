package dao.server.core;
@FunctionalInterface
public interface Command {
  void executor(World world);
}
