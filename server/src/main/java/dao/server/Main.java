package dao.server;

import dao.server.net.NettyServer;

public class Main {
  void main(String[] args) throws InterruptedException {
    new NettyServer(7777).start();
  }
}
