package dao.server;

import dao.server.core.GameLoop;
import dao.server.net.NettyServer;
import dao.server.system.HeartbeatSystem;
import dao.server.system.MovementSystem;
import dao.server.system.SnapshotSystem;

public class Main {
  void main(String[] args) throws InterruptedException {
    GameLoop loop = new GameLoop();
    loop.addSystem(new SnapshotSystem());
    loop.addSystem(new MovementSystem());
    loop.addSystem(new HeartbeatSystem());
    Thread gameThread = new Thread(loop::run,"game-loop");
    gameThread.start();
    try{
      new NettyServer(7777,loop).start();
    } finally{
      loop.stop();
    }
  }
}
