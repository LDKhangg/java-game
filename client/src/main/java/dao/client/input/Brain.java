package dao.client.input;

import dao.client.core.Intent;
import dao.client.core.Player;

public interface Brain {

  Intent think(Player player, float deltaTimeSecond);
}
