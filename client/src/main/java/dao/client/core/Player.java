package dao.client.core;

public class Player {

  private static final float INTERPOLATION_DURATION_SECONDS = 1f / 20f;
  private PlayerId id;
  private PlayerState state;
  private float positionXPixel;
  private float positionYPixel;
  private FacingDirection facingDirection;
  //status bar
  private int currHealPoints;
  private int  currManaPoints;
  private int maxHealPoints;
  private int maxManaPoints;
  private int level;
  private int experiencePointsToNextLevel;
  private float currExperiencePoint;
  private String displayName;
  private int realmId;
  //interpolation
  private float startX,startY;
  private float targetX,targetY;
  private float interpolationElapsedSeconds;

  public Player(PlayerId value,String displayName,int realmId){
    this.id = value;
    this.displayName = displayName;
    this.realmId = realmId;
    this.state = PlayerState.IDLE;
    this.currHealPoints = 100;
    this.maxHealPoints = 100;
    this.currManaPoints = 50;
    this.maxManaPoints = 50;
    this.level = 1;
    this.currExperiencePoint = 0f;
    this.experiencePointsToNextLevel = 100;
    this.facingDirection = FacingDirection.FRONT;
  }

  public void move(FacingDirection direction, float speedPixelsPerSecond,float deltaTimePerSecond){
    this.facingDirection = direction;
    this.state = PlayerState.MOVING;
    float distance = speedPixelsPerSecond * deltaTimePerSecond;
    switch (direction){
      case FRONT -> positionYPixel-=distance;
      case BACK -> positionYPixel+=distance;
      case LEFT -> positionXPixel -=distance;
      case RIGHT -> positionXPixel +=distance;
    }

  }

  public void applyDamage(int value){
    currHealPoints = Math.max(0,currHealPoints-value);
  }
  public void heal(int value){
    currHealPoints = Math.min(maxHealPoints,currHealPoints+value);
  }

  public void stop(){
    this.state=PlayerState.IDLE;
  }

  public boolean isMoving(){
    return state == PlayerState.MOVING ;
  }

  public PlayerId getId() {
    return id;
  }

  public void setId(PlayerId id) {
    this.id = id;
  }

  public PlayerState getState() {
    return state;
  }

  public void setState(PlayerState state) {
    this.state = state;
  }

  public float getPositionXPixel() {
    return positionXPixel;
  }

  public void setPositionXPixel(float positionXPixel) {
    this.positionXPixel = positionXPixel;
  }

  public float getPositionYPixel() {
    return positionYPixel;
  }

  public void setPositionYPixel(float positionYPixel) {
    this.positionYPixel = positionYPixel;
  }

  public FacingDirection getFacingDirection() {
    return facingDirection;
  }

  public void setFacingDirection(FacingDirection facingDirection) {
    this.facingDirection = facingDirection;
  }

  public int getCurrHealPoints() {
    return currHealPoints;
  }

  public void setCurrHealPoints(int currHealPoints) {
    this.currHealPoints = currHealPoints;
  }

  public int getCurrManaPoints() {
    return currManaPoints;
  }

  public void setCurrManaPoints(int currManaPoints) {
    this.currManaPoints = currManaPoints;
  }

  public int getMaxHealPoints() {
    return maxHealPoints;
  }

  public void setMaxHealPoints(int maxHealPoints) {
    this.maxHealPoints = maxHealPoints;
  }

  public int getMaxManaPoints() {
    return maxManaPoints;
  }

  public void setMaxManaPoints(int maxManaPoints) {
    this.maxManaPoints = maxManaPoints;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public int getExperiencePointsToNextLevel() {
    return experiencePointsToNextLevel;
  }

  public void setExperiencePointsToNextLevel(int experiencePointsToNextLevel) {
    this.experiencePointsToNextLevel = experiencePointsToNextLevel;
  }

  public float getCurrExperiencePoint() {
    return currExperiencePoint;
  }

  public void setCurrExperiencePoint(float currExperiencePoint) {
    this.currExperiencePoint = currExperiencePoint;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public int getRealmId() {
    return realmId;
  }

  public void setRealmId(int realmId) {
    this.realmId = realmId;
  }

  public void setInterpolationTarget(float newX,float newY){
    startX = positionXPixel;
    startY = positionYPixel;

    targetX = newX;
    targetY=newY;

    interpolationElapsedSeconds =0f;
  }
  public void updateInterpolation(float deltaTimePerSecond){
    interpolationElapsedSeconds = Math.min(interpolationElapsedSeconds+deltaTimePerSecond,INTERPOLATION_DURATION_SECONDS);
    float alpha = interpolationElapsedSeconds/INTERPOLATION_DURATION_SECONDS;
    positionXPixel =
      startX
        + (targetX - startX) * alpha;
    positionYPixel =
      startY
        + (targetY - startY) * alpha;
  }
  public float getInterpolationTargetX() { return targetX; }
  public float getInterpolationTargetY() { return targetY; }
}
