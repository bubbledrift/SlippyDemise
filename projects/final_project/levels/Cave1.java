package projects.final_project.levels;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.AABShape;
import engine.game.components.*;
import engine.game.components.screenEffects.FadeInEffect;
import engine.game.components.screenEffects.FadeOutEffect;
import engine.game.systems.CollisionSystem;
import engine.game.tileSystem.TileMap;
import engine.support.Vec2d;
import projects.final_project.BackgroundMusic;
import projects.final_project.FinalGame;
import projects.final_project.MiscElements;
import projects.final_project.Player;
import projects.final_project.characters.Goomba;
import projects.final_project.characters.Skeleton;

public class Cave1 {

    public static void setCaveLevel(TileMap tileMap){
        int[][] tiles_int = new int[][]{
                {1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1},
                {1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1},
                {1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1},
                {1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1},
                {1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1},
                {1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1},
                {1, 1, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        };

        String[] index = new String[]{"cave", "wall", "exit"};
        String[][] tiles = new String[tiles_int.length][tiles_int[0].length];
        for(int i = 0; i < tiles_int.length; i++){
            for(int j = 0; j < tiles_int[0].length; j++){
                tiles[i][j] = index[tiles_int[i][j]];
            }
        }

        tileMap.setTiles(tiles);
        tileMap.setExteriorTile("wall",0);
    }

    public static void addGameObjects(GameWorld gameWorld){

        MiscElements.placeBarrel(gameWorld, 1, new Vec2d(19.6, 12),(int)(Math.random()*7.0));
        MiscElements.placeBarrel(gameWorld, 1, new Vec2d(19.1, 12),(int)(Math.random()*7.0));
        MiscElements.placeBarrel(gameWorld, 1, new Vec2d(19.6, 13),(int)(Math.random()*7.0));
        MiscElements.placeBarrel(gameWorld, 1, new Vec2d(7.6, 2),(int)(Math.random()*7.0));
        MiscElements.placeBarrel(gameWorld, 1, new Vec2d(5, 27.6),(int)(Math.random()*7.0));
        MiscElements.placeBarrel(gameWorld, 1, new Vec2d(9, 20.6),(int)(Math.random()*7.0));
        MiscElements.placeBarrel(gameWorld, 1, new Vec2d(55, 10.6),(int)(Math.random()*7.0));
        MiscElements.placeBarrel(gameWorld, 1, new Vec2d(50, 27.6),(int)(Math.random()*7.0));
        MiscElements.placeBarrel(gameWorld, 1, new Vec2d(50, 26.6),(int)(Math.random()*7.0));
        MiscElements.placeBarrel(gameWorld, 1, new Vec2d(50.5, 27.6),(int)(Math.random()*7.0));
        MiscElements.placeBarrel(gameWorld, 1, new Vec2d(15, 2.6),(int)(Math.random()*30.0));

        MiscElements.placeTorch(gameWorld, 0, new Vec2d(8,13));
        MiscElements.placeTorch(gameWorld, 0, new Vec2d(6,21));
        MiscElements.placeTorch(gameWorld, 0, new Vec2d(14,25));
        MiscElements.placeTorch(gameWorld, 0, new Vec2d(50,15));
        MiscElements.placeTorch(gameWorld, 0, new Vec2d(32,9));
        MiscElements.placeTorch(gameWorld, 0, new Vec2d(29,14));
        MiscElements.placeTorch(gameWorld, 0, new Vec2d(20,3));


        Goomba.placeGoomba(gameWorld, new Vec2d(20, 5));
        Goomba.placeGoomba(gameWorld, new Vec2d(25, 10));
        Goomba.placeGoomba(gameWorld, new Vec2d(43, 15));
        Skeleton.placeSkeleton(gameWorld, new Vec2d(53, 25));
        Skeleton.placeSkeleton(gameWorld, new Vec2d(35, 25));
        Skeleton.placeSkeleton(gameWorld, new Vec2d(11, 25));

        placeWarpToArea2(gameWorld);
        placeWarpToArea3(gameWorld);
    }

    public static void placeWarpToArea2(GameWorld gameWorld){
        GameObject warp = new GameObject(gameWorld, 0);
        CollisionComponent collisionComponent = new CollisionComponent(new AABShape(new Vec2d(0,0), new Vec2d(2,.5)),
                true, false, CollisionSystem.CollisionMask.NONE, FinalGame.PLAYER_LAYER);// only cares about player collision
        collisionComponent.linkCollisionCallback(Cave1::FadeOutCave1_Area2);
        warp.addComponent(collisionComponent);
        warp.getTransform().position = new Vec2d(4,0);
        gameWorld.addGameObject(warp);
    }


    public static void LoadArea2(GameObject gameObject){
        gameObject.addComponent(new FadeInEffect(0, 1));
        gameObject.gameWorld.unloadRegion();
        gameObject.gameWorld.loadRegion(Levels.area2);
        gameObject.getTransform().position = new Vec2d(32,8.5);
        DrawFogComponent fog = (DrawFogComponent)gameObject.getComponent("DrawFogComponent");
        fog.disable();
        CameraComponent camera = (CameraComponent)gameObject.getComponent("CameraComponent");
        camera.setHorizontalRange(new Vec2d(0,40));
        camera.setVerticalRange(new Vec2d(0,40));
        BackgroundMusic.stopBGM(gameObject.gameWorld);
        BackgroundMusic.playBGM1(gameObject.gameWorld);
        Player.isBetweenAreas = false;
    }

    public static void FadeOutCave1_Area2(CollisionSystem.CollisionInfo collisionInfo){
        if(!Player.isBetweenAreas) {
            Player.isBetweenAreas = true;
            FadeOutEffect fadeout = new FadeOutEffect(0, .5);
            fadeout.linkEventCallback(Cave1::LoadArea2);
            collisionInfo.gameObjectOther.addComponent(fadeout);
        }
    }

    public static void placeWarpToArea3(GameWorld gameWorld){
        GameObject warp = new GameObject(gameWorld, 0);
        CollisionComponent collisionComponent = new CollisionComponent(new AABShape(new Vec2d(0,0), new Vec2d(2,.5)),
                true, false, CollisionSystem.CollisionMask.NONE, FinalGame.PLAYER_LAYER);// only cares about player collision
        collisionComponent.linkCollisionCallback(Cave1::FadeOutCave1_Area3);
        warp.addComponent(collisionComponent);
        warp.getTransform().position = new Vec2d(54,0);
        gameWorld.addGameObject(warp);
    }


    public static void LoadArea3(GameObject gameObject){
        gameObject.addComponent(new FadeInEffect(0, 1));
        gameObject.gameWorld.unloadRegion();
        gameObject.gameWorld.loadRegion(Levels.area3);
        gameObject.getTransform().position = new Vec2d(5,35);
        DrawFogComponent fog = (DrawFogComponent)gameObject.getComponent("DrawFogComponent");
        fog.disable();
        CameraComponent camera = (CameraComponent)gameObject.getComponent("CameraComponent");
        camera.setHorizontalRange(new Vec2d(0,40));
        camera.setVerticalRange(new Vec2d(0,40));
        BackgroundMusic.stopBGM(gameObject.gameWorld);
        BackgroundMusic.playBGM1(gameObject.gameWorld);
        Player.isBetweenAreas = false;
    }

    public static void FadeOutCave1_Area3(CollisionSystem.CollisionInfo collisionInfo){
        if(!Player.isBetweenAreas) {
            Player.isBetweenAreas = true;
            FadeOutEffect fadeout = new FadeOutEffect(0, .5);
            fadeout.linkEventCallback(Cave1::LoadArea3);
            collisionInfo.gameObjectOther.addComponent(fadeout);
        }
    }
}
