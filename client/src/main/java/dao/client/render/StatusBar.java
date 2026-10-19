package dao.client.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class StatusBar {
  private final static float BAR_WIDTH_PIXELS = 40f;
  private final static float BAR_HEIGHT_PIXELS = 10f;
  private final static float BAR_OFFSET_ABOVE_HEAD_PIXELS = 60f;

  private final static float HP_BAR_LEFT_MARGIN_PIXELS = 1f;
  private final static float HP_BAR_VERTICAL_OFFSET_PIXELS = 5f;
  private final static float HP_BAR_HEIGHT_PIXELS = 4f;
  private final static float HP_BAR_HORIZONTAL_MARGIN_PIXELS = 2f;

  private final static float MP_BAR_LEFT_MARGIN_PIXELS = 2f;
  private final static float MP_BAR_VERTICAL_OFFSET_PIXELS = 1f;
  private final static float MP_BAR_HEIGHT_PIXELS = 3f;
  private final static float MP_BAR_HORIZONTAL_MARGIN_PIXELS = 3f;

  public void draw(ShapeRenderer shapeRenderer, float playerX, float playerY, float currentHp, float maxHp, float currentMp, float maxMp) {

    float drawX = playerX - (BAR_WIDTH_PIXELS / 2f);
    float drawY = playerY + BAR_OFFSET_ABOVE_HEAD_PIXELS;

    shapeRenderer.setColor(Color.BLACK);
    shapeRenderer.rect(drawX, drawY, BAR_WIDTH_PIXELS, BAR_HEIGHT_PIXELS);

    float hpWidth = (currentHp / maxHp) * (BAR_WIDTH_PIXELS - HP_BAR_HORIZONTAL_MARGIN_PIXELS);
    float mpWidth = (currentMp / maxMp) * (BAR_WIDTH_PIXELS - MP_BAR_HORIZONTAL_MARGIN_PIXELS);

    //hp
    shapeRenderer.setColor(Color.RED);
    shapeRenderer.rect(drawX + HP_BAR_LEFT_MARGIN_PIXELS, drawY + HP_BAR_VERTICAL_OFFSET_PIXELS, hpWidth, HP_BAR_HEIGHT_PIXELS);

    //mp
    shapeRenderer.setColor(Color.BLUE);
    shapeRenderer.rect(drawX + MP_BAR_LEFT_MARGIN_PIXELS, drawY + MP_BAR_VERTICAL_OFFSET_PIXELS, mpWidth, MP_BAR_HEIGHT_PIXELS);
  }
}
