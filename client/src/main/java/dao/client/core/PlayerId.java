package dao.client.core;

public record PlayerId(int value) {
  public PlayerId{
    if(value < 0){
      throw new IllegalArgumentException("Invalid id");
    }
  }
}
