package dao.client;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class StatusBar {
  private final static float BAR_WIDTH_PIXELS=40f;
  private final static float BAR_HEIGHT_PIXELS=10f;

  public void draw(ShapeRenderer shapeRenderer, float playerX,float playerY,float currentHp, float maxHp, float currentMp, float maxMp){

    float drawX = playerX - (BAR_WIDTH_PIXELS / 2f);
    float drawY = playerY + 30f;

    shapeRenderer.setColor(Color.BLACK);
    shapeRenderer.rect(drawX, drawY, BAR_WIDTH_PIXELS, BAR_HEIGHT_PIXELS);

    float hpWidth = (currentHp / maxHp) * (BAR_WIDTH_PIXELS - 2f);
    float mpWidth = (currentMp / maxMp) * (BAR_WIDTH_PIXELS - 3f);

    //hp
    shapeRenderer.setColor(Color.RED);
    shapeRenderer.rect(drawX+1f, drawY + 5f, hpWidth, 4f);

    //mp
    shapeRenderer.setColor(Color.BLUE);
    shapeRenderer.rect(drawX+2f, drawY + 1f, mpWidth, 3f);
  }
}
