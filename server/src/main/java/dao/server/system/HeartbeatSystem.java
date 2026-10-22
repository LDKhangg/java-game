package dao.server.system;

import dao.GameConstants;
import dao.server.core.GameLoop;
import dao.server.core.GameSystem;
import dao.server.core.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatSystem implements GameSystem {
  Logger log = LoggerFactory.getLogger(HeartbeatSystem.class);
  @Override
  public void update(World world) {
    if(world.tick()% GameConstants.TICK_RATE==0){{
      log.info("tick={} players={}", world.tick(), world.playerCount());
    }}

  }
}
