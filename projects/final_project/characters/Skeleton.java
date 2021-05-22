package projects.final_project.characters;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.AABShape;
import engine.game.collisionShapes.CircleShape;
import engine.game.components.*;
import engine.game.components.animation.*;
import engine.game.components.animation.animationGraph.*;
import engine.game.systems.CollisionSystem;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import javafx.scene.paint.Color;
import projects.final_project.FinalGame;
import projects.final_project.HealthBarComponent;
import projects.final_project.MiscElements;
import projects.final_project.Player;
import projects.final_project.assets.sounds.AnimationGraphComponent;

import java.util.concurrent.ThreadLocalRandom;

public class Skeleton {

    private static final Vec2d SKELETON_SIZE = new Vec2d(2,2);

    public static void placeSkeleton(GameWorld gameWorld, Vec2d pos){
        GameObject enemy = new GameObject(gameWorld, 1);

        AnimationGraphComponent agc = getSkeletonAnimationGraph();
        enemy.addComponent(agc);
        enemy.addComponent(new SkeletonMovementComponent(2, agc));


        enemy.addComponent(new CollisionComponent(new CircleShape(new Vec2d(0,-.5),.3),
                false, true, FinalGame.ENEMY_LAYER, FinalGame.ENEMY_MASK));

        CollisionComponent hitCollisionComponent = new CollisionComponent(new CircleShape(new Vec2d(0,-.5),.3),
                false, false, CollisionSystem.CollisionMask.NONE, FinalGame.ATTACK_MASK);
        hitCollisionComponent.linkCollisionCallback(Skeleton::onHitCallback);
        enemy.addComponent(hitCollisionComponent);

        CollisionComponent nearPlayer = new CollisionComponent(new CircleShape(new Vec2d(0,0),8),
                false, false, CollisionSystem.CollisionMask.NONE, FinalGame.OBJECT_MASK);
        nearPlayer.linkCollisionCallback(Skeleton::skeletonNearPlayer);
        enemy.addComponent(nearPlayer);


        HealthComponent healthComponent = new HealthComponent(5);
        healthComponent.linkDeathCallback(Skeleton::enemyDeathCallback);
        enemy.addComponent(healthComponent);

        enemy.addComponent(new AudioComponent("skeleton.wav"));

        enemy.addComponent(new HealthBarComponent(Color.RED, new Vec2d(0,-1.75), new Vec2d(1,.1), healthComponent, true));

        enemy.addComponent(new IDComponent("skeleton"));

        enemy.getTransform().position = pos;
        enemy.getTransform().size = new Vec2d(2,2);
        gameWorld.addGameObject(enemy);
    }

    public static void onHitCallback(CollisionSystem.CollisionInfo collisionInfo){
        if(collisionInfo.gameObjectSelf.getComponent("ShakeComponent") == null) {
            collisionInfo.gameObjectSelf.addComponent(new ShakeComponent(.1, .1));
            ((AudioComponent)(collisionInfo.gameObjectSelf.getComponent("AudioComponent"))).start();
            Player.PlayerComponent playerComponent = (Player.PlayerComponent)collisionInfo.gameObjectOther.getComponent("PlayerComponent");
            HealthComponent health = (HealthComponent)collisionInfo.gameObjectSelf.getComponent("HealthComponent");
            if(health != null && playerComponent != null){
                health.hit(playerComponent.getAttack());
            }
        }
    }

    private static void enemyDeathCallback(GameObject enemy){
        CollisionComponent collision = (CollisionComponent)enemy.getComponent("CollisionComponent");
        collision.disable();

        Vec2d pos = enemy.getTransform().position;
        for(int i = 0; i < 5; i++) {
            MiscElements.placeCoin(enemy.gameWorld, 2, new Vec2d(pos.x, pos.y),
                    new Vec2d(Math.random() * 2 - 1, Math.random() * 2 - 1).normalize().smult(2));
        }
        //2/3 chance to drop a potion
        int potion = ThreadLocalRandom.current().nextInt(0, 3);
        if(potion != 2) {
            MiscElements.placePotion(enemy.gameWorld, 1, new Vec2d(pos.x, pos.y),
                    new Vec2d(Math.random() * 2 - 1, Math.random() * 2 - 1).normalize().smult(Math.random()*3));
        }
        DelayEventComponent delayEventComponent = new DelayEventComponent(.1);
        delayEventComponent.linkEventCallback(Skeleton::enemyRemoveCallback);
        enemy.addComponent(delayEventComponent);


    }

    private static void enemyRemoveCallback(GameObject gameObject){
        gameObject.gameWorld.removeGameObject(gameObject);
    }

    private static AnimationGraphComponent getSkeletonAnimationGraph(){
        Vec2d spriteOffset = new Vec2d(-1,-2);
        AnimationComponent idle_up = new SpriteAnimationComponent(FinalGame.getSpritePath("skeleton"),
                spriteOffset, SKELETON_SIZE, 1, new Vec2d(0,8*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent idle_left = new SpriteAnimationComponent(FinalGame.getSpritePath("skeleton"),
                spriteOffset, SKELETON_SIZE, 1, new Vec2d(0,9*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent idle_down = new SpriteAnimationComponent(FinalGame.getSpritePath("skeleton"),
                spriteOffset, SKELETON_SIZE, 1, new Vec2d(0,10*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent idle_right = new SpriteAnimationComponent(FinalGame.getSpritePath("skeleton"),
                spriteOffset, SKELETON_SIZE, 1, new Vec2d(0,11*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AGNode N_idle_up = new AGAnimation("idle_up", idle_up);
        AGNode N_idle_left = new AGAnimation("idle_left", idle_left);
        AGNode N_idle_down = new AGAnimation("idle_down", idle_down);
        AGNode N_idle_right = new AGAnimation("idle_right", idle_right);

        AnimationComponent walk_up = new SpriteAnimationComponent(FinalGame.getSpritePath("skeleton"),
                spriteOffset, SKELETON_SIZE, 9, new Vec2d(0,8*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent walk_left = new SpriteAnimationComponent(FinalGame.getSpritePath("skeleton"),
                spriteOffset, SKELETON_SIZE, 9, new Vec2d(0,9*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent walk_down = new SpriteAnimationComponent(FinalGame.getSpritePath("skeleton"),
                spriteOffset, SKELETON_SIZE, 9, new Vec2d(0,10*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent walk_right = new SpriteAnimationComponent(FinalGame.getSpritePath("skeleton"),
                spriteOffset, SKELETON_SIZE, 9, new Vec2d(0,11*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AGNode N_walk_up = new AGAnimation("walk_up", walk_up);
        AGNode N_walk_left = new AGAnimation("walk_left", walk_left);
        AGNode N_walk_down = new AGAnimation("walk_down", walk_down);
        AGNode N_walk_right = new AGAnimation("walk_right", walk_right);

        AnimationComponent shoot_up = new SpriteAnimationComponent(FinalGame.getSpritePath("skeleton"),
                spriteOffset, SKELETON_SIZE, 12, new Vec2d(0,16*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent shoot_left = new SpriteAnimationComponent(FinalGame.getSpritePath("skeleton"),
                spriteOffset, SKELETON_SIZE, 12, new Vec2d(0,17*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent shoot_down = new SpriteAnimationComponent(FinalGame.getSpritePath("skeleton"),
                spriteOffset, SKELETON_SIZE, 12, new Vec2d(0,18*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent shoot_right = new SpriteAnimationComponent(FinalGame.getSpritePath("skeleton"),
                spriteOffset, SKELETON_SIZE, 12, new Vec2d(0,19*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AGNode N_shoot_up = new AGAnimation("shoot_up", shoot_up);
        AGNode N_shoot_left = new AGAnimation("shoot_left", shoot_left);
        AGNode N_shoot_down = new AGAnimation("shoot_down", shoot_down);
        AGNode N_shoot_right = new AGAnimation("shoot_right", shoot_right);

        AGAnimationGroup idle = new AGAnimationGroup("idle",
                new AGNode[]{N_idle_up, N_idle_left, N_idle_down, N_idle_right},
                new Vec2d[]{new Vec2d(0,-1), new Vec2d(-1,0), new Vec2d(0,1), new Vec2d(1,0)});
        idle.setInterruptible(true);

        AGAnimationGroup walk = new AGAnimationGroup("walk",
                new AGNode[]{N_walk_up, N_walk_left, N_walk_down, N_walk_right},
                new Vec2d[]{new Vec2d(0,-1), new Vec2d(-1,0), new Vec2d(0,1), new Vec2d(1,0)});
        walk.setInterruptible(true);

        AGAnimationGroup shoot = new AGAnimationGroup("shoot", "idle",
                new AGNode[]{N_shoot_up, N_shoot_left, N_shoot_down, N_shoot_right},
                new Vec2d[]{new Vec2d(0,-1), new Vec2d(-1,0), new Vec2d(0,1), new Vec2d(1,0)});
        shoot.setInterruptible(false);

        AGNode[] animationNodes = new AGNode[]{idle, walk, shoot};
        AnimationGraphComponent agc = new AnimationGraphComponent(animationNodes);
        return agc;
    }

    private static class SkeletonMovementComponent extends Component{

        private Vec2d direction = new Vec2d(0,0);
        private double speed = 1.5;
        private double time = 2;

        private double margin = .5;

        private String state = "standing"; //standing, idle, follow, retreat, shoot

        public GameObject player;

        private AnimationGraphComponent animationGraphComponent;

        public SkeletonMovementComponent(double speed, AnimationGraphComponent animationGraphComponent) {
            super();
            this.speed = speed;
            this.animationGraphComponent = animationGraphComponent;
        }


        //Every couple seconds, choose a new direction to go and walk in that direction
        @Override
        public void onTick(long nanosSincePreviousTick){
            double dx = 0;
            double dy = 0;
            double dt = nanosSincePreviousTick/1000000000.0; //seconds since last tick

            time -= dt;

            Vec2d pos = this.gameObject.getTransform().position;

            //being hit
            if(this.gameObject.getComponent("ShakeComponent") != null) return;
            if(this.state.equals("standing")){
                if(time <= 0) {
                    this.state = "idle";
                }
                this.animationGraphComponent.queueAnimation("idle");
            } else if(this.state.equals("idle")){
                if(time <= 0) {//Randomly pick a new direction every 2 seconds.
                    int pickDirection = ThreadLocalRandom.current().nextInt(0, 6);
                    if(pickDirection == 0) direction = new Vec2d(0,1);
                    else if(pickDirection == 1) direction = new Vec2d(0,-1);
                    else if(pickDirection == 2) direction = new Vec2d(-1,0);
                    else if(pickDirection == 3) direction = new Vec2d(1,0);
                    else direction = new Vec2d(0,0);
                    time = ThreadLocalRandom.current().nextInt(1, 3);
                }

                if(direction.x == 0 && direction.y == 0) {
                    this.animationGraphComponent.queueAnimation("idle");
                } else {
                    this.animationGraphComponent.queueAnimation("walk");
                }
                dx -= direction.x * dt * speed;
                dy -= direction.y * dt * speed;

            } else if(this.state.equals("follow")){
                Vec2d delta = this.player.getTransform().position.minus(pos);
                if(delta.mag() < 4){
                    this.state = "retreat";
                    time = 1;
                }
                this.direction = delta.normalize();
                if(this.time < 0){
                    this.state = "shoot";
                    time = .6;
                }
                this.animationGraphComponent.queueAnimation("walk");
                dx -= direction.x * dt * speed;
                dy -= direction.y * dt * speed;
            } else if(this.state.equals("retreat")){
                Vec2d delta = this.player.getTransform().position.minus(pos);
                if(delta.mag() > 7){
                    this.state = "follow";
                    time = 1;
                }
                this.direction = delta.normalize();
                if(this.time < 0){
                    this.state = "shoot";
                    time = .6;
                }
                this.direction = this.direction.smult(-1);
                this.animationGraphComponent.queueAnimation("walk");
                dx -= direction.x * dt * speed;
                dy -= direction.y * dt * speed;
            } else if(this.state.equals("shoot")){
                Vec2d delta = this.player.getTransform().position.minus(pos);
                this.direction = delta.normalize();
                this.animationGraphComponent.queueAnimation("shoot");
                if(this.animationGraphComponent.justFinished()) {
                    Vec2d arrowDir = this.player.getTransform().position.plus(0,0).minus(pos);
                    placeArrow(this.gameObject.gameWorld, pos.plus(0,0), arrowDir);
                    this.state = "standing";
                    this.player = null;
                    this.time = 1;
                }
            }


            this.animationGraphComponent.updateState(new Vec2d[]{this.direction});
            this.gameObject.getTransform().position = new Vec2d(pos.x - dx, pos.y - dy);

        }

        public void followPlayer(GameObject player){
            if(this.player == null && (this.state.equals("idle"))) {
                this.state = "follow";
                this.player = player;
                this.time = 3;
            }
        }

        @Override
        public int getSystemFlags() {
            return SystemFlag.TickSystem;
        }

        @Override
        public String getTag() {
            return "SkeletonMovementComponent";
        }
    }

    public static void placeArrow(GameWorld gameWorld, Vec2d pos, Vec2d direction){
        GameObject arrow = new GameObject(gameWorld, 1);

        int crop = ((int)Math.round(-8.0*direction.angle()/(2.0*Math.PI)) + 1 + 8) % 8;
        SpriteComponent sprite = new SpriteComponent(FinalGame.getSpritePath("projectiles"), new Vec2d(-.5,-.5), new Vec2d(1,1),
                new Vec2d(32.0*crop, 0), new Vec2d(32,32));
        arrow.addComponent(sprite);

        arrow.addComponent(new VelocityComponent(direction.normalize().smult(6)));

        CollisionComponent collisionComponent = new CollisionComponent(new CircleShape(new Vec2d(0,0),.25),
                false, false, FinalGame.ENEMY_LAYER, FinalGame.ENEMY_MASK);
        collisionComponent.linkCollisionCallback(Skeleton::deleteArrow);
        arrow.addComponent(collisionComponent);

        arrow.addComponent(new IDComponent("arrow"));

        arrow.getTransform().position = pos.plus(new Vec2d(0,0)); //make a copy
        gameWorld.addGameObject(arrow);
    }

    public static void deleteArrow(CollisionSystem.CollisionInfo collisionInfo){
        IDComponent id = (IDComponent)collisionInfo.gameObjectOther.getComponent("IDComponent");
        if(id != null){
            if(id.getId().equals("skeleton")) return;
        }
        collisionInfo.gameObjectSelf.gameWorld.removeGameObject(collisionInfo.gameObjectSelf);
    }

    public static void skeletonNearPlayer(CollisionSystem.CollisionInfo collisionInfo){
        SkeletonMovementComponent gmc = (SkeletonMovementComponent)collisionInfo.gameObjectSelf.getComponent("SkeletonMovementComponent");
        if(gmc == null) return;
        IDComponent idComponent = (IDComponent)collisionInfo.gameObjectOther.getComponent("IDComponent");
        if(idComponent == null) return;
        if(!idComponent.getId().equals("player")) return;
        gmc.followPlayer(collisionInfo.gameObjectOther);

    }
}
