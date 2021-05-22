package projects.final_project;

import engine.Screen;
import engine.UIToolKit.UIViewport;
import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.Region;
import engine.game.components.HealthComponent;
import engine.game.components.ValueComponent;
import projects.final_project.assets.sounds.AnimationGraphComponent;
import engine.game.components.screenEffects.FadeInEffect;
import engine.game.tileSystem.TileMap;
import engine.game.systems.CollisionSystem;
import engine.support.Vec2d;
import projects.final_project.levels.*;
import projects.final_project.levels.tileMaps.*;

import java.io.File;

public class FinalGame {

    public static final int TILE_LAYER = CollisionSystem.CollisionMask.layer0;
    public static final int TILE_MASK = CollisionSystem.CollisionMask.NONE;

    public static final int PLAYER_LAYER = CollisionSystem.CollisionMask.layer3;
    public static final int PLAYER_MASK = CollisionSystem.CollisionMask.layer0 | CollisionSystem.CollisionMask.layer1;

    public static final int ENEMY_LAYER = CollisionSystem.CollisionMask.layer2 | CollisionSystem.CollisionMask.layer1;
    public static final int ENEMY_MASK = CollisionSystem.CollisionMask.layer0 | CollisionSystem.CollisionMask.layer2 | CollisionSystem.CollisionMask.layer1 | CollisionSystem.CollisionMask.layer3;

    public static final int OBJECT_LAYER = CollisionSystem.CollisionMask.layer1;
    public static final int OBJECT_MASK = CollisionSystem.CollisionMask.layer0 | CollisionSystem.CollisionMask.layer1 | CollisionSystem.CollisionMask.layer3;

    public static final int ATTACK_LAYER = CollisionSystem.CollisionMask.layer4;
    public static final int ATTACK_MASK = CollisionSystem.CollisionMask.layer4;

    public static final int TALK_LAYER = CollisionSystem.CollisionMask.layer5;
    public static final int TALK_MASK = CollisionSystem.CollisionMask.layer5;

    public GameWorld gameWorld;
    private UIViewport viewport;
    private GameObject player;

    public boolean hasStarted = false;

    public FinalGame(GameWorld gameWorld, UIViewport viewport) {
        this.gameWorld = gameWorld;
        this.viewport = viewport;


        gameWorld.linkViewport(0, this.viewport);
    }

    public void loadPlayer(){
        this.player = Player.createPlayer(this.gameWorld,new Vec2d(3,37));
    }

    public void init() {
        if(this.player == null) {
            this.player = Player.createPlayer(this.gameWorld, new Vec2d(3, 37));
        }
        this.gameWorld.addGameObject(player);

        TileMap caveTileMap = CaveTileMap.createTileMap();
        TileMap worldTileMap = WorldTileMap.createTileMap();


        //create cave Region
        Levels.cave = new Region();
        gameWorld.loadRegion(Levels.cave);
        Cave1.setCaveLevel(caveTileMap);
        caveTileMap.addTilesToGameWorld(this.gameWorld, 0, 2, TILE_LAYER, TILE_MASK);
        Cave1.addGameObjects(gameWorld);
        gameWorld.unloadRegion();

        //create area1 Region
        Levels.area1 = new Region();
        gameWorld.loadRegion(Levels.area1);
        Area1.setTiles(worldTileMap);
        worldTileMap.addTilesToGameWorld(this.gameWorld, 0, 2, TILE_LAYER, TILE_MASK);
        Area1.addGameObjects(gameWorld);
        gameWorld.unloadRegion();

        //create area2 Region
        Levels.area2 = new Region();
        gameWorld.loadRegion(Levels.area2);
        Area2.setTiles(worldTileMap);
        worldTileMap.addTilesToGameWorld(this.gameWorld, 0, 2, TILE_LAYER, TILE_MASK);
        Area2.addGameObjects(gameWorld);
        gameWorld.unloadRegion();

        //create house Region
        Levels.house = new Region();
        gameWorld.loadRegion(Levels.house);
        House1.addGameObjects(gameWorld);
        gameWorld.unloadRegion();

        //create area3 Region
        Levels.area3 = new Region();
        gameWorld.loadRegion(Levels.area3);
        Area3.setTiles(worldTileMap);
        worldTileMap.addTilesToGameWorld(this.gameWorld, 0, 2, TILE_LAYER, TILE_MASK);
        Area3.addGameObjects(gameWorld);
        gameWorld.unloadRegion();

        gameWorld.processQueues();

        gameWorld.loadRegion(Levels.area1);


        hasStarted = true;
    }

    /*
     * converts a string into the path to the corresponding png file.
     */
    public static String getSpritePath(String name){
        File folder = new File("file:.\\projects\\final_project\\assets\\");
        File sprite = new File(folder, name.concat(".png"));
        return sprite.toString();
    }

    public static void onDeath(GameObject player){
        BackgroundMusic.stopBGM(player.gameWorld);
        BackgroundMusic.playBGM1(player.gameWorld);

        player.getTransform().position = new Vec2d(9,32);
        player.gameWorld.loadRegion(Levels.area1);

        ((HealthComponent)player.getComponent("HealthComponent")).resetHealth();
        ((ValueComponent)player.getComponent("ValueComponent")).value -= 10;
        if(((ValueComponent)player.getComponent("ValueComponent")).value < 0){
            ((ValueComponent)player.getComponent("ValueComponent")).value = 0;
        }
        AnimationGraphComponent agc = (AnimationGraphComponent)player.getComponent("AnimationGraphComponent");
        agc.queueAnimation("idle",true);

        FadeInEffect fadein = new FadeInEffect(0, 3);
        fadein.linkEventCallback(FinalGame::startPlayer);
        player.addComponent(fadein);
    }

    public void resetGameWorld(){
        if(this.gameWorld == null) return; //nothing to reset
        BackgroundMusic.stopBGM(gameWorld);
        gameWorld.unloadRegion();
        gameWorld.clearAllGameObjects();
        hasStarted = false;
    }

    public static void startPlayer(GameObject player){
        Player.PlayerComponent playerComponent = (Player.PlayerComponent)player.getComponent("PlayerComponent");
        playerComponent.enable();
    }



    public GameObject getPlayer() {
        return player;
    }
}
