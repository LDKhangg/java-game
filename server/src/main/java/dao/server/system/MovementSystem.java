package dao.server.system;

import dao.server.core.GameLoop;
import dao.server.core.GameSystem;
import dao.server.core.Player;
import dao.server.core.World;

import static dao.GameConstants.*;

public class MovementSystem implements GameSystem {
  @Override
  public void update(World world) {
    for(Player player: world.players()){
      float mx=player.getMoveX(),my=player.getMoveY();
      if (!Float.isFinite(mx) || !Float.isFinite(my)) continue;
      double len = Math.hypot(mx,my);
      if (len == 0) continue;
      if (len > 1) { mx /= len; my /= len; }

      float step = PLAYER_SPEED_PX_PER_SEC / GameLoop.TICK_RATE;
      player.setPosition(
        Math.clamp(player.getX() + mx * step, MIN_X, MAX_X),
        Math.clamp(player.getY() + my * step, MIN_Y, MAX_Y));
    }
  }
}
