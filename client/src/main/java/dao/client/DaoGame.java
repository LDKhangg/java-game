package dao.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dao.client.core.Intent;
import dao.client.core.Player;
import dao.client.core.PlayerId;
import dao.client.core.World;
import dao.client.input.Brain;
import dao.client.input.PlayerBrain;
import dao.client.render.HeroRenderer;
import dao.client.render.SectMap;
import dao.client.render.StatusBar;


public final class DaoGame extends ApplicationAdapter {
  private static final float BACKGROUND_RED = 0.04f;
  private static final float BACKGROUND_GREEN = 0.04f;
  private static final float BACKGROUND_BLUE = 0.08f;
  private static final float BACKGROUND_ALPHA = 1.0f;
  private static final float PLAYER_RADIUS_PIXELS = 10f;
  private static final float PLAYER_SPEED_PER_SECOND = 300f;

  private StatusBar statusBar;
  private ShapeRenderer shapeRenderer;
  private SpriteBatch spriteBatch;
  private HeroRenderer heroRenderer;
  private Brain playerBrain;
  private SectMap map;
  private World world;

  private int minX,maxX;
  private int minY,maxY;

  private OrthographicCamera camera;


  @Override
  public void create() {

    spriteBatch = new SpriteBatch();
    shapeRenderer = new ShapeRenderer();
    camera = new OrthographicCamera();

    camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

    statusBar = new StatusBar();
    map = new SectMap();
    heroRenderer = new HeroRenderer();
    playerBrain = new PlayerBrain();
    Player player = new Player(new PlayerId(1),"Hero",1);

    minX = minY = (int)PLAYER_RADIUS_PIXELS*5;
    maxX= SectMap.MAP_WIDTH_PIXELS-(int)PLAYER_RADIUS_PIXELS*2;
    maxY=SectMap.MAP_HEIGHT_PIXELS-(int)PLAYER_RADIUS_PIXELS*5;

    player.setPositionXPixel(minX);
    player.setPositionYPixel(maxY/2);
    world = new World();
    world.addPlayer(player);

  }

  @Override
  public void resize(int width, int height) {

    camera.setToOrtho(false,width,height);

  }

  @Override
  public void render() {
    shapeRenderer.setProjectionMatrix(camera.combined);
    camera.position.set(
      Math.clamp(world.getLocalPlayer().getPositionXPixel(), camera.viewportWidth / 2f, SectMap.MAP_WIDTH_PIXELS- camera.viewportWidth / 2f),
      Math.clamp(world.getLocalPlayer().getPositionYPixel(), camera.viewportHeight / 2f, SectMap.MAP_HEIGHT_PIXELS - camera.viewportHeight / 2f),
      0f);
    camera.update();
    float deltaTimeSecond=Gdx.graphics.getDeltaTime();
    Intent intent = playerBrain.think(world.getLocalPlayer(),deltaTimeSecond);
    if (intent.direction() != null) {
      world.getLocalPlayer().move(intent.direction(), PLAYER_SPEED_PER_SECOND, deltaTimeSecond);
    } else {
      world.getLocalPlayer().stop();
    }
    world.getLocalPlayer().setPositionXPixel(Math.clamp(world.getLocalPlayer().getPositionXPixel(),minX,maxX));
    world.getLocalPlayer().setPositionYPixel(Math.clamp(world.getLocalPlayer().getPositionYPixel(),minY,maxY));

    //clear background
    Gdx.gl.glClearColor(BACKGROUND_RED,BACKGROUND_GREEN,BACKGROUND_BLUE,BACKGROUND_ALPHA);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    spriteBatch.setProjectionMatrix(camera.combined);
    spriteBatch.begin();
    map.draw(spriteBatch);
    heroRenderer.draw(spriteBatch,world.getLocalPlayer(),deltaTimeSecond);
    spriteBatch.end();

    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    statusBar.draw(shapeRenderer,
    world.getLocalPlayer().getPositionXPixel(),
      world.getLocalPlayer().getPositionYPixel() + 4,
      world.getLocalPlayer().getCurrHealPoints(),
      world.getLocalPlayer().getMaxHealPoints(),
      world.getLocalPlayer().getCurrManaPoints(),
      world.getLocalPlayer().getMaxManaPoints());
    shapeRenderer.end();
  }

  @Override
  public void dispose() {
    shapeRenderer.dispose();
    spriteBatch.dispose();
    map.dispose();
    heroRenderer.dispose();
  }

}
