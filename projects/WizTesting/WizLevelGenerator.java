package projects.WizTesting;

import engine.MapGeneration;
import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.AABShape;
import engine.game.components.*;
import engine.game.systems.CollisionSystem;
import engine.support.Vec2d;
import engine.support.Vec2i;
import javafx.scene.paint.Color;

public class WizLevelGenerator {

    private static final int TILE_LAYER = CollisionSystem.CollisionMask.layer0;
    private static final int TILE_MASK = CollisionSystem.CollisionMask.NONE;

    private static int[][] map;

    private static GameObject player;

    private static double calculateSpawnRate(int difficulty){
        if(difficulty == 0) return 10;
        return Math.max(5/difficulty,1);
    }

    public static void resetLevel(WizGame wizGame, GameWorld gameWorld, long seed, int difficulty){
        gameWorld.clearAllGameObjects();

        player = WizPlayer.createPlayer(wizGame, gameWorld, new Vec2d(0,0));

        MapGeneration.RoomDungeon dungeon = MapGeneration.generateDungeonMap(
                new Vec2i(30,30), new Vec2i(5,5), 3, seed);
        createTiles(gameWorld, dungeon.map);
        createFog(gameWorld, dungeon.map);

        //player = WizPlayer.createPlayer(wizGame, gameWorld, new Vec2d(0,0));
        player.getTransform().position = new Vec2d(2.0 * dungeon.start.x, 2.0 * dungeon.start.y);
        gameWorld.addGameObject(player);

        gameWorld.addGameObject(createSpawn(gameWorld, new Vec2d(2.0 * dungeon.start.x, 2.0 * dungeon.start.y)));
        gameWorld.addGameObject(createDoor(gameWorld, new Vec2d(2.0 * dungeon.end.x, 2.0 * dungeon.end.y), player));

        for(MapGeneration.Room room : dungeon.rooms) {

//            gameWorld.addGameObject(WizEnemies.createSpawner(gameWorld, calculateSpawnRate(difficulty),
//                    new Vec2d(2.0*room.position.x + room.size.x, 2.0*room.position.y + room.size.y), player));

            gameWorld.addGameObject(WizEnemies.createEnemy(gameWorld,
                    new Vec2d(2.0*room.position.x + room.size.x, 2.0*room.position.y + room.size.y), player, dungeon.map));

        }
    }

    public static void createTiles(GameWorld gameWorld, int[][] map){
        for(int i = 0; i< map.length;i++){
            for(int j = 0; j< map[0].length;j++){
                WizLevelGenerator.map = map;
                addTile(gameWorld, i, j, new Vec2d(i * 2, j * 2));
            }
        }
    }

    public static void createFog(GameWorld gameWorld, int[][] map){
        for(int i = 0; i< map.length;i++){
            for(int j = 0; j< map[0].length;j++){
                addFogTile(gameWorld, new Vec2d(i * 2, j * 2));
            }
        }
    }

    private static int getAbove(int[][] map, int i , int j){
        if(j > 0){
            return map[i][j-1];
        }
        return 1;
    }
    private static int getBelow(int[][] map, int i , int j){
        if(j < map[0].length-1){
            return map[i][j+1];
        }
        return 1;
    }
    private static int getRight(int[][] map, int i , int j){
        if(i < map.length-1){
            return map[i+1][j];
        }
        return 1;
    }
    private static int getLeft(int[][] map, int i , int j){
        if( i > 0){
            return map[i-1][j];
        }
        return 1;
    }

    private static void addFogTile(GameWorld gameWorld, Vec2d pos) {
        GameObject fog = new GameObject(gameWorld);
        fog.getTransform().position = pos;
        fog.getTransform().size = new Vec2d(2,2);

        LateRectComponent lateRectComponent = new LateRectComponent(Color.rgb(0,0,0,0.8));
        lateRectComponent.setGameObject(fog);
        fog.addComponent(lateRectComponent);

        ProximityComponent proximityComponent = new ProximityComponent(player, 10);
        proximityComponent.setGameObject(fog);
        proximityComponent.linkProximityCallback(WizLevelGenerator::fogBreakCallback);
        fog.addComponent(proximityComponent);

        gameWorld.addGameObject(fog);

        //every tick, set the fog's color(transparency) based on proximity to objects with
    }

    private static void addTile(GameWorld gameWorld, int i, int j, Vec2d pos){
        GameObject tile = new GameObject(gameWorld, 0);
        boolean is_low_tile = false;

        //based on surrounding tiles sets the sprites and collision boxes for each tile.
        //is_low_tile is used to flag the "stump" tile. It gets its own height and collision
        //map[i][j] == 0 means this tile is a grass tile and has a chance of being a stump
        //map[i][j] == 1 means this tile is a wall and the correct texture needs to be rendered.
        if(map[i][j] == 0) {
            if(Math.random() > .1) {
                tile.addComponent(new SpriteComponent(WizGame.getSpritePath("tiles"),
                        new Vec2d(0, 0), new Vec2d(2, 2), new Vec2d(34, 0), new Vec2d(32, 32)));
            } else {
                if(Math.random() < .5) {
                    tile.addComponent(new SpriteComponent(WizGame.getSpritePath("tiles"),
                            new Vec2d(0, 0), new Vec2d(2, 2), new Vec2d(34, 34), new Vec2d(32, 32)));
                    is_low_tile = true;
                 } else {
                    tile.addComponent(new SpriteComponent(WizGame.getSpritePath("tiles"),
                            new Vec2d(0, 0), new Vec2d(2, 2), new Vec2d(34, 68), new Vec2d(32, 32)));
                }
            }
        } else if(getAbove(map, i,j) == 0 && getBelow(map,i,j) == 0){
            tile.addComponent(new SpriteComponent(WizGame.getSpritePath("tiles"),
                    new Vec2d(0, 0), new Vec2d(2, 2), new Vec2d(68,68), new Vec2d(32, 32)));
            tile.addComponent(new CollisionComponent(new AABShape(new Vec2d(0, 0), new Vec2d(2, 4/3)),
                    true, true, TILE_LAYER, TILE_MASK));
        } else if(getRight(map, i,j) == 0 && getBelow(map,i,j) == 0){
            tile.addComponent(new SpriteComponent(WizGame.getSpritePath("tiles"),
                    new Vec2d(0, 0), new Vec2d(2, 2), new Vec2d(68,34), new Vec2d(32, 32)));
            tile.addComponent(new CollisionComponent(new AABShape(new Vec2d(0, 0), new Vec2d(2, 4/3)),
                    true, true, TILE_LAYER, TILE_MASK));
            ((SpriteComponent)tile.getComponent("SpriteComponent")).flipHorizontally();
        } else if(getLeft(map, i,j) == 0 && getBelow(map,i,j) == 0){
            tile.addComponent(new SpriteComponent(WizGame.getSpritePath("tiles"),
                    new Vec2d(0, 0), new Vec2d(2, 2), new Vec2d(68,34), new Vec2d(32, 32)));
            tile.addComponent(new CollisionComponent(new AABShape(new Vec2d(0, 0), new Vec2d(2, 4/3)),
                    true, true, TILE_LAYER, TILE_MASK));
        } else if(getAbove(map, i,j) == 0){
            tile.addComponent(new SpriteComponent(WizGame.getSpritePath("tiles"),
                    new Vec2d(0, 0), new Vec2d(2, 2), new Vec2d(0,68), new Vec2d(32, 32)));
            tile.addComponent(new CollisionComponent(new AABShape(new Vec2d(0, 0), new Vec2d(2, 2)),
                    true, true, TILE_LAYER, TILE_MASK));
        } else if(getBelow(map, i,j) == 0){
            tile.addComponent(new SpriteComponent(WizGame.getSpritePath("tiles"),
                    new Vec2d(0, 0), new Vec2d(2, 2), new Vec2d(68,0), new Vec2d(32, 32)));
            tile.addComponent(new CollisionComponent(new AABShape(new Vec2d(0, 0), new Vec2d(2, 2)),
                    true, true,TILE_LAYER, TILE_MASK));
        } else if(getRight(map, i,j) == 0){
            tile.addComponent(new SpriteComponent(WizGame.getSpritePath("tiles"),
                    new Vec2d(0, 0), new Vec2d(2, 2), new Vec2d(0,34), new Vec2d(32, 32)));
            tile.addComponent(new CollisionComponent(new AABShape(new Vec2d(0, 0), new Vec2d(2, 2)),
                    true, true, TILE_LAYER, TILE_MASK));
        } else if(getLeft(map, i,j) == 0){
            tile.addComponent(new SpriteComponent(WizGame.getSpritePath("tiles"),
                    new Vec2d(0, 0), new Vec2d(2, 2), new Vec2d(0,34), new Vec2d(32, 32)));
            tile.addComponent(new CollisionComponent(new AABShape(new Vec2d(0, 0), new Vec2d(2, 2)),
                    true, true, TILE_LAYER, TILE_MASK));
            ((SpriteComponent)tile.getComponent("SpriteComponent")).flipHorizontally();
        } else {
            tile.addComponent(new SpriteComponent(WizGame.getSpritePath("tiles"),
                    new Vec2d(0, 0), new Vec2d(2, 2), new Vec2d(0,0), new Vec2d(32, 32)));
            tile.addComponent(new CollisionComponent(new AABShape(new Vec2d(0, 0), new Vec2d(2, 2)),
                    true, true,TILE_LAYER, TILE_MASK));
        }


        if(map[i][j] == 1) {
            tile.addComponent(new IDComponent("wall"));
            tile.addComponent(new ValueComponent(10));
        } else {
            if(is_low_tile){
                map[i][j] = 1;
                tile.addComponent(new CollisionComponent(new AABShape(new Vec2d(.2, .2), new Vec2d(1.6, 1.6)),
                        true, true, TILE_LAYER, TILE_MASK));
                HealthComponent healthComponent = new HealthComponent(5);
                healthComponent.linkDeathCallback(WizLevelGenerator::tileBreakCallback);
                tile.addComponent(healthComponent);
                tile.addComponent(new ValueComponent(5));
                tile.addComponent(new IDComponent("wall"));
            } else {
                tile.addComponent(new ValueComponent(0));
                tile.addComponent(new IDComponent("grass"));
            }
        }

        tile.getTransform().position = pos;
        tile.getTransform().size = new Vec2d(2,2);
        gameWorld.addGameObject(tile);
    }

    private static void tileBreakCallback(GameObject tile){
        SpriteComponent sprite = (SpriteComponent)tile.getComponent("SpriteComponent");
        sprite.resetSprite(WizGame.getSpritePath("tiles"),
                new Vec2d(0, 0), new Vec2d(2, 2), new Vec2d(34, 68), new Vec2d(32, 32));
        ValueComponent height = (ValueComponent)tile.getComponent("HeightComponent");
        if(height != null) height.value = 0;
        CollisionComponent collision = (CollisionComponent)tile.getComponent("CollisionComponent");
        collision.setCollisionLayer(CollisionSystem.CollisionMask.NONE);
        collision.setCollisionMask(CollisionSystem.CollisionMask.NONE);
        //map[i][j] = 0;  //TODO need way to know which tile this is.
    }

    //Distance is less than 10.
    private static void fogBreakCallback(GameObject fogTile, double distance) {

        LateRectComponent fog = (LateRectComponent)fogTile.getComponent("LateRectComponent");
        //The shorter the distance, the smaller the opacity.

        if(distance < 6) {
            fog.setColor(Color.rgb(0,0,0,0));
        }
        else {
            //0.19 = .8/(10-6)
            fog.setColor(Color.rgb(0,0,0,(distance-6)*0.19));
        }

    }


    //spawn square for player
    private static GameObject createSpawn(GameWorld gameWorld, Vec2d pos){
        GameObject box = new GameObject(gameWorld, 1);
        box.addComponent(new SpriteComponent(WizGame.getSpritePath("spawn"),
                new Vec2d(0,0), new Vec2d(2,2)));
        box.getTransform().position = pos;
        box.getTransform().size = new Vec2d(2,2);
        return box;
    }

    //next level door for player
    private static GameObject createDoor(GameWorld gameWorld, Vec2d pos, GameObject player){
        GameObject door = new GameObject(gameWorld, 1);
        door.addComponent(new SpriteComponent(WizGame.getSpritePath("door"),
                new Vec2d(0,0), new Vec2d(2,2)));
        door.addComponent(new IDComponent("door"));
        door.addComponent(new CollisionComponent(new AABShape(new Vec2d(.1,.1),new Vec2d(1.8,1.8)),
                false, true, TILE_LAYER, TILE_MASK));
        door.addComponent(new AudioComponent("knock.wav", player, true));
        door.getTransform().position = pos;
        door.getTransform().size = new Vec2d(2,2);
        return door;
    }


}
