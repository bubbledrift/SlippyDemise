package projects.WizTesting;

import engine.Application;
import engine.Screen;
import engine.UIToolKit.UIButton;
import engine.UIToolKit.UIRect;
import engine.UIToolKit.UIText;
import engine.UIToolKit.UIViewport;
import engine.game.GameWorld;
import engine.support.Vec2d;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class App extends Application {

  private final Color colorMain = Color.color(.8, .35, .35);
  private final Color colorBorder = Color.color(.4, 0, .15);
  private final Color colorBackground = Color.color(.6, .35, .35);

  private final Font fontLarge = Font.font("Ariel", FontWeight.BOLD, 50);
  private final Font fontNormal = Font.font("Ariel", FontWeight.NORMAL, 30);

  public App(String title) {
    super(title);
  }

  public App(String title, Vec2d windowSize, boolean debugMode, boolean fullscreen) {
    super(title, windowSize, debugMode, fullscreen);
  }

  /**
   * Called when the app is starting up.
   */
  @Override
  protected void onStartup() {
    super.onStartup();
    Screen mainMenu = new Screen();

    createMainMenu(mainMenu);


    this.setCurrentScreen("mainMenu");
  }

  private void createMainMenu(Screen mainMenu){
    mainMenu.addUIElement(new UIRect(new Vec2d(0,0), this.originalStageSize, colorBackground));
    mainMenu.addUIElement(new UIText(new Vec2d(100,80), new Vec2d(400, 50),"WIZ II",
            colorBorder, fontLarge));

    addButton(mainMenu, new Vec2d(100,100), 0);
    addButton(mainMenu, new Vec2d(100,200), 1);
    addButton(mainMenu, new Vec2d(100,300), 2);

    mainMenu.addUIElement(new UIText(new Vec2d(600,100), new Vec2d(400, 50),
            "WASD to move\nClick and SPACE to shoot\nMouse to aim\nScroll to zoom" +
                    "\n\nFind the door at the end \nof each level to proceed",
            colorBorder, fontNormal));

    this.addScreen(mainMenu, "mainMenu");
  }

  private void addButton(Screen mainMenu, Vec2d pos, long seed){
    UIButton startButton = new UIButton(pos, new Vec2d(400,50), colorMain, colorBorder);
    startButton.setOnMouseClicked(() -> {
      GameWorld gameWorld = new WizGameWorld();
      createGameScreen(new Screen(), gameWorld, seed);
      this.setCurrentScreen("gameScreen");
    });
    startButton.addChild(new UIText(new Vec2d(10,30), new Vec2d(400, 50),"START with seed " + (int)seed,
            colorBorder, fontNormal));

    mainMenu.addUIElement(startButton);

  }

  private WizGame createGameScreen(Screen gameScreen, GameWorld gameWorld, long seed){
    gameScreen.addUIElement(new UIRect(new Vec2d(0,0),
            new Vec2d(this.currentStageSize.x,this.currentStageSize.y), colorBackground));

    UIViewport viewport = new UIViewport(new Vec2d(0,0),
            new Vec2d(this.originalStageSize.x, this.originalStageSize.y),
            gameWorld, new Vec2d(0,0), 50, true);
    gameScreen.addUIElement(viewport);

    UIButton restartButton = new UIButton(new Vec2d(10,10), new Vec2d(150,50), colorMain, colorBorder);
    restartButton.setOnMouseClicked(() -> {
      createGameScreen(new Screen(), gameWorld, seed);
      this.setCurrentScreen("mainMenu");
    });
    restartButton.addChild(new UIText(new Vec2d(10,35), new Vec2d(150, 50),"RESTART",
            colorBorder, fontNormal));
    gameScreen.addUIElement(restartButton);

    this.addScreen(gameScreen, "gameScreen");

    WizGame game = new WizGame(gameWorld, viewport, seed);
    game.init();
    return game;
  }

}
