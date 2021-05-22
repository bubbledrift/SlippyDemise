package projects.final_project;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.AABShape;
import engine.game.collisionShapes.CircleShape;
import engine.game.components.CollisionComponent;
import engine.game.components.LightComponent;
import engine.game.components.SpriteComponent;
import engine.support.Vec2d;
import javafx.scene.paint.Color;
import projects.final_project.FinalGame;

public class NaturalElements {

    public static void placeTree(GameWorld gameWorld, int layer, Vec2d pos){
        GameObject tree = new GameObject(gameWorld, layer);

        SpriteComponent left_trunk = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                new Vec2d(-2,-1), new Vec2d(2,2), new Vec2d(0,13).smult(32), new Vec2d(32,32));
        SpriteComponent right_trunk = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                new Vec2d(0,-1), new Vec2d(2,2), new Vec2d(1,13).smult(32), new Vec2d(32,32));
        SpriteComponent bottom_left_folliage = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                new Vec2d(-2,-3), new Vec2d(2,2), new Vec2d(0,12).smult(32), new Vec2d(32,32));
        SpriteComponent bottom_right_folliage = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                new Vec2d(0,-3), new Vec2d(2,2), new Vec2d(1,12).smult(32), new Vec2d(32,32));
        SpriteComponent top_left_folliage = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                new Vec2d(-2,-5), new Vec2d(2,2), new Vec2d(2,12).smult(32), new Vec2d(32,32));
        SpriteComponent top_right_folliage = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                new Vec2d(0,-5), new Vec2d(2,2), new Vec2d(3,12).smult(32), new Vec2d(32,32));

        tree.addComponent(left_trunk);
        tree.addComponent(right_trunk);
        tree.addComponent(bottom_left_folliage);
        tree.addComponent(bottom_right_folliage);
        tree.addComponent(top_left_folliage);
        tree.addComponent(top_right_folliage);

        tree.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,.5), .6), true, true,
                FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));

        tree.getTransform().position = pos;

        gameWorld.addGameObject(tree);
    }

    /**
     * Places rocks into the game world
     * @param gameWorld Gameworld to add to
     * @param pos location in game world
     * @param size integer in {0, 1, 2, 3, 4}
     */
    public static void placeRocks(GameWorld gameWorld, int layer, Vec2d pos, int size){
        GameObject rocks = new GameObject(gameWorld, layer);

        if(size > 4 || size < 0) size = 0;

        SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                new Vec2d(-1,-1), new Vec2d(2,2), new Vec2d(4-size,14).smult(32), new Vec2d(32,32));
        rocks.addComponent(sprite);

        rocks.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,0), ((double)size)/6.0), true, true,
                FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));

        rocks.getTransform().position = pos;
        gameWorld.addGameObject(rocks);
    }

    /**
     * Places flowers into the game world
     * @param gameWorld Gameworld to add to
     * @param pos location in game world
     * @param type integer in 0 small patch, 1 large patch, 2 trunk
     */
    public static void placeWhiteFlowers(GameWorld gameWorld, int layer, Vec2d pos, int type){
        GameObject flowers = new GameObject(gameWorld, layer);

        if(type > 2 || type < 0) type = 0;
        if(type == 0) {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(2, 11).smult(32), new Vec2d(32, 32));
            flowers.addComponent(sprite);
        } else if(type == 1){
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(2, 10).smult(32), new Vec2d(32, 32));
            flowers.addComponent(sprite);
        } else{
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(0, 6).smult(32), new Vec2d(32, 32));
            flowers.addComponent(sprite);

            flowers.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,0), .5), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));
        }

        flowers.getTransform().position = pos;
        gameWorld.addGameObject(flowers);
    }

    /**
     * Places flowers into the game world
     * @param gameWorld Gameworld to add to
     * @param pos location in game world
     * @param type integer in 0 small patch, 1 large patch, 2 trunk
     */
    public static void placeRedFlowers(GameWorld gameWorld, int layer, Vec2d pos, int type){
        GameObject flowers = new GameObject(gameWorld, layer);

        if(type > 2 || type < 0) type = 0;
        if(type == 0) {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(1, 11).smult(32), new Vec2d(32, 32));
            flowers.addComponent(sprite);
        } else if(type == 1){
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(1, 10).smult(32), new Vec2d(32, 32));
            flowers.addComponent(sprite);
        } else{
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(0, 9).smult(32), new Vec2d(32, 32));
            flowers.addComponent(sprite);

            flowers.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,0), .5), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));
        }

        flowers.getTransform().position = pos;
        gameWorld.addGameObject(flowers);
    }

    /**
     * Places flowers into the game world
     * @param gameWorld Gameworld to add to
     * @param pos location in game world
     * @param type integer in 0 small patch, 1 large patch, 2 trunk
     */
    public static void placeYellowFlowers(GameWorld gameWorld, int layer, Vec2d pos, int type){
        GameObject flowers = new GameObject(gameWorld, layer);

        if(type > 2 || type < 0) type = 0;
        if(type == 0) {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(2, 9).smult(32), new Vec2d(32, 32));
            flowers.addComponent(sprite);
        } else if(type == 1){
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(2, 8).smult(32), new Vec2d(32, 32));
            flowers.addComponent(sprite);
        } else{
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(0, 7).smult(32), new Vec2d(32, 32));
            flowers.addComponent(sprite);

            flowers.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,0), .5), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));
        }

        flowers.getTransform().position = pos;
        gameWorld.addGameObject(flowers);
    }

    /**
     * Places flowers into the game world
     * @param gameWorld Gameworld to add to
     * @param pos location in game world
     * @param type integer in 0 small patch, 1 large patch, 2 trunk
     */
    public static void placePinkFlowers(GameWorld gameWorld, int layer, Vec2d pos, int type){
        GameObject flowers = new GameObject(gameWorld, layer);

        if(type > 2 || type < 0) type = 0;
        if(type == 0) {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(1, 9).smult(32), new Vec2d(32, 32));
            flowers.addComponent(sprite);
        } else if(type == 1){
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(1, 8).smult(32), new Vec2d(32, 32));
            flowers.addComponent(sprite);
        } else{
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(0, 8).smult(32), new Vec2d(32, 32));
            flowers.addComponent(sprite);

            flowers.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,0), .5), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));
        }

        flowers.getTransform().position = pos;
        gameWorld.addGameObject(flowers);
    }

    /**
     * Places rock flowers into the game world
     * @param gameWorld Gameworld to add to
     * @param pos location in game world
     * @param color integer: 0 is white, 1 is yellow
     */
    public static void placeRockFlowers(GameWorld gameWorld, int layer, Vec2d pos, int color){
        GameObject rockFlowers = new GameObject(gameWorld, layer);

        if(color == 0) {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(2, 6).smult(32), new Vec2d(32, 32));
            rockFlowers.addComponent(sprite);

            rockFlowers.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,0), .5), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));
        } else {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(2, 7).smult(32), new Vec2d(32, 32));
            rockFlowers.addComponent(sprite);

            rockFlowers.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,0), .5), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));
        }

        rockFlowers.getTransform().position = pos;
        gameWorld.addGameObject(rockFlowers);
    }

    /**
     * Places cactus into the game world
     * @param gameWorld Gameworld to add to
     * @param pos location in game world
     * @param size 0 is plain, 1 is right arm, 2 is left arm, 3 is full size.
     */
    public static void placeCactus(GameWorld gameWorld, int layer, Vec2d pos, int size){
        GameObject cactus = new GameObject(gameWorld, layer);

        if(size == 0) {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(0, 10).smult(32), new Vec2d(32, 32));
            cactus.addComponent(sprite);
            cactus.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,.5), .2), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));


        } else if(size == 1) {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(3, 10).smult(32), new Vec2d(32, 32));
            cactus.addComponent(sprite);
            cactus.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,.5), .2), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));


        } else if(size == 2) {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(5, 10).smult(32), new Vec2d(32, 32));
            cactus.addComponent(sprite);

            cactus.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,.5), .2), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));
        } else {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(0, 11).smult(32), new Vec2d(32, 32));
            cactus.addComponent(sprite);

            cactus.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,.5), .2), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));
        }

        cactus.getTransform().position = pos;
        gameWorld.addGameObject(cactus);
    }

    /**
     * Places stump into the game world
     * @param gameWorld Gameworld to add to
     * @param pos location in game world
     */
    public static void placeStump(GameWorld gameWorld, int layer, Vec2d pos){
        GameObject stump = new GameObject(gameWorld, layer);

        SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                new Vec2d(-1,-1), new Vec2d(2, 2), new Vec2d(1, 7).smult(32), new Vec2d(32, 32));
        stump.addComponent(sprite);
        stump.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,0), .5), true, true,
                FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));

        stump.getTransform().position = pos;
        gameWorld.addGameObject(stump);
    }

    /**
     * Places bush into the game world
     * @param gameWorld Gameworld to add to
     * @param pos location in game world
     * @param size 0 is small, 1 is medium, 2 is large, 3 is double.
     */
    public static void placeBush(GameWorld gameWorld, int layer, Vec2d pos, int size){
        GameObject bush = new GameObject(gameWorld, layer);

        if(size == 0) {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-2), new Vec2d(2, 2), new Vec2d(4, 12).smult(32), new Vec2d(32, 32));
            bush.addComponent(sprite);
            bush.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,-.5), .1), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));
        } else if(size == 1) {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-2), new Vec2d(2, 2), new Vec2d(4, 11).smult(32), new Vec2d(32, 32));
            bush.addComponent(sprite);
            bush.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,-.5), .2), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));
        } else if(size == 2) {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-2), new Vec2d(2, 2), new Vec2d(3, 11).smult(32), new Vec2d(32, 32));
            bush.addComponent(sprite);

            bush.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,-.5), .5), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));
        } else {
            SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("tile_sprite_sheet"),
                    new Vec2d(-1,-2), new Vec2d(2, 2), new Vec2d(4, 13).smult(32), new Vec2d(32, 32));
            bush.addComponent(sprite);

            bush.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,-.5), .3), true, true,
                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));
        }

        bush.getTransform().position = pos;
        gameWorld.addGameObject(bush);
    }

    public static void placeChicken(GameWorld gameWorld, int layer, Vec2d pos){
        GameObject chicken = new GameObject(gameWorld, layer);


        SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("chicken"),
                new Vec2d(-.5,-1), new Vec2d(1, 1), new Vec2d(48, 0), new Vec2d(24, 24));
        chicken.addComponent(sprite);
//        chicken.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,-.5), .1), true, true,
//                    FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK));

        chicken.getTransform().position = pos;
        gameWorld.addGameObject(chicken);
    }
}
