package dao.client.core;

public record Intent(FacingDirection direction) {
  public float moveX(){
    if(direction ==null) return 0f;

    return switch (direction){
      case LEFT -> -1f;
      case RIGHT -> +1f;
      default -> 0f;
    };
  }
  public float moveY(){
    if(direction==null) return 0f;
    return switch(direction){
      case FRONT -> -1f;
      case BACK -> 1f;
      default -> 0f;
    };
  }
}
