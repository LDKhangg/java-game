package dao.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public final class DaoGame extends ApplicationAdapter {
  private static final float BACKGROUND_RED = 0.04f;
  private static final float BACKGROUND_GREEN = 0.04f;
  private static final float BACKGROUND_BLUE = 0.08f;
  private static final float BACKGROUND_ALPHA = 1.0f;
  private static final float PLAYER_RADIUS_PIXELS = 10f;
  private static final float PLAYER_SPEED_PER_SECOND = 300f;

  private static final String HERO_BACK_IMAGE_PATH="hero-back.png";
  private static final String HERO_FRONT_IMAGE_PATH="hero-front.png";
  private static final String HERO_SIDE_IMAGE_PATH="hero-side.png";

  private static final String SECT_MAP_IMAGE_PATH="sect-map.png";
  private static final int MAP_WIDTH_PIXELS=1920;
  private static final int MAP_HEIGHT_PIXELS=1080;
  private static final String HERO_FRONT_WALK_PATH="hero-front-walk.png";
  private static final String HERO_BACK_WALK_PATH = "hero-back-walk.png";
  private static final String HERO_SIDE_WALK_PATH = "hero-side-walk.png";

  private static final int WALK_FRAME_COUNT=4;
  private static final int WALK_FRAME_WIDTH_PIXELS=256;
  private static final int WALK_FRAME_HEIGHT_PIXELS=256;
  private static final float FRAME_DURATION_SECONDS=0.15f;
  private static final float HERO_DISPLAY_SIZE_PIXELS = 128f;
  private static StatusBar statusBar;
  private ShapeRenderer tutorialShape;
  private Texture heroTexture;
  private Texture heroSideTexture;
  private Texture heroFrontTexture;
  private SpriteBatch spriteBatch;
  private Texture sectMapTexture;
  //Hero front walk
  private Texture heroFrontWalkTexture;
  private Animation<TextureRegion> heroFrontWalkAnimation;
  //Hero back walk
  private Texture heroBackWalkTexture;
  private Animation<TextureRegion> heroBackWalkAnimation;
  //Hero side walk
  private Texture heroSideWalkTexture;
  private Animation<TextureRegion> heroSideWalkAnimation;
  private float walkStateTimeSecond=0f;
  private String facingDirection = "front";
  private float playerPositionX,playerPositionY;

  private int minX,maxX;
  private int minY,maxY;

  private int mapPixelWidth;
  private int mapPixelHeight;

  private OrthographicCamera camera;


  @Override
  public void create() {

    tutorialShape = new ShapeRenderer();
    camera = new OrthographicCamera();
    statusBar = new StatusBar();

    camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

    heroTexture = new Texture(HERO_BACK_IMAGE_PATH);
    heroTexture.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);

    heroSideTexture = new Texture(HERO_SIDE_IMAGE_PATH);
    heroSideTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

    heroFrontTexture = new Texture(HERO_FRONT_IMAGE_PATH);
    heroFrontTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

    //backwalk init
    heroBackWalkTexture = new Texture(HERO_BACK_WALK_PATH);
    heroBackWalkTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    TextureRegion[][] bangCat = TextureRegion.split(heroBackWalkTexture, WALK_FRAME_WIDTH_PIXELS, WALK_FRAME_HEIGHT_PIXELS);
    heroBackWalkAnimation = new Animation<>(FRAME_DURATION_SECONDS,bangCat[0]);
    //sidewalk init
    heroSideWalkTexture = new Texture(HERO_SIDE_WALK_PATH);
    heroSideWalkTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    TextureRegion[][] bangCat2 = TextureRegion.split(heroSideWalkTexture,WALK_FRAME_WIDTH_PIXELS,WALK_FRAME_WIDTH_PIXELS);
    heroSideWalkAnimation = new Animation<>(FRAME_DURATION_SECONDS,bangCat2[0]);
    //frontwalk init
    heroFrontWalkTexture = new Texture(HERO_FRONT_WALK_PATH);
    heroFrontWalkTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    TextureRegion[][] bangCat3 = TextureRegion.split(heroFrontWalkTexture,WALK_FRAME_WIDTH_PIXELS,WALK_FRAME_WIDTH_PIXELS);
    heroFrontWalkAnimation = new Animation<>(FRAME_DURATION_SECONDS,bangCat3[0]);


    spriteBatch = new SpriteBatch();

    sectMapTexture = new Texture(SECT_MAP_IMAGE_PATH);
    sectMapTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    mapPixelWidth = MAP_WIDTH_PIXELS;
    mapPixelHeight = MAP_HEIGHT_PIXELS;

    minX = minY = (int)PLAYER_RADIUS_PIXELS*5;
    maxX= mapPixelWidth-(int)PLAYER_RADIUS_PIXELS*2;
    maxY=mapPixelHeight-(int)PLAYER_RADIUS_PIXELS*5;

    playerPositionX = minX;
    playerPositionY=maxY;

    }

  @Override
  public void resize(int width, int height) {

    camera.setToOrtho(false,width,height);

  }

  @Override
  public void render() {
    tutorialShape.setProjectionMatrix(camera.combined);
    camera.position.set(
      Math.clamp(playerPositionX, camera.viewportWidth / 2f, mapPixelWidth- camera.viewportWidth / 2f),
      Math.clamp(playerPositionY, camera.viewportHeight / 2f, mapPixelHeight - camera.viewportHeight / 2f),
      0f);
    camera.update();
    float deltaTimeSecond=Gdx.graphics.getDeltaTime();
    boolean isMoving= Gdx.input.isKeyPressed(Input.Keys.W)
      || Gdx.input.isKeyPressed(Input.Keys.S)
      || Gdx.input.isKeyPressed(Input.Keys.A)
      || Gdx.input.isKeyPressed(Input.Keys.D);

    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      facingDirection = "back";
      playerPositionY += PLAYER_SPEED_PER_SECOND * deltaTimeSecond;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      facingDirection = "front";
      playerPositionY -= PLAYER_SPEED_PER_SECOND * deltaTimeSecond;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      facingDirection = "left";
      playerPositionX -= PLAYER_SPEED_PER_SECOND * deltaTimeSecond;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      facingDirection = "right";
      playerPositionX += PLAYER_SPEED_PER_SECOND * deltaTimeSecond;
    }

    playerPositionY = Math.clamp(playerPositionY,minY,maxY);
    playerPositionX = Math.clamp(playerPositionX,minX,maxX);
    //clear background
    Gdx.gl.glClearColor(BACKGROUND_RED,BACKGROUND_GREEN,BACKGROUND_BLUE,BACKGROUND_ALPHA);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    spriteBatch.setProjectionMatrix(camera.combined);
    spriteBatch.begin();
    spriteBatch.draw(sectMapTexture, 0f, 0f, MAP_WIDTH_PIXELS, MAP_HEIGHT_PIXELS);
    if (isMoving) {
  walkStateTimeSecond += deltaTimeSecond;
}

float heroDrawX = playerPositionX - HERO_DISPLAY_SIZE_PIXELS / 2f;
float heroDrawY = playerPositionY - HERO_DISPLAY_SIZE_PIXELS / 2f;

if (facingDirection.equals("right")) {
  if (isMoving) {
    TextureRegion sideFrame = heroSideWalkAnimation.getKeyFrame(walkStateTimeSecond, true);
    spriteBatch.draw(sideFrame, heroDrawX, heroDrawY,
        HERO_DISPLAY_SIZE_PIXELS, HERO_DISPLAY_SIZE_PIXELS);
  } else {
    spriteBatch.draw(heroSideTexture, heroDrawX, heroDrawY,
        HERO_DISPLAY_SIZE_PIXELS, HERO_DISPLAY_SIZE_PIXELS);
  }
} else if (facingDirection.equals("left")) {
  if (isMoving) {
    TextureRegion sideFrame = heroSideWalkAnimation.getKeyFrame(walkStateTimeSecond, true);
    spriteBatch.draw(sideFrame, heroDrawX + HERO_DISPLAY_SIZE_PIXELS, heroDrawY,
        -HERO_DISPLAY_SIZE_PIXELS, HERO_DISPLAY_SIZE_PIXELS);
  } else {
    spriteBatch.draw(heroSideTexture, heroDrawX + HERO_DISPLAY_SIZE_PIXELS, heroDrawY,
        -HERO_DISPLAY_SIZE_PIXELS, HERO_DISPLAY_SIZE_PIXELS);
  }
} else if (facingDirection.equals("back")) {
  if (isMoving) {
    TextureRegion backFrame = heroBackWalkAnimation.getKeyFrame(walkStateTimeSecond, true);
    spriteBatch.draw(backFrame, heroDrawX, heroDrawY,
        HERO_DISPLAY_SIZE_PIXELS, HERO_DISPLAY_SIZE_PIXELS);
  } else {
    spriteBatch.draw(heroTexture, heroDrawX, heroDrawY,
        HERO_DISPLAY_SIZE_PIXELS, HERO_DISPLAY_SIZE_PIXELS);
  }
} else {
  if (isMoving) {
    TextureRegion frontFrame = heroFrontWalkAnimation.getKeyFrame(walkStateTimeSecond, true);
    spriteBatch.draw(frontFrame, heroDrawX, heroDrawY,
        HERO_DISPLAY_SIZE_PIXELS, HERO_DISPLAY_SIZE_PIXELS);
  } else {
    spriteBatch.draw(heroFrontTexture, heroDrawX, heroDrawY,
        HERO_DISPLAY_SIZE_PIXELS, HERO_DISPLAY_SIZE_PIXELS);
  }
}
spriteBatch.end();

    //turn on blending mode
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    //render tutorialShape
    tutorialShape.begin(ShapeRenderer.ShapeType.Filled);
    //draw main character
    statusBar.draw(tutorialShape,playerPositionX,playerPositionY+5,100,100,100,100);
    tutorialShape.end();
  }

  @Override
  public void dispose() {
    tutorialShape.dispose();
    heroTexture.dispose();
    spriteBatch.dispose();
    sectMapTexture.dispose();
  }

}
