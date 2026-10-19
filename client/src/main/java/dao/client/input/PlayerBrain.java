package dao.client.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import dao.client.core.FacingDirection;
import dao.client.core.Intent;
import dao.client.core.Player;

public class PlayerBrain implements Brain{
  @Override
  public Intent think(Player player, float deltaTimeSecond) {
    if( Gdx.input.isKeyPressed(Input.Keys.W)) return new Intent(FacingDirection.BACK);
    if( Gdx.input.isKeyPressed(Input.Keys.S)) return new Intent(FacingDirection.FRONT);
    if( Gdx.input.isKeyPressed(Input.Keys.A)) return new Intent(FacingDirection.LEFT);
    if( Gdx.input.isKeyPressed(Input.Keys.D)) return new Intent(FacingDirection.RIGHT);

    return new Intent(null);
  }
}
