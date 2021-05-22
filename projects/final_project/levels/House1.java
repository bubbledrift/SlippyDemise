package projects.final_project.levels;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.AABShape;
import engine.game.components.*;
import engine.game.components.screenEffects.FadeInEffect;
import engine.game.components.screenEffects.FadeOutEffect;
import engine.game.systems.CollisionSystem;
import engine.support.Vec2d;
import javafx.scene.paint.Color;
import projects.final_project.FinalGame;
import projects.final_project.MiscElements;
import projects.final_project.Player;

public class House1 {



    public static void addGameObjects(GameWorld gameWorld){
        placeHouseInterior(gameWorld, 0);

        placeWarpToArea2(gameWorld);
    }

    public static void placeHouseInterior(GameWorld gameWorld, int layer){
        GameObject house = new GameObject(gameWorld, layer);

        SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("house"), new Vec2d(0,0), new Vec2d(20, 16));
        house.addComponent(sprite);

        CollisionComponent wallTop = new CollisionComponent(new AABShape(new Vec2d(0,0), new Vec2d(20,6)),
                true, true, FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK);
        CollisionComponent wallLeft = new CollisionComponent(new AABShape(new Vec2d(0,0), new Vec2d(2,16)),
                true, true, FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK);
        CollisionComponent wallRight = new CollisionComponent(new AABShape(new Vec2d(14,0), new Vec2d(6,16)),
                true, true, FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK);
        CollisionComponent wallBottom = new CollisionComponent(new AABShape(new Vec2d(0,15), new Vec2d(20,1)),
                true, true, FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK);
        house.addComponent(wallTop);
        house.addComponent(wallLeft);
        house.addComponent(wallRight);
        house.addComponent(wallBottom);

        CollisionComponent counter = new CollisionComponent(new AABShape(new Vec2d(2,10), new Vec2d(3,2)),
                true, true, FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK);
        CollisionComponent chest = new CollisionComponent(new AABShape(new Vec2d(6.5,6), new Vec2d(1.5,1.5)),
                true, true, FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK);
        CollisionComponent shelf = new CollisionComponent(new AABShape(new Vec2d(8,5), new Vec2d(1,3)),
                true, true, FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK);
        CollisionComponent bed = new CollisionComponent(new AABShape(new Vec2d(10,6), new Vec2d(3,4)),
                true, true, FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK);
        CollisionComponent table = new CollisionComponent(new AABShape(new Vec2d(8.75,11), new Vec2d(3,2.5)),
                true, true, FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK);
        CollisionComponent chairLeft = new CollisionComponent(new AABShape(new Vec2d(7.5,12), new Vec2d(1,1)),
                true, true, FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK);
        CollisionComponent chairRight = new CollisionComponent(new AABShape(new Vec2d(11.9,12), new Vec2d(1,1)),
                true, true, FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK);
        CollisionComponent chairBottom = new CollisionComponent(new AABShape(new Vec2d(9.75,13.75), new Vec2d(1,1)),
                true, true, FinalGame.OBJECT_LAYER, FinalGame.OBJECT_MASK);

        house.addComponent(counter);
        house.addComponent(chest);
        house.addComponent(shelf);
        house.addComponent(bed);
        house.addComponent(table);
        house.addComponent(chairLeft);
        house.addComponent(chairRight);
        house.addComponent(chairBottom);

        LightComponent tableCandle = new LightComponent(1, 4, new Vec2d(9.5,4));
        LightComponent shelfCandle = new LightComponent(1, 4, new Vec2d(10.2,11.9));

        house.addComponent(tableCandle);
        house.addComponent(shelfCandle);

        MiscElements.placeBarrel(gameWorld, 1, new Vec2d(13.5,6.5), 50);

        gameWorld.addGameObject(house);
    }

    public static void placeWarpToArea2(GameWorld gameWorld){
        GameObject warp = new GameObject(gameWorld, 0);
        CollisionComponent collisionComponent = new CollisionComponent(new AABShape(new Vec2d(0,0), new Vec2d(2,.5)),
                true, false, CollisionSystem.CollisionMask.NONE, FinalGame.PLAYER_LAYER);// only cares about player collision
        collisionComponent.linkCollisionCallback(House1::FadeOutHouse_Area2);
        warp.addComponent(collisionComponent);
        warp.getTransform().position = new Vec2d(3,6);

        gameWorld.addGameObject(warp);
    }


    public static void LoadArea2(GameObject gameObject){
        gameObject.addComponent(new FadeInEffect(0, 1));
        gameObject.gameWorld.unloadRegion();
        gameObject.gameWorld.loadRegion(Levels.area2);
        gameObject.getTransform().position = new Vec2d(11.5,10.5);
        DrawFogComponent fog = (DrawFogComponent)gameObject.getComponent("DrawFogComponent");
        fog.disable();
        CameraComponent camera = (CameraComponent)gameObject.getComponent("CameraComponent");
        camera.setHorizontalRange(new Vec2d(0,40));
        camera.setVerticalRange(new Vec2d(0,40));
        Player.isBetweenAreas = false;
    }

    public static void FadeOutHouse_Area2(CollisionSystem.CollisionInfo collisionInfo){
        if(!Player.isBetweenAreas) {
            Player.isBetweenAreas = true;
            FadeOutEffect fadeout = new FadeOutEffect(0, .5);
            fadeout.linkEventCallback(House1::LoadArea2);
            collisionInfo.gameObjectOther.addComponent(fadeout);
        }
    }
}
