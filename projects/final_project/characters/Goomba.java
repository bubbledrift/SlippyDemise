package projects.final_project.characters;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.AABShape;
import engine.game.collisionShapes.CircleShape;
import engine.game.components.*;
import engine.game.components.animation.AnimationComponent;
import engine.game.components.animation.SpriteAnimationComponent;
import engine.game.components.animation.animationGraph.AGAnimation;
import engine.game.components.animation.animationGraph.AGAnimationGroup;
import engine.game.components.animation.animationGraph.AGNode;
import javafx.scene.paint.Color;
import projects.final_project.HealthBarComponent;
import projects.final_project.Player;
import projects.final_project.assets.sounds.AnimationGraphComponent;
import engine.game.systems.CollisionSystem;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import projects.final_project.FinalGame;
import projects.final_project.MiscElements;

import java.util.concurrent.ThreadLocalRandom;

public class Goomba {

    private static final Vec2d GOOMBA_SIZE = new Vec2d(1.13,1);

    public static void placeGoomba(GameWorld gameWorld, Vec2d pos){
        GameObject enemy = new GameObject(gameWorld, 1);

        AnimationGraphComponent agc = getGoombaAnimationGraph();
        enemy.addComponent(agc);
        enemy.addComponent(new GoombaMovementComponent(2, agc));


        enemy.addComponent(new CollisionComponent(new AABShape(new Vec2d(-.46,-.9),new Vec2d(0.7,0.65)),
                false, true, FinalGame.ENEMY_LAYER, FinalGame.ENEMY_MASK));

        CollisionComponent hitCollisionComponent = new CollisionComponent(new AABShape(new Vec2d(-.46,-.9),new Vec2d(0.7,0.65)),
                false, false, CollisionSystem.CollisionMask.NONE, FinalGame.ATTACK_MASK);
        hitCollisionComponent.linkCollisionCallback(Goomba::onHitCallback);
        enemy.addComponent(hitCollisionComponent);

        CollisionComponent nearPlayer = new CollisionComponent(new CircleShape(new Vec2d(0,0),5),
                false, false, CollisionSystem.CollisionMask.NONE, FinalGame.OBJECT_MASK);
        nearPlayer.linkCollisionCallback(Goomba::goombaNearPlayer);
        enemy.addComponent(nearPlayer);
        
        enemy.addComponent(new AudioComponent("goomba.wav"));

        HealthComponent healthComponent = new HealthComponent(5);
        healthComponent.linkDeathCallback(Goomba::enemyDeathCallback);
        enemy.addComponent(healthComponent);

        enemy.addComponent(new HealthBarComponent(Color.RED, new Vec2d(0,-1.5), new Vec2d(.8,.1), healthComponent, true));

        enemy.addComponent(new IDComponent("goomba"));

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
        //1/2 chance to drop a potion
        int potion = ThreadLocalRandom.current().nextInt(0, 2);
        if(potion != 1) {
            MiscElements.placePotion(enemy.gameWorld, 1, new Vec2d(pos.x, pos.y),
                    new Vec2d(Math.random() * 2 - 1, Math.random() * 2 - 1).normalize().smult(Math.random()*3));
        }
        DelayEventComponent delayEventComponent = new DelayEventComponent(.1);
        delayEventComponent.linkEventCallback(Goomba::enemyRemoveCallback);
        enemy.addComponent(delayEventComponent);


    }

    private static void enemyRemoveCallback(GameObject gameObject){
        gameObject.gameWorld.removeGameObject(gameObject);
    }

    public static AnimationGraphComponent getGoombaAnimationGraph(){
        Vec2d spriteOffset = new Vec2d(-GOOMBA_SIZE.x/2,-GOOMBA_SIZE.y);
        Vec2d cropSize = new Vec2d(25,22);
        AnimationComponent idle_up = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 1, new Vec2d(0,1*22), cropSize, new Vec2d(25,0), .05);
        AnimationComponent idle_left = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 1, new Vec2d(0,2*22), cropSize, new Vec2d(25,0), .05);
        AnimationComponent idle_down = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 1, new Vec2d(0,0*22), cropSize, new Vec2d(25,0), .05);
        AnimationComponent idle_right = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 1, new Vec2d(0,3*22), cropSize, new Vec2d(25,0), .05);
        AGNode N_idle_up = new AGAnimation("idle_up", idle_up);
        AGNode N_idle_left = new AGAnimation("idle_left", idle_left);
        AGNode N_idle_down = new AGAnimation("idle_down", idle_down);
        AGNode N_idle_right = new AGAnimation("idle_right", idle_right);

        AnimationComponent walk_up = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 7, new Vec2d(25,1*22), cropSize, new Vec2d(25,0), .1);
        AnimationComponent walk_left = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 7, new Vec2d(25,2*22), cropSize, new Vec2d(25,0), .1);
        AnimationComponent walk_down = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 7, new Vec2d(25,0*22), cropSize, new Vec2d(25,0), .1);
        AnimationComponent walk_right = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 7, new Vec2d(25,3*22), cropSize, new Vec2d(25,0), .1);
        AGNode N_walk_up = new AGAnimation("walk_up", walk_up);
        AGNode N_walk_left = new AGAnimation("walk_left", walk_left);
        AGNode N_walk_down = new AGAnimation("walk_down", walk_down);
        AGNode N_walk_right = new AGAnimation("walk_right", walk_right);

        AnimationComponent charge_up = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 7, new Vec2d(25,1*22), cropSize, new Vec2d(25,0), .1);
        AnimationComponent charge_left = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 7, new Vec2d(25,2*22), cropSize, new Vec2d(25,0), .1);
        AnimationComponent charge_down = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 7, new Vec2d(25,0*22), cropSize, new Vec2d(25,0), .1);
        AnimationComponent charge_right = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 7, new Vec2d(25,3*22), cropSize, new Vec2d(25,0), .1);
        AGNode N_charge_up = new AGAnimation("charge_up", charge_up);
        AGNode N_charge_left = new AGAnimation("charge_left", charge_left);
        AGNode N_charge_down = new AGAnimation("charge_down", charge_down);
        AGNode N_charge_right = new AGAnimation("charge_right", charge_right);

        AGAnimationGroup idle = new AGAnimationGroup("idle",
                new AGNode[]{N_idle_up, N_idle_left, N_idle_down, N_idle_right},
                new Vec2d[]{new Vec2d(0,-1), new Vec2d(-1,0), new Vec2d(0,1), new Vec2d(1,0)});
        idle.setInterruptible(true);

        AGAnimationGroup walk = new AGAnimationGroup("walk",
                new AGNode[]{N_walk_up, N_walk_left, N_walk_down, N_walk_right},
                new Vec2d[]{new Vec2d(0,-1), new Vec2d(-1,0), new Vec2d(0,1), new Vec2d(1,0)});
        walk.setInterruptible(true);

        AGAnimationGroup charge = new AGAnimationGroup("charge",
                new AGNode[]{N_charge_up, N_charge_left, N_charge_down, N_charge_right},
                new Vec2d[]{new Vec2d(0,-1), new Vec2d(-1,0), new Vec2d(0,1), new Vec2d(1,0)});
        charge.setInterruptible(true);

        AGNode[] animationNodes = new AGNode[]{idle, walk, charge};
        AnimationGraphComponent agc = new AnimationGraphComponent(animationNodes);

        return agc;
    }

    private static class GoombaMovementComponent extends Component{

        private Vec2d direction = new Vec2d(0,0);
        private double speed, time = 2;

        private double margin = .5;

        private String state = "idle"; // idle, follow, prep, charge

        public GameObject player;

        private AnimationGraphComponent animationGraphComponent;

        public GoombaMovementComponent(double speed, AnimationGraphComponent animationGraphComponent) {
            super();
            this.speed = speed;
            this.animationGraphComponent = animationGraphComponent;
            this.animationGraphComponent.queueAnimation("idle");
        }


        //Every couple seconds, choose a new direction to go and walk in that direction
        @Override
        public void onTick(long nanosSincePreviousTick){
            double dx = 0;
            double dy = 0;
            double dt = nanosSincePreviousTick/1000000000.0; //seconds since last tick
            time -= dt;

            //being hit
            if(this.gameObject.getComponent("ShakeComponent") != null) return;

            if(this.state.equals("idle")){
                if(time <= 0) {//Randomly pick a new direction every 2 seconds.
                    int pickDirection = ThreadLocalRandom.current().nextInt(0, 4);
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
                Vec2d delta = this.player.getTransform().position.minus(this.gameObject.getTransform().position);
                this.direction = delta.normalize();
                if(time < 0){
                    this.state = "prep";
                    time = 1;
                }
                this.animationGraphComponent.queueAnimation("walk");
                dx -= direction.x * dt * speed;
                dy -= direction.y * dt * speed;
            } else if(this.state.equals("prep")){
                Vec2d delta = this.player.getTransform().position.minus(this.gameObject.getTransform().position);
                this.direction = delta.normalize();
                if(time < 0){
                    this.state = "charge";
                    time = 3;
                }
                this.animationGraphComponent.queueAnimation("walk");
            }else if(this.state.equals("charge")){
                if(time <= 0) {
                    this.state = "idle";
                    this.player = null;
                }
                this.animationGraphComponent.queueAnimation("charge");
                dx -= direction.x * dt * speed * 2;
                dy -= direction.y * dt * speed * 2;
            }


            this.animationGraphComponent.updateState(new Vec2d[]{this.direction});
            Vec2d pos = this.gameObject.getTransform().position;
            this.gameObject.getTransform().position = new Vec2d(pos.x - dx, pos.y - dy);

        }

        public void followPlayer(GameObject player){
            if(this.player == null) {
                this.state = "follow";
                this.player = player;
                this.time = 2;
            }
        }

        @Override
        public int getSystemFlags() {
            return SystemFlag.TickSystem;
        }

        @Override
        public String getTag() {
            return "GoombaMovementComponent";
        }
    }

    public static void goombaNearPlayer(CollisionSystem.CollisionInfo collisionInfo){
        GoombaMovementComponent gmc = (GoombaMovementComponent)collisionInfo.gameObjectSelf.getComponent("GoombaMovementComponent");
        if(gmc == null) return;
        IDComponent idComponent = (IDComponent)collisionInfo.gameObjectOther.getComponent("IDComponent");
        if(idComponent == null) return;
        if(!idComponent.getId().equals("player")) return;
        gmc.followPlayer(collisionInfo.gameObjectOther);

    }
}
