package dao.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public final class DaoGame extends ApplicationAdapter {
  private static final float BACKGROUND_RED = 0.04f;
  private static final float BACKGROUND_GREEN = 0.04f;
  private static final float BACKGROUND_BLUE = 0.08f;
  private static final float BACKGROUND_ALPHA = 1.0f;
  private static final float PLAYER_RADIUS_PIXELS = 10f;
  private static final float PLAYER_RED = 1.0f;
  private static final float PLAYER_GREEN = 0.85f;
  private static final float PLAYER_BLUE = 0.4f;
  private static final float PLAYER_ALPHA = 1.0f;
  private static final float PLAYER_SPEED = 300f;

  private ShapeRenderer tutorialShape;
  private ShapeRenderer aura;
  private static final float AURA_ALPHA=0.10f;

  private float playerPositionX,playerPositionY;

  @Override
  public void create() {
    tutorialShape = new ShapeRenderer();
    aura = new ShapeRenderer();
  }

  @Override
  public void resize(int width, int height) {
    playerPositionX = width/2f;
    playerPositionY = height/2f;
  }

  @Override
  public void render() {
    float deltaTimeSecond=Gdx.graphics.getDeltaTime();
    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      playerPositionY += PLAYER_SPEED * deltaTimeSecond;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      playerPositionY -= PLAYER_SPEED * deltaTimeSecond;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      playerPositionX -= PLAYER_SPEED * deltaTimeSecond;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      playerPositionX += PLAYER_SPEED * deltaTimeSecond;
    }
    if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
      aura.setColor(1.0f,0.0f,0.0f,AURA_ALPHA);
      }
    if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
      aura.setColor(0.0f,0.04f,0.0f,AURA_ALPHA);
    }
    if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)){
      aura.setColor(0.0f,0.0f,0.08f,AURA_ALPHA);
    }

    //clear background
    Gdx.gl.glClearColor(BACKGROUND_RED,BACKGROUND_GREEN,BACKGROUND_BLUE,BACKGROUND_ALPHA);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    //render tutorialShape
    aura.begin(ShapeRenderer.ShapeType.Filled);
    aura.circle(playerPositionX+5f,playerPositionY+5f,PLAYER_RADIUS_PIXELS*3);
    aura.end();

    tutorialShape.begin(ShapeRenderer.ShapeType.Filled);
    tutorialShape.setColor(PLAYER_RED,PLAYER_GREEN,PLAYER_BLUE,PLAYER_ALPHA);
    tutorialShape.circle(playerPositionX,playerPositionY,PLAYER_RADIUS_PIXELS);
    tutorialShape.end();

  }

  @Override
  public void dispose() {
    tutorialShape.dispose();
    aura.dispose();
  }

}
