package dao.client.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SectMap {
  public static final int MAP_WIDTH_PIXELS = 1920;
  public static final int MAP_HEIGHT_PIXELS = 1080;
  public static final String SECT_MAP_IMAGE_PATH = "sect-map.png";

  private final Texture sectMapTexture;

  public SectMap() {
    sectMapTexture = new Texture(SECT_MAP_IMAGE_PATH);
    sectMapTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
  }

  public void draw(SpriteBatch spriteBatch) {
    spriteBatch.draw(sectMapTexture, 0f, 0f, MAP_WIDTH_PIXELS, MAP_HEIGHT_PIXELS);
  }

  public void dispose() {
    sectMapTexture.dispose();
  }
}
