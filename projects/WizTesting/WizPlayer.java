package projects.WizTesting;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.AABShape;
import engine.game.collisionShapes.CircleShape;
import engine.game.components.*;
import engine.game.components.animation.SpriteAnimationComponent;
import engine.game.systems.CollisionSystem;
import engine.support.Vec2d;
import javafx.scene.input.KeyEvent;


public class WizPlayer {


    private static final int PROJECTILE_LAYER = CollisionSystem.CollisionMask.layer2;
    private static final int PLAYER_LAYER = CollisionSystem.CollisionMask.layer3;

    private static final int PROJECTILE_MASK = CollisionSystem.CollisionMask.layer0 | CollisionSystem.CollisionMask.layer1;
    private static final int PLAYER_MASK = CollisionSystem.CollisionMask.layer0 | CollisionSystem.CollisionMask.layer1;

    public static WizGame wizGame;

    //creates player
    public static GameObject createPlayer(WizGame wizGame, GameWorld gameWorld, Vec2d pos){
        WizPlayer.wizGame = wizGame;
        GameObject player = new GameObject(gameWorld, 3);
        player.addComponent(new WASDMovementComponent(10));
        player.addComponent(new CameraComponent(0, new Vec2d(0,0)));

        player.addComponent(new SpriteAnimationComponent(WizGame.getSpritePath("player"),
                new Vec2d(0,0), new Vec2d(2,2), 5, new Vec2d(0,0), new Vec2d(32,32), .1));//normal animation

        player.addComponent(new CollisionComponent(new AABShape(new Vec2d(.3,.25),new Vec2d(1.4,1.75)),
                false, true, PLAYER_LAYER, PLAYER_MASK));
        MouseClickComponent mouseClickComponent = new MouseClickComponent();
        mouseClickComponent.linkClickCallback( WizPlayer::clickCallback);
        player.addComponent(mouseClickComponent);

        KeyPressedComponent keyPressedComponent = new KeyPressedComponent();
        keyPressedComponent.linkKeyCallback( WizPlayer::keyCallback);
        player.addComponent(keyPressedComponent);

        player.getTransform().position = pos;
        player.getTransform().size = new Vec2d(1.4,1.75);
        return player;
    }

    private static void clickCallback(GameObject player, Vec2d e){
        Vec2d position = player.getTransform().position.plus(player.getTransform().size.smult(.5));
        Vec2d velocity = e.minus(position);
        GameObject o = createProjectile(player.gameWorld, position,velocity.normalize().smult(15));
        player.gameWorld.addGameObject(o);
    }

    private static void keyCallback(GameObject player, KeyEvent e){
        if(!e.getText().equals(" ")) return;
        Vec2d position = player.getTransform().position.plus(player.getTransform().size.smult(.5));
        Vec2d velocity = player.gameWorld.getMousePosition().minus(position);
        GameObject o = createBigProjectile(player.gameWorld, position,velocity.normalize().smult(5));
        player.gameWorld.addGameObject(o);
    }

    private static void PlayerCollisionCallback(CollisionSystem.CollisionInfo collisionInfo){
        GameObject player = collisionInfo.gameObjectSelf;
        IDComponent id = (IDComponent)collisionInfo.gameObjectOther.getComponent("IDComponent");
        if(id != null && id.getId().equals("door")){

            Vec2d door_loc = collisionInfo.gameObjectOther.getTransform().position;
            player.getTransform().position = new Vec2d(door_loc.x, door_loc.y);

            CollisionComponent collision = (CollisionComponent)player.getComponent("CollisionComponent");
            collision.setCollisionLayer(CollisionSystem.CollisionMask.NONE);
            collision.setCollisionMask(CollisionSystem.CollisionMask.NONE);

            WASDMovementComponent movement = (WASDMovementComponent)player.getComponent("WASDMovementComponent");
            movement.disable();

            SpriteAnimationComponent animation =
                    (SpriteAnimationComponent)player.getComponent("SpriteAnimationComponent");
            animation.resetAnimation( WizGame.getSpritePath("player"),
                    new Vec2d(0,0), new Vec2d(2,2), 5,
                    new Vec2d(0,64), new Vec2d(32,32), new Vec2d(32,32), .1); //door animation

            DelayEventComponent delayEventComponent = new DelayEventComponent(.5);
            delayEventComponent.linkEventCallback(WizPlayer::nextLevel);
            player.addComponent(delayEventComponent);

        }
        if(id != null && id.getId().equals("enemy")){
            CollisionComponent collision =
                    (CollisionComponent)player.getComponent("CollisionComponent");
            collision.setCollisionLayer(CollisionSystem.CollisionMask.NONE);
            collision.setCollisionMask(CollisionSystem.CollisionMask.NONE);

            WASDMovementComponent movement = (WASDMovementComponent)player.getComponent("WASDMovementComponent");
            movement.disable();

            SpriteAnimationComponent animation =
                    (SpriteAnimationComponent)player.getComponent("SpriteAnimationComponent");
            animation.resetAnimation( WizGame.getSpritePath("player"),
                    new Vec2d(0,0), new Vec2d(2,2), 5,
                    new Vec2d(0,32), new Vec2d(32,32), new Vec2d(32,32), .1); //death animation

            DelayEventComponent delayEventComponent = new DelayEventComponent(.5);
            delayEventComponent.linkEventCallback(WizPlayer::playerDeath);
            player.addComponent(delayEventComponent);

        }
    }

    private static void playerDeath(GameObject gameObject){
        wizGame.onDeath();
    }

    private static void nextLevel(GameObject gameObject){
        wizGame.nextLevel();
    }

    //pushable box for player
    private static GameObject createBox(GameWorld gameWorld, Vec2d pos){
        GameObject box = new GameObject(gameWorld, 4);
        box.addComponent(new SpriteComponent(WizGame.getSpritePath("block"),
                new Vec2d(0,0), new Vec2d(2,2)));

        box.addComponent(new CollisionComponent(new AABShape(new Vec2d(0,0),new Vec2d(2,2)),
                false, true, PLAYER_LAYER, PLAYER_MASK));
        box.getTransform().position = pos;
        box.getTransform().size = new Vec2d(2,2);
        return box;
    }

    //players low projectile
    private static GameObject createProjectile(GameWorld gameWorld, Vec2d position, Vec2d velocity){
        double projctile_height = 3;

        GameObject projectile = new GameObject(gameWorld, 2);
        projectile.addComponent(new SpriteComponent(WizGame.getSpritePath("projectile"),
                new Vec2d(0,0), new Vec2d(1,1)));

        CollisionComponent collisionComponent = new CollisionComponent(new CircleShape(new Vec2d(.5,.5),.3),
                false, true,PROJECTILE_LAYER, PROJECTILE_MASK){
                    @Override
                    public boolean caresAboutCollision(GameObject g){
                        ValueComponent height = (ValueComponent) g.getComponent("HeightComponent");
                        if(height == null) return true;
                        return projctile_height < height.value;
                    }
                };
        collisionComponent.linkCollisionCallback(WizPlayer::projectileCollisionCallback);
        projectile.addComponent(collisionComponent);

        projectile.addComponent(new ValueComponent(7));
        projectile.addComponent(new VelocityComponent(velocity));

        projectile.addComponent(new AudioComponent("laser.wav"));

        projectile.getTransform().position = position;
        projectile.getTransform().size = new Vec2d(2,2);
        return projectile;
    }

    private static void projectileCollisionCallback(CollisionSystem.CollisionInfo collisionInfo){
        IDComponent id = (IDComponent)collisionInfo.gameObjectOther.getComponent("IDComponent");
        if(id == null){
            return;
        }
        HealthComponent health = (HealthComponent)collisionInfo.gameObjectOther.getComponent("HealthComponent");
        if(health != null){
            health.hit(1);
        }
        collisionInfo.gameObjectSelf.gameWorld.removeGameObject(collisionInfo.gameObjectSelf);

    }


    //players big projectile
    private static GameObject createBigProjectile(GameWorld gameWorld, Vec2d position, Vec2d velocity){
        double projctile_height = 7;

        GameObject projectile = new GameObject(gameWorld, 2);
        projectile.addComponent(new SpriteComponent(WizGame.getSpritePath("projectile2"),
                new Vec2d(0,0), new Vec2d(1,1)));

        CollisionComponent collisionComponent = new CollisionComponent(new CircleShape(new Vec2d(.5,.5),.3),
                false, true, PROJECTILE_LAYER, PROJECTILE_MASK){
            @Override
            public boolean caresAboutCollision(GameObject g){
                ValueComponent height = (ValueComponent) g.getComponent("HeightComponent");
                if(height == null) return true;
                return projctile_height < height.value;
            }
        };
        collisionComponent.linkCollisionCallback(WizPlayer::bigProjectileCollisionCallback);
        projectile.addComponent(collisionComponent);
        projectile.addComponent(new ValueComponent(7));
        projectile.addComponent(new VelocityComponent(velocity));

        projectile.getTransform().position = position;
        projectile.getTransform().size = new Vec2d(2,2);
        return projectile;
    }

    private static void bigProjectileCollisionCallback(CollisionSystem.CollisionInfo collisionInfo){
        IDComponent id = (IDComponent)collisionInfo.gameObjectOther.getComponent("IDComponent");
        if(id == null){
            return;
        }
        HealthComponent health = (HealthComponent)collisionInfo.gameObjectOther.getComponent("HealthComponent");
        if(health != null){
            health.hit(10);
        }
        collisionInfo.gameObjectSelf.gameWorld.removeGameObject(collisionInfo.gameObjectSelf);
    }



}
