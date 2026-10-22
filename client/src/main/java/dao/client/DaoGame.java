package dao.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import dao.GameConstants;
import dao.client.core.Intent;
import dao.client.core.Player;
import dao.client.core.PlayerId;
import dao.client.core.World;
import dao.client.input.Brain;
import dao.client.input.PlayerBrain;
import dao.client.net.NettyClient;
import dao.client.net.SnapshotApplier;
import dao.client.prediction.ClientPrediction;
import dao.client.render.HeroRenderer;
import dao.client.render.SectMap;
import dao.client.render.StatusBar;
import dao.protocol.Snapshot;


public final class DaoGame extends ApplicationAdapter {
  private static final float BACKGROUND_RED = 0.04f;
  private static final float BACKGROUND_GREEN = 0.04f;
  private static final float BACKGROUND_BLUE = 0.08f;
  private static final float BACKGROUND_ALPHA = 1.0f;

  private static final String PLAYER_NAME = "Hero";

  private BitmapFont loadingFont;
  private StatusBar statusBar;
  private ShapeRenderer shapeRenderer;
  private SpriteBatch spriteBatch;
  private HeroRenderer heroRenderer;
  private Brain playerBrain;
  private SectMap map;
  private World world;

  private NettyClient nettyClient;

  private ClientPrediction clientPrediction;

  private OrthographicCamera camera;


  @Override
  public void create() {

    clientPrediction = new ClientPrediction();

    spriteBatch = new SpriteBatch();
    shapeRenderer = new ShapeRenderer();
    camera = new OrthographicCamera();

    camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

    statusBar = new StatusBar();
    map = new SectMap();
    heroRenderer = new HeroRenderer();
    playerBrain = new PlayerBrain();

    world = new World();

    loadingFont = new BitmapFont();

    nettyClient = new NettyClient();
    nettyClient.connect("localhost", GameConstants.SERVER_PORT, PLAYER_NAME);

  }

  @Override
  public void resize(int width, int height) {

    camera.setToOrtho(false,width,height);

  }

  @Override
  public void render() {
    float deltaTimeSecond=Gdx.graphics.getDeltaTime();

    Snapshot latest = null;
    Snapshot polled;
    while ((polled = nettyClient.pollSnapshot()) != null) {
      latest = polled;
    }
    if (latest != null) {
      SnapshotApplier.apply(latest, world);
    }

    int myId = nettyClient.myId();
    if (myId != -1 && world.getLocalPlayer() == null) {
      world.setLocalPlayerId(new PlayerId(myId));
    }
    Player me = world.getLocalPlayer();
    if(latest!=null && me!=null){
      clientPrediction.reconcile(me,latest);
    }
    for(Player player:world.getPlayers().values()){
      if(!world.isLocalPlayer(player.getId())){
        player.updateInterpolation(deltaTimeSecond);
      }
    }

    Gdx.gl.glClearColor(BACKGROUND_RED,BACKGROUND_GREEN,BACKGROUND_BLUE,BACKGROUND_ALPHA);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    if (me != null) {
      Intent intent = playerBrain.think(me, deltaTimeSecond);

      clientPrediction.update(
        me,
        intent,
        deltaTimeSecond,
        nettyClient
      );

      camera.position.set(
        Math.clamp(me.getPositionXPixel(), camera.viewportWidth / 2f, SectMap.MAP_WIDTH_PIXELS- camera.viewportWidth / 2f),
        Math.clamp(me.getPositionYPixel(), camera.viewportHeight / 2f, SectMap.MAP_HEIGHT_PIXELS - camera.viewportHeight / 2f),
        0f);
    }
    camera.update();

    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    spriteBatch.setProjectionMatrix(camera.combined);
    spriteBatch.begin();
    map.draw(spriteBatch);
    if (me == null) {
      loadingFont.draw(spriteBatch, "Dang ket noi...",
        camera.position.x - camera.viewportWidth / 2f,
        camera.position.y,
        camera.viewportWidth,
        Align.center,
        false);
      spriteBatch.end();
      return;
    }
    for (Player player : world.getPlayers().values()) {
      heroRenderer.draw(spriteBatch, player, deltaTimeSecond);
    }
    spriteBatch.end();

    shapeRenderer.setProjectionMatrix(camera.combined);
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    for (Player player : world.getPlayers().values()) {
      statusBar.draw(shapeRenderer,
        player.getPositionXPixel(),
        player.getPositionYPixel() + 4,
        player.getCurrHealPoints(),
        player.getMaxHealPoints(),
        player.getCurrManaPoints(),
        player.getMaxManaPoints());
    }
    shapeRenderer.end();
  }

  @Override
  public void dispose() {
    loadingFont.dispose();
    shapeRenderer.dispose();
    spriteBatch.dispose();
    map.dispose();
    heroRenderer.dispose();
    nettyClient.close();
  }

}
