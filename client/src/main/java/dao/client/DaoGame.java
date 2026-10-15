package dao.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

  private static final String HERO_IMAGE_PATH="hero.png";

  private ShapeRenderer tutorialShape;
  private Texture heroTexture;
  private SpriteBatch spriteBatch;

  private static StatusBar statusBar;

  private float playerPositionX,playerPositionY;
  private int minX,maxX;
  private int minY,maxY;

  private OrthographicCamera camera;


  @Override
  public void create() {
    tutorialShape = new ShapeRenderer();
    camera = new OrthographicCamera();
    camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
    statusBar = new StatusBar();
    heroTexture = new Texture(HERO_IMAGE_PATH);
    heroTexture.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);
    spriteBatch = new SpriteBatch();
  }

  @Override
  public void resize(int width, int height) {

    camera.setToOrtho(false,width,height);

    playerPositionX = width/2f;
    playerPositionY = height/2f;

    minX=minY=(int) PLAYER_RADIUS_PIXELS*3;
    maxX= Gdx.graphics.getWidth()-(int) PLAYER_RADIUS_PIXELS*3;
    maxY= Gdx.graphics.getHeight()-(int) PLAYER_RADIUS_PIXELS*3;
  }

  @Override
  public void render() {
    tutorialShape.setProjectionMatrix(camera.combined);
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

    playerPositionY = Math.clamp(playerPositionY,minY,maxY);
    playerPositionX = Math.clamp(playerPositionX,minX,maxX);
    //clear background
    Gdx.gl.glClearColor(BACKGROUND_RED,BACKGROUND_GREEN,BACKGROUND_BLUE,BACKGROUND_ALPHA);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    //turn on blending mode
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    //render tutorialShape
    tutorialShape.begin(ShapeRenderer.ShapeType.Filled);
    //draw halo
    tutorialShape.setColor(1.0f, 0.0f, 0.0f, 0.2f);
    tutorialShape.circle(playerPositionX, playerPositionY, PLAYER_RADIUS_PIXELS * 3);
    //draw main character
    tutorialShape.setColor(PLAYER_RED,PLAYER_GREEN,PLAYER_BLUE,PLAYER_ALPHA);
    tutorialShape.circle(playerPositionX,playerPositionY,PLAYER_RADIUS_PIXELS);

    statusBar.draw(tutorialShape,playerPositionX,playerPositionY+5,100,100,100,100);
    tutorialShape.end();

    spriteBatch.setProjectionMatrix(camera.combined);
    spriteBatch.begin();
    
    float heroDrawX = playerPositionX - heroTexture.getWidth() / 2f;
    float heroDrawY = playerPositionY - heroTexture.getHeight() / 2f;
    spriteBatch.draw(heroTexture, heroDrawX, heroDrawY);
    spriteBatch.end();

  }

  @Override
  public void dispose() {
    tutorialShape.dispose();
    spriteBatch.dispose();
  }

}
