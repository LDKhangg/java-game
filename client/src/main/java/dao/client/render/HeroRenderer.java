package dao.client.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import dao.client.core.FacingDirection;
import dao.client.core.Player;


public class HeroRenderer {
  private static final String HERO_BACK_IMAGE_PATH="hero-back.png";
  private static final String HERO_FRONT_IMAGE_PATH="hero-front.png";
  private static final String HERO_SIDE_IMAGE_PATH="hero-side.png";
  private static final String HERO_FRONT_WALK_PATH="hero-front-walk.png";
  private static final String HERO_BACK_WALK_PATH = "hero-back-walk.png";
  private static final String HERO_SIDE_WALK_PATH = "hero-side-walk.png";
  private static final int WALK_FRAME_WIDTH_PIXELS=256;
  private static final int WALK_FRAME_HEIGHT_PIXELS=256;
  private static final float FRAME_DURATION_SECONDS=0.15f;
  private static final float HERO_DISPLAY_SIZE_PIXELS = 128f;
  private final Texture heroFrontTexture;
  private final Texture heroBackTexture;
  private final Texture heroSideTexture;
  private final Texture heroFrontWalkTexture;
  private final Texture heroBackWalkTexture;
  private final Texture heroSideWalkTexture;
  private final Animation<TextureRegion> heroFrontWalkAnimation;
  private final Animation<TextureRegion> heroBackWalkAnimation;
  private final Animation<TextureRegion> heroSideWalkAnimation;
  private float walkStateTimeSecond=0f;

  public HeroRenderer() {
    heroFrontTexture = new Texture(HERO_FRONT_IMAGE_PATH);
    heroFrontTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    heroBackTexture = new Texture(HERO_BACK_IMAGE_PATH);
    heroBackTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    heroSideTexture = new Texture(HERO_SIDE_IMAGE_PATH);
    heroSideTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

    heroFrontWalkTexture = new Texture(HERO_FRONT_WALK_PATH);
    heroFrontWalkTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    heroBackWalkTexture = new Texture(HERO_BACK_WALK_PATH);
    heroBackWalkTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    heroSideWalkTexture = new Texture(HERO_SIDE_WALK_PATH);
    heroSideWalkTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

    TextureRegion[][] frontTR = TextureRegion.split(heroFrontWalkTexture,WALK_FRAME_WIDTH_PIXELS,WALK_FRAME_HEIGHT_PIXELS);
    TextureRegion[][] backTR = TextureRegion.split(heroBackWalkTexture,WALK_FRAME_WIDTH_PIXELS,WALK_FRAME_HEIGHT_PIXELS);
    TextureRegion[][] sideTR = TextureRegion.split(heroSideWalkTexture,WALK_FRAME_WIDTH_PIXELS,WALK_FRAME_HEIGHT_PIXELS);

    heroFrontWalkAnimation = new Animation<>(FRAME_DURATION_SECONDS,frontTR[0]);
    heroBackWalkAnimation = new Animation<>(FRAME_DURATION_SECONDS,backTR[0]);
    heroSideWalkAnimation = new Animation<>(FRAME_DURATION_SECONDS,sideTR[0]);

  }

  public void draw(SpriteBatch sprite, Player player,float deltaTimeSecond){

    float heroDrawX = player.getPositionXPixel() - HERO_DISPLAY_SIZE_PIXELS / 2f;
    float heroDrawY = player.getPositionYPixel() - HERO_DISPLAY_SIZE_PIXELS / 2f;


    if (player.isMoving()) {
      walkStateTimeSecond += deltaTimeSecond;
    }

      FacingDirection fD=player.getFacingDirection();
      if(fD == FacingDirection.RIGHT){
        if(player.isMoving()){
        TextureRegion sideFrame = heroSideWalkAnimation.getKeyFrame(walkStateTimeSecond,true);
        sprite.draw(sideFrame,heroDrawX,heroDrawY,HERO_DISPLAY_SIZE_PIXELS,HERO_DISPLAY_SIZE_PIXELS);
        } else{
          sprite.draw(heroSideTexture,heroDrawX,heroDrawY,HERO_DISPLAY_SIZE_PIXELS,HERO_DISPLAY_SIZE_PIXELS);
        }
      } else if(fD == FacingDirection.LEFT){
        if(player.isMoving()){
        TextureRegion sideFrame = heroSideWalkAnimation.getKeyFrame(walkStateTimeSecond,true);
          sprite.draw(sideFrame, heroDrawX + HERO_DISPLAY_SIZE_PIXELS, heroDrawY,
            -HERO_DISPLAY_SIZE_PIXELS, HERO_DISPLAY_SIZE_PIXELS);
        } else {
          sprite.draw(heroSideTexture, heroDrawX + HERO_DISPLAY_SIZE_PIXELS, heroDrawY,
            -HERO_DISPLAY_SIZE_PIXELS, HERO_DISPLAY_SIZE_PIXELS);
        }
      } else if (fD == FacingDirection.BACK){
        if(player.isMoving()){
        TextureRegion backFrame = heroBackWalkAnimation.getKeyFrame(walkStateTimeSecond,true);
        sprite.draw(backFrame,heroDrawX,heroDrawY,HERO_DISPLAY_SIZE_PIXELS,HERO_DISPLAY_SIZE_PIXELS);
        } else{
          sprite.draw(heroBackTexture,heroDrawX,heroDrawY,HERO_DISPLAY_SIZE_PIXELS,HERO_DISPLAY_SIZE_PIXELS);
        }
      } else {
        if(player.isMoving()){
          TextureRegion frontFrame = heroFrontWalkAnimation.getKeyFrame(walkStateTimeSecond,true);
          sprite.draw(frontFrame,heroDrawX,heroDrawY,HERO_DISPLAY_SIZE_PIXELS,HERO_DISPLAY_SIZE_PIXELS);
        } else{
          sprite.draw(heroFrontTexture,heroDrawX,heroDrawY,HERO_DISPLAY_SIZE_PIXELS,HERO_DISPLAY_SIZE_PIXELS);
        }

      }
  }

  public void dispose(){
    this.heroFrontTexture.dispose();
    this.heroBackTexture.dispose();
    this.heroSideTexture.dispose();
    this.heroSideWalkTexture.dispose();
    this.heroBackWalkTexture.dispose();
    this.heroFrontWalkTexture.dispose();
  }
}
