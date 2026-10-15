package dao.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/** Khung client rỗng: chỉ mở cửa sổ tối, chưa có gameplay. */
public final class DaoGame extends ApplicationAdapter {
  private static final float BACKGROUND_RED = 0.04f;
  private static final float BACKGROUND_GREEN = 0.04f;
  private static final float BACKGROUND_BLUE = 0.08f;
  private static final float BACKGROUND_ALPHA = 1.0f;

  @Override
  public void create() {
    // Khởi tạo tài nguyên game (hiện chưa có gì).
  }

  @Override
  public void render() {
    // Xóa màn hình bằng màu tối mỗi khung hình.
    Gdx.gl.glClearColor(BACKGROUND_RED, BACKGROUND_GREEN, BACKGROUND_BLUE, BACKGROUND_ALPHA);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
  }

  @Override
  public void dispose() {
    // Giải phóng tài nguyên (hiện chưa có gì).
  }
}
