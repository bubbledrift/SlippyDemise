package projects.final_project;

import engine.Application;
import engine.Screen;
import engine.UIToolKit.*;
import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.components.BooleanComponent;
import engine.game.components.HealthComponent;
import engine.game.components.ValueComponent;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class App extends Application {

  private final Color colorMain = Color.color(.6, .95, .9, .9);
  private final Color colorBorder = Color.color(.4, 0, .15);
  private final Color colorBackground = Color.color(.5, .9, .8, .9);

  private final Font fontLarge = Font.font("Ariel", FontWeight.BOLD, 50);
  private final Font fontNormal = Font.font("Ariel", FontWeight.NORMAL, 30);
  private final Font fontSmall = Font.font("Ariel", FontWeight.NORMAL, 14);
  private final Font fontSCORE = Font.font("Ariel", FontWeight.BOLD, 30);

  private FinalGame finalGame;
  private MenuGameWorld menuWorld;

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
    Screen controlsScreen = new Screen();
    Screen creditsScreen = new Screen();
    Screen gameScreen = new Screen(){
      @Override
      protected void onKeyReleased(KeyEvent e) {
        super.onKeyReleased(e);
        if(e.getCode() == KeyCode.ESCAPE){
          BackgroundMusic.pauseBGM(finalGame.gameWorld);
          setCurrentScreen("mainMenu");
          BackgroundMusic.resumeBGM(menuWorld.gameWorld);
        }
      }
    };

    createMainMenu(mainMenu);
    createControlsScreen(controlsScreen);
    createCreditsScreen(creditsScreen);
    createGameScreen(gameScreen);

    this.setCurrentScreen("mainMenu");
    BackgroundMusic.playMenuBGM(menuWorld.gameWorld);
  }

  private void createMainMenu(Screen mainMenu){
//    mainMenu.addUIElement(new UIRect(new Vec2d(0,0), this.originalStageSize, colorBackground));
    GameWorld gameWorld = new GameWorld();

    UIViewport viewport = new UIViewport(new Vec2d(0,0),
            new Vec2d(this.originalStageSize.x, this.originalStageSize.y),
            gameWorld, new Vec2d(0,0), 50, true);
    mainMenu.addUIElement(viewport);
    menuWorld = new MenuGameWorld(gameWorld, viewport);
    menuWorld.init();

    mainMenu.addUIElement(new UIRect(new Vec2d(100,30), new Vec2d(640, 70), colorMain));
    mainMenu.addUIElement(new UIText(new Vec2d(120,80), new Vec2d(400, 50),"S L I P P Y ' S   D E M I S E",
            colorBorder, fontLarge));

    UIButton startButton = new UIButton(new Vec2d(100,120), new Vec2d(200,50), colorMain, colorBorder);
    startButton.setOnMouseClicked(() -> {
      finalGame.resetGameWorld();
      finalGame.loadPlayer();
      finalGame.init();
      BackgroundMusic.pauseBGM(menuWorld.gameWorld);
      this.setCurrentScreen("gameScreen");
      BackgroundMusic.resumeBGM(finalGame.gameWorld);
    });
    startButton.addChild(new UIText(new Vec2d(10,30), new Vec2d(400, 50),"New Game", colorBorder, fontNormal));
    mainMenu.addUIElement(startButton);

    UIButton continueButton = new UIButton(new Vec2d(350,120), new Vec2d(200,50), colorMain, colorBorder){
      @Override
      public void onDraw(GraphicsContext g) {
        if(finalGame.hasStarted) super.onDraw(g);
      }
      @Override
      public void onTick(long nanosSincePreviousTick) {
        if(finalGame.hasStarted) super.onTick(nanosSincePreviousTick);
      }
      @Override
      public void onMouseClicked(MouseEvent e, Vec2d shift) {
        if(finalGame.hasStarted) super.onMouseClicked(e, shift);
      }
    };
    continueButton.setOnMouseClicked(() -> {
      BackgroundMusic.pauseBGM(menuWorld.gameWorld);
      this.setCurrentScreen("gameScreen");
      BackgroundMusic.resumeBGM(finalGame.gameWorld);
    });
    continueButton.addChild(new UIText(new Vec2d(10,30), new Vec2d(400, 50),"Continue", colorBorder, fontNormal));
    mainMenu.addUIElement(continueButton);

    UIButton controlsButton = new UIButton(new Vec2d(100,200), new Vec2d(200,50), colorMain, colorBorder);
    controlsButton.setOnMouseClicked(() -> {
      this.setCurrentScreen("controlsScreen");
    });
    controlsButton.addChild(new UIText(new Vec2d(10,30), new Vec2d(400, 50),"Controls", colorBorder, fontNormal));
    mainMenu.addUIElement(controlsButton);

    UIButton creditsButton = new UIButton(new Vec2d(100,280), new Vec2d(200,50), colorMain, colorBorder);
    creditsButton.setOnMouseClicked(() -> {
      this.setCurrentScreen("creditsScreen");
    });
    creditsButton.addChild(new UIText(new Vec2d(10,30), new Vec2d(400, 50),"Credits", colorBorder, fontNormal));
    mainMenu.addUIElement(creditsButton);

    this.addScreen(mainMenu, "mainMenu");
  }

  private void createControlsScreen(Screen controlsScreen){
    controlsScreen.addUIElement(new UIRect(new Vec2d(0,0), this.originalStageSize, colorBackground));


    controlsScreen.addUIElement(new UIImage(new Image(FinalGame.getSpritePath("keyboard")),
            new Vec2d(10,65),
            new Vec2d(940,400)));

    controlsScreen.addUIElement(new UIText(new Vec2d(20,45), new Vec2d(400, 50),"Controls",
            colorBorder, fontLarge));


    UIButton returnButton = new UIButton(new Vec2d(800,460), new Vec2d(130,50), colorMain, colorBorder);
    returnButton.setOnMouseClicked(() -> {
      this.setCurrentScreen("mainMenu");
    });
    returnButton.addChild(new UIText(new Vec2d(10,30), new Vec2d(400, 50),"Back", colorBorder, fontNormal));

    controlsScreen.addUIElement(returnButton);
    this.addScreen(controlsScreen, "controlsScreen");
  }

  private void createCreditsScreen(Screen creditsScreen){
    creditsScreen.addUIElement(new UIImage(new Image(FinalGame.getSpritePath("ControlsBackground")), new Vec2d(0,0), this.originalStageSize));
    creditsScreen.addUIElement(new UIRect(new Vec2d(80,30), new Vec2d(800,500), colorBackground));
    creditsScreen.addUIElement(new UIText(new Vec2d(100,80), new Vec2d(400, 50),"Credits",
            colorBorder, fontLarge));
    String text = "Universal-LPC-spritesheet : https://github.com/jrconway3/Universal-LPC-spritesheet\n" +
            " - Credit goes to authors listed in credits directory, CC-BY-SA 3.0 and GNU GPL 3.0 licences\n" +
            "Thanks to Ivan Voirol for creating the RPG tilemap, \n" +
            " - OpenGameArt Link: https://opengameart.org/comment/31378, CC-BY-SA 3.0, GNU GPL 3.0, and GNU GPL 2.0 licences\n" +
            "Thanks to Davias for the Simulation RPG Tsukuru spritesheet for cave levels\n" +
            " - Link: https://www.spriters-resource.com/playstation/simrpgtsu/sheet/38986/\n" +
            "Thanks to Butch for the HUD sprite: https://opengameart.org/users/buch\n" +
            "Thanks to Bonsaiheldin for the interior tile set and potion sprites: \n" +
            " - https://opengameart.org/content/interior-tileset-16x16\n" +
            " - https://opengameart.org/content/rpg-potions-16x16\n" +
            "Thanks to Joe Williamson roguelike item sprites: https://opengameart.org/content/roguelikerpg-items\n" +
            "Thanks to Gerald Burke for the key prompt sprites: https://opengameart.org/users/gerald-burke\n" +
            "Thanks to AntumDeluge for the chicken sprite, used under Creative Commons Zero: https://opengameart.org/content/chick\n" +
            "\n" +
            "Special thanks to friends and family for play testing\n" +
            "Special thanks to the TA's of CSCI1950N for debugging help and general guidance\n" +
            "\n" +
            "Licence Links:\n" +
            " - CC-BY-SA 3.0 licence https://creativecommons.org/licenses/by/3.0/\n" +
            " - GNU GPL 3.0 licence http://www.gnu.org/licenses/gpl-3.0.html\n" +
            " - GNU GPL 2.0 licence http://www.gnu.org/licenses/old-licenses/gpl-2.0.html\n" +
            "\n";
    creditsScreen.addUIElement(new UIText(new Vec2d(100,120), new Vec2d(400, 500),text, Color.BLACK, fontSmall));


    UIButton returnButton = new UIButton(new Vec2d(730,460), new Vec2d(130,50), colorMain, colorBorder);
    returnButton.setOnMouseClicked(() -> {
      this.setCurrentScreen("mainMenu");
    });
    returnButton.addChild(new UIText(new Vec2d(10,30), new Vec2d(400, 50),"Back", colorBorder, fontNormal));

    creditsScreen.addUIElement(returnButton);
    this.addScreen(creditsScreen, "creditsScreen");
  }

  public void createEndScreen(Screen endScreen, GameObject player){
    ValueComponent score = (ValueComponent)player.getComponent("ValueComponent");

    endScreen.addUIElement(new UIImage (new Image(FinalGame.getSpritePath("endBackground"))
            ,new Vec2d(0,0), this.originalStageSize));


    endScreen.addUIElement(new UIText(new Vec2d(35,70), new Vec2d(400, 50),"THANKS FOR PLAYING!",
            Color.rgb(255,245,196), fontLarge));

    endScreen.addUIElement(new UIText(new Vec2d(35,140), new Vec2d(400, 50),"" +
            "Final Score: ",
            Color.rgb(255,245,196), fontLarge));
    endScreen.addUIElement(new UIText(new Vec2d(305,142), new Vec2d(400, 50),"" +
            (int)score.value,
            Color.rgb(255,245,196), fontLarge));

    String text = "After defeating Slippy, our hero brought peace\nto the town and was rewarded handsomely.";
    endScreen.addUIElement(new UIText(new Vec2d(35,210), new Vec2d(400, 500),text, Color.rgb(255,245,196)
            , fontNormal));

    UIButton returnButton = new UIButton(new Vec2d(800,460), new Vec2d(130,50), colorMain, colorBorder);
    returnButton.setOnMouseClicked(() -> {
      this.setCurrentScreen("mainMenu");
    });
    returnButton.addChild(new UIText(new Vec2d(10,30), new Vec2d(400, 50),"Back", colorBorder, fontNormal));

    endScreen.addUIElement(returnButton);
    this.addScreen(endScreen, "endScreen");
  }

  private void createGameScreen(Screen gameScreen){
    GameWorld gameWorld = new GameWorld();

    gameScreen.addUIElement(new UIRect(new Vec2d(0,0),
            new Vec2d(this.currentStageSize.x,this.currentStageSize.y), Color.rgb(148, 100, 58)));


    UIViewport viewport = new UIViewport(new Vec2d(0,0),
            new Vec2d(this.originalStageSize.x, this.originalStageSize.y),
            gameWorld, new Vec2d(0,0), 50, true);
    gameScreen.addUIElement(viewport);

    finalGame = new FinalGame(gameWorld, viewport);
    finalGame.loadPlayer();

    /**
     * Other Game Screen UI below
     */
    //HUD
    gameScreen.addUIElement(new HUD(new Vec2d(8,6), new Vec2d(250, 250 * 32.0/100.0), finalGame));

    //SCORE
    UIRect scorebox = new UIRect(new Vec2d(830,6), new Vec2d(120, 40),
            Color.rgb(90,90,90,0.9));
    scorebox.addChild(new Score(new Vec2d(60,30), new Vec2d(120, 30),
            true, "0", Color.WHITESMOKE, fontSCORE, finalGame));
    gameScreen.addUIElement(scorebox);

    gameScreen.addUIElement(new GameOverCheck(new Vec2d(60,30), new Vec2d(120, 30),
            true, "0", Color.WHITESMOKE, fontSCORE, finalGame));

//

    this.addScreen(gameScreen, "gameScreen");

  }

  private static class HUD extends UIElement {

    private FinalGame finalGame;

    private Image HUD_image;
    private Image weapon_image;


    public HUD(Vec2d position, Vec2d size, FinalGame finalGame) {
      super(position, size);
      this.finalGame = finalGame;
      this.HUD_image = new Image(FinalGame.getSpritePath("HUD"));
      this.weapon_image = new Image(FinalGame.getSpritePath("roguelikeitems"));

    }

    @Override
    public void onDraw(GraphicsContext g) {
      Vec2d pos = this.getOffset();

      GameObject player = this.finalGame.getPlayer();

      g.save();
      g.setImageSmoothing(false);

      HealthComponent healthComponent = (HealthComponent)player.getComponent("HealthComponent");
      Player.PlayerComponent playerComponent = (Player.PlayerComponent)player.getComponent("PlayerComponent");
      int weapon = playerComponent.getCurrentWeapon();

      double crop_width = 51.0 * healthComponent.getHealthRatio();
      double image_ratio = 32 / 100.0; // y over x
      double W = this.size.x;
      double H = image_ratio * W;

      g.drawImage(this.HUD_image, 100, 0, crop_width, 8,
              pos.x + 37 / 100.0 * W, pos.y, crop_width / 100.0 * W, 8 / 32.0 * H);

      g.drawImage(this.HUD_image, 0, 0, 100, 32,
              pos.x, pos.y, W, H);

      Vec2d crop_start = new Vec2d(0,0);
      if(weapon == 0){
        crop_start = new Vec2d(16,112);
      } else {
        crop_start = new Vec2d(64,112);
      }
      g.drawImage(this.weapon_image, crop_start.x, crop_start.y, 16, 16,
              pos.x + 9/ 100.0 * W, pos.y + 9/ 100.0 * W, 14/100.0*W, 14/100.0*W);


      g.restore();
      super.onDraw(g);
    }
  }

  private static class Score extends UIText {

    private FinalGame finalGame;

    public Score(Vec2d position, Vec2d size, boolean centered, String text, Color textColor, Font font, FinalGame finalGame) {
      super(position, size, centered, text, textColor, font);
      this.finalGame = finalGame;
    }

    @Override
    public void onDraw(GraphicsContext g) {
      GameObject player = this.finalGame.getPlayer();
      ValueComponent score = (ValueComponent)player.getComponent("ValueComponent");
      this.text = "" + (int)score.value;
      super.onDraw(g);
    }
  }

  private class GameOverCheck extends UIText {
    private FinalGame finalGame;

    public GameOverCheck(Vec2d position, Vec2d size, boolean centered, String text, Color textColor, Font font, FinalGame finalGame) {
      super(position, size, centered, text, textColor, font);
      this.finalGame = finalGame;
    }

    @Override
    public void onDraw(GraphicsContext g) {
      GameObject player = this.finalGame.getPlayer();
      if (((BooleanComponent)player.getComponent("BooleanComponent")).getBool()) {
        ((BooleanComponent)player.getComponent("BooleanComponent")).setBool(false);
        Screen screen = new Screen();
        createEndScreen(screen, player);
        setCurrentScreen("endScreen");
        BackgroundMusic.playMenuBGM(this.finalGame.getPlayer().gameWorld);

        finalGame.resetGameWorld();

      }
    }
  }

}
