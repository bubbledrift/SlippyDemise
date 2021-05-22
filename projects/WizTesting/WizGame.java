package projects.WizTesting;

import engine.UIToolKit.UIViewport;
import engine.game.GameWorld;
import engine.game.SpriteLoader;

import java.io.File;

public class WizGame {


    private GameWorld gameWorld;
    private UIViewport viewport;

    private int difficulty = 0;

    private long seed = 0;

    public WizGame(GameWorld gameWorld, UIViewport viewport, long seed){
        this.gameWorld = gameWorld;
        this.viewport = viewport;
        this.seed = seed;

        gameWorld.linkViewport(0, viewport);
    }

    /*
     * converts a string into the path to the corresponding png file.
     */
    public static String getSpritePath(String name){
        File folder = new File("file:.\\projects\\wiz\\assets\\");
        File sprite = new File(folder, name.concat(".png"));
        return sprite.toString();
    }

    public void setSeed(long seed){
        seed = seed;
    }

    public UIViewport getViewport(){
        return viewport;
    }

    public void init(){
        WizLevelGenerator.resetLevel(this,this.gameWorld, seed, difficulty);
    }

    public void nextLevel(){
        this.difficulty++;
        this.seed+=10;
        WizLevelGenerator.resetLevel(this,this.gameWorld, seed, difficulty);
    }

    public void onDeath(){
        this.difficulty = 0;
        WizLevelGenerator.resetLevel(this,this.gameWorld, seed, difficulty);
    }


}
