package dao.client;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public final class DesktopLauncher {
    //default config
    private static final String WINDOW_TITLE = "DaoDu";
    private static final int WINDOW_WIDTH_PIXELS = 1280;
    private static final int WINDOW_HEIGHT_PIXELS = 720;
    private static final int FOREGROUND_FPS = 60;
    void main(String[] args) {
      Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
      configuration.setTitle(WINDOW_TITLE);
      configuration.setWindowedMode(WINDOW_WIDTH_PIXELS,WINDOW_HEIGHT_PIXELS);
      configuration.setForegroundFPS(FOREGROUND_FPS);
      configuration.useVsync(true);
      new Lwjgl3Application(new DaoGame(),configuration);

    }
}
