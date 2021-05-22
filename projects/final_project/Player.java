package projects.final_project;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.AABShape;
import engine.game.collisionShapes.CircleShape;
import engine.game.components.*;
import engine.game.components.animation.animationGraph.AGAnimation;
import engine.game.components.animation.animationGraph.AGAnimationGroup;
import engine.game.components.animation.animationGraph.AGNode;
import engine.game.components.screenEffects.ShakeEffect;
import engine.game.components.screenEffects.TintEffect;
import javafx.scene.paint.Color;
import projects.final_project.assets.sounds.AnimationGraphComponent;
import engine.game.components.animation.SpriteAnimationComponent;
import engine.game.components.animation.AnimationComponent;
import engine.game.components.TextBoxComponent;
import engine.game.components.screenEffects.FadeOutEffect;
import engine.game.systems.CollisionSystem;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import javafx.scene.input.KeyCode;

import java.util.Set;

public class Player {

    public static boolean isBetweenAreas = false;

    public static final Vec2d PLAYER_SIZE = new Vec2d(2,2);
    protected static AudioComponent swing;

    //creates player
    public static GameObject createPlayer(GameWorld gameWorld, Vec2d pos){
        GameObject player = new GameObject(gameWorld, 1);

        player.addComponent(new CameraComponent(0, new Vec2d(0,0), new Vec2d(0,40), new Vec2d(0, 40)));

        //TODO this is very bad design needs to be fixed at some point
        // need to somehow differentiate between multiple components of same type. Maybe give components names?
        CollisionComponent attackHitBox = new CollisionComponent(new CircleShape(new Vec2d(0,0),.5),
                false, false, FinalGame.ATTACK_LAYER, FinalGame.ATTACK_MASK){
                    @Override
                    public String getTag() {
                return "AttackCollisionComponent";
            }
                };
        player.addComponent(attackHitBox);

        AnimationGraphComponent animationGraphComponent = getPlayerAnimationGraph(attackHitBox);
        player.addComponent(animationGraphComponent);

        player.addComponent(new PlayerComponent(animationGraphComponent, 5));
        player.addComponent(new ValueComponent(0)); //Player Score

        LightComponent lightComponent = new LightComponent(0, 5, new Vec2d(0,0));
        player.addComponent(lightComponent);

        DrawFogComponent drawFogComponent = new DrawFogComponent(0, new Vec2d(0,0), .05, 1);
        drawFogComponent.disable();
        player.addComponent(drawFogComponent);



        swing = new AudioComponent("swing.wav", true);

        player.addComponent(new AudioComponent("playerDamage.wav"));

        CollisionComponent collision = new CollisionComponent(new CircleShape(new Vec2d(0,0),.25),
                false, true, FinalGame.PLAYER_LAYER, FinalGame.PLAYER_MASK);
        player.addComponent(collision);

        CollisionComponent hitbox = new CollisionComponent(new AABShape(new Vec2d(-.25,-1),new Vec2d(.5,1.26)),
                false, false, CollisionSystem.CollisionMask.NONE, FinalGame.ENEMY_LAYER);
        hitbox.linkCollisionCallback(Player::PlayerCollisionCallback);
        player.addComponent(hitbox);

        HealthComponent healthComponent = new HealthComponent(10);
        healthComponent.linkDeathCallback(Player::playerDeathCallback);
        player.addComponent(healthComponent);

        player.addComponent(new TintEffect(0, Color.RED, 0));

        //For knowing if the game has been won
        player.addComponent(new BooleanComponent(false));

        //TALKING TRIGGER
        //TODO this is very bad design needs to be fixed at some point
        // need to somehow differentiate between multiple components of same type. Maybe give components names?
        CollisionComponent talkTrigger = new CollisionComponent(new CircleShape(new Vec2d(0,0),1),
                false, false, FinalGame.TALK_LAYER, FinalGame.TALK_MASK){
                    @Override
                    public String getTag() {
                        return "TalkCollisionComponent";
                    }
                };
        player.addComponent(talkTrigger);

        player.addComponent(new IDComponent("player"));

        player.getTransform().position = pos;
        player.getTransform().size = new Vec2d(1.4,1.75);
        return player;
    }

    private static void PlayerCollisionCallback(CollisionSystem.CollisionInfo collisionInfo){
        GameObject player = collisionInfo.gameObjectSelf;
        IDComponent id = (IDComponent)collisionInfo.gameObjectOther.getComponent("IDComponent");
        ShakeEffect shake = (ShakeEffect)collisionInfo.gameObjectOther.getComponent("ShakeEffect");
        if(shake != null) return; //taking damage
        if(id == null) return;
        double damage = 0;
        if(id.getId().equals("goomba")){
            damage = .1;
        }
        if(id.getId().equals("skeleton")){
            damage = .15;
        }
        if(id.getId().equals("arrow")){
            damage = .05;
        }
        if(id.getId().equals("slippy")){
            damage = .2;
        }
        if(id.getId().equals("SlippySpit")){
            damage = .2;
        }
        if(damage > 0) {
            ((HealthComponent) (player.getComponent("HealthComponent"))).hit(damage);

            DelayEventComponent delayEventComponent = new DelayEventComponent(damage);
            delayEventComponent.linkEventCallback(Player::deleteScreenShake);
            ShakeEffect shakeEffect = new ShakeEffect(.1, damage);

            ((AudioComponent)(collisionInfo.gameObjectSelf.getComponent("AudioComponent"))).start();
            player.addComponent(delayEventComponent);
            player.addComponent(shakeEffect);
        }
    }

    public static void deleteScreenShake(GameObject player){

    }

    private static void playerDeathCallback(GameObject player){
        AnimationGraphComponent agc = (AnimationGraphComponent)player.getComponent("AnimationGraphComponent");
        if(agc == null) return;
        if(agc.getCurrentAnimation().equals("death")) return;
        agc.queueAnimation("death", true);
        PlayerComponent playerComponent = (PlayerComponent)player.getComponent("PlayerComponent");
        playerComponent.disable();
        deathFadeout(player);
    }

    public static void deathFadeout(GameObject player) {
        //Fadeout
        FadeOutEffect fadeout = new FadeOutEffect(0, 1);
        fadeout.linkEventCallback(FinalGame::onDeath);
        player.addComponent(fadeout);
        DrawFogComponent fog = (DrawFogComponent)player.getComponent("DrawFogComponent");
        fog.disable();
        CameraComponent camera = (CameraComponent)player.getComponent("CameraComponent");
        camera.setHorizontalRange(new Vec2d(0,40));
        camera.setVerticalRange(new Vec2d(0,40));
    }

    private static AnimationGraphComponent getPlayerAnimationGraph(CollisionComponent playerAttackBox){
        Vec2d spriteOffset = new Vec2d(-1,-2);

        //IDLE SWORD
        AnimationComponent idle_sword_up = new SpriteAnimationComponent(FinalGame.getSpritePath("player_sword"),
                spriteOffset, PLAYER_SIZE, 1, new Vec2d(0,8*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent idle_sword_left = new SpriteAnimationComponent(FinalGame.getSpritePath("player_sword"),
                spriteOffset, PLAYER_SIZE, 1, new Vec2d(0,9*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent idle_sword_down = new SpriteAnimationComponent(FinalGame.getSpritePath("player_sword"),
                spriteOffset, PLAYER_SIZE, 1, new Vec2d(0,10*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent idle_sword_right = new SpriteAnimationComponent(FinalGame.getSpritePath("player_sword"),
                spriteOffset, PLAYER_SIZE, 1, new Vec2d(0,11*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        idle_sword_up.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true});
        idle_sword_left.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true});
        idle_sword_down.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true});
        idle_sword_right.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true});
        AGNode N_idle_sword_up = new AGAnimation("idle_sword_up", idle_sword_up);
        AGNode N_idle_sword_left = new AGAnimation("idle_sword_left", idle_sword_left);
        AGNode N_idle_sword_down = new AGAnimation("idle_sword_down", idle_sword_down);
        AGNode N_idle_sword_right = new AGAnimation("idle_sword_right", idle_sword_right);

        //WALK SWORD
        AnimationComponent walk_sword_up = new SpriteAnimationComponent(FinalGame.getSpritePath("player_sword"),
                spriteOffset, PLAYER_SIZE, 9, new Vec2d(0,8*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent walk_sword_left = new SpriteAnimationComponent(FinalGame.getSpritePath("player_sword"),
                spriteOffset, PLAYER_SIZE, 9, new Vec2d(0,9*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent walk_sword_down = new SpriteAnimationComponent(FinalGame.getSpritePath("player_sword"),
                spriteOffset, PLAYER_SIZE, 9, new Vec2d(0,10*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent walk_sword_right = new SpriteAnimationComponent(FinalGame.getSpritePath("player_sword"),
                spriteOffset, PLAYER_SIZE, 9, new Vec2d(0,11*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        walk_sword_up.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,true,true,true,true,true,true,true,true});
        walk_sword_left.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,true,true,true,true,true,true,true,true});
        walk_sword_down.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,true,true,true,true,true,true,true,true});
        walk_sword_right.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,true,true,true,true,true,true,true,true});
        AGNode N_walk_sword_up = new AGAnimation("walk_sword_up", walk_sword_up);
        AGNode N_walk_sword_left = new AGAnimation("walk_sword_left", walk_sword_left);
        AGNode N_walk_sword_down = new AGAnimation("walk_sword_down", walk_sword_down);
        AGNode N_walk_sword_right = new AGAnimation("walk_sword_right", walk_sword_right);


        //ATTACK SWORD
        AnimationComponent attack_sword_up = new SpriteAnimationComponent(FinalGame.getSpritePath("player_sword"),
                new Vec2d(-3,-4), PLAYER_SIZE.smult(3), 6, new Vec2d(0,7*192), new Vec2d(192,192), new Vec2d(192,0), .05);
        AnimationComponent attack_sword_left = new SpriteAnimationComponent(FinalGame.getSpritePath("player_sword"),
                new Vec2d(-3,-4), PLAYER_SIZE.smult(3), 6, new Vec2d(0,8*192), new Vec2d(192,192), new Vec2d(192,0), .05);
        AnimationComponent attack_sword_down = new SpriteAnimationComponent(FinalGame.getSpritePath("player_sword"),
                new Vec2d(-3,-4), PLAYER_SIZE.smult(3), 6, new Vec2d(0,9*192), new Vec2d(192,192), new Vec2d(192,0), .05);
        AnimationComponent attack_sword_right = new SpriteAnimationComponent(FinalGame.getSpritePath("player_sword"),
                new Vec2d(-3,-4), PLAYER_SIZE.smult(3), 6, new Vec2d(0,10*192), new Vec2d(192,192), new Vec2d(192,0), .05);
        attack_sword_up.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,false,false,false,false,false});
        attack_sword_up.addAnimationSequence(playerAttackBox.position,
                new Vec2d[]{new Vec2d(0,0),new Vec2d(-1,-.5),new Vec2d(-.5,-.5),new Vec2d(-1,-1),new Vec2d(0,-2),new Vec2d(1.5,-1)});

        attack_sword_left.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,false,false,false,false,false});
        attack_sword_left.addAnimationSequence(playerAttackBox.position,
                new Vec2d[]{new Vec2d(0,0),new Vec2d(0,0),new Vec2d(.5,-.75),new Vec2d(0,-.5),new Vec2d(-2,-1),new Vec2d(-.75,-1)});

        attack_sword_down.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,false,false,false,false,false});
        attack_sword_down.addAnimationSequence(playerAttackBox.position,
                new Vec2d[]{new Vec2d(0,0),new Vec2d(-.5,-.5),new Vec2d(-.75,-1),new Vec2d(-.75,-1),new Vec2d(0,.5),new Vec2d(1.5,-.5)});

        attack_sword_right.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,false,false,false,false,false});
        attack_sword_right.addAnimationSequence(playerAttackBox.position,
                new Vec2d[]{new Vec2d(0,0),new Vec2d(0,0),new Vec2d(-.5,-.75),new Vec2d(0,-.5),new Vec2d(2,-1),new Vec2d(.75,-1)});
        AGNode N_attack_sword_up = new AGAnimation("attack_sword_up", attack_sword_up);
        AGNode N_attack_sword_left = new AGAnimation("attack_sword_left", attack_sword_left);
        AGNode N_attack_sword_down = new AGAnimation("attack_sword_down", attack_sword_down);
        AGNode N_attack_sword_right = new AGAnimation("attack_sword_right", attack_sword_right);



        //IDLE AXE
        AnimationComponent idle_axe_up = new SpriteAnimationComponent(FinalGame.getSpritePath("player_axe"),
                spriteOffset, PLAYER_SIZE, 1, new Vec2d(0,8*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent idle_axe_left = new SpriteAnimationComponent(FinalGame.getSpritePath("player_axe"),
                spriteOffset, PLAYER_SIZE, 1, new Vec2d(0,9*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent idle_axe_down = new SpriteAnimationComponent(FinalGame.getSpritePath("player_axe"),
                spriteOffset, PLAYER_SIZE, 1, new Vec2d(0,10*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent idle_axe_right = new SpriteAnimationComponent(FinalGame.getSpritePath("player_axe"),
                spriteOffset, PLAYER_SIZE, 1, new Vec2d(0,11*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        idle_axe_up.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true});
        idle_axe_left.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true});
        idle_axe_down.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true});
        idle_axe_right.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true});
        AGNode N_idle_axe_up = new AGAnimation("idle_axe_up", idle_axe_up);
        AGNode N_idle_axe_left = new AGAnimation("idle_axe_left", idle_axe_left);
        AGNode N_idle_axe_down = new AGAnimation("idle_axe_down", idle_axe_down);
        AGNode N_idle_axe_right = new AGAnimation("idle_axe_right", idle_axe_right);

        //WALK AXE
        AnimationComponent walk_axe_up = new SpriteAnimationComponent(FinalGame.getSpritePath("player_axe"),
                spriteOffset, PLAYER_SIZE, 9, new Vec2d(0,8*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent walk_axe_left = new SpriteAnimationComponent(FinalGame.getSpritePath("player_axe"),
                spriteOffset, PLAYER_SIZE, 9, new Vec2d(0,9*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent walk_axe_down = new SpriteAnimationComponent(FinalGame.getSpritePath("player_axe"),
                spriteOffset, PLAYER_SIZE, 9, new Vec2d(0,10*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        AnimationComponent walk_axe_right = new SpriteAnimationComponent(FinalGame.getSpritePath("player_axe"),
                spriteOffset, PLAYER_SIZE, 9, new Vec2d(0,11*64), new Vec2d(64,64), new Vec2d(64,0), .05);
        walk_axe_up.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,true,true,true,true,true,true,true,true});
        walk_axe_left.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,true,true,true,true,true,true,true,true});
        walk_axe_down.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,true,true,true,true,true,true,true,true});
        walk_axe_right.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,true,true,true,true,true,true,true,true});
        AGNode N_walk_axe_up = new AGAnimation("walk_axe_up", walk_axe_up);
        AGNode N_walk_axe_left = new AGAnimation("walk_axe_left", walk_axe_left);
        AGNode N_walk_axe_down = new AGAnimation("walk_axe_down", walk_axe_down);
        AGNode N_walk_axe_right = new AGAnimation("walk_axe_right", walk_axe_right);

        //ATTACK AXE
        AnimationComponent attack_axe_up = new SpriteAnimationComponent(FinalGame.getSpritePath("player_axe"),
                new Vec2d(-3,-4), PLAYER_SIZE.smult(3), 6, new Vec2d(0,7*192), new Vec2d(192,192), new Vec2d(192,0), .1);
        AnimationComponent attack_axe_left = new SpriteAnimationComponent(FinalGame.getSpritePath("player_axe"),
                new Vec2d(-3,-4), PLAYER_SIZE.smult(3), 6, new Vec2d(0,8*192), new Vec2d(192,192), new Vec2d(192,0), .1);
        AnimationComponent attack_axe_down = new SpriteAnimationComponent(FinalGame.getSpritePath("player_axe"),
                new Vec2d(-3,-4), PLAYER_SIZE.smult(3), 6, new Vec2d(0,9*192), new Vec2d(192,192), new Vec2d(192,0), .1);
        AnimationComponent attack_axe_right = new SpriteAnimationComponent(FinalGame.getSpritePath("player_axe"),
                new Vec2d(-3,-4), PLAYER_SIZE.smult(3), 6, new Vec2d(0,10*192), new Vec2d(192,192), new Vec2d(192,0), .1);
        attack_axe_up.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,false,false,false,false,false});
        attack_axe_up.addAnimationSequence(playerAttackBox.position,
                new Vec2d[]{new Vec2d(0,0),new Vec2d(-.5,-.5),new Vec2d(-.5,-.5),new Vec2d(-.5,-1),new Vec2d(0,-1.5),new Vec2d(1.5,-1)});

        attack_axe_left.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,false,false,false,false,false});
        attack_axe_left.addAnimationSequence(playerAttackBox.position,
                new Vec2d[]{new Vec2d(0,0),new Vec2d(0,-.25),new Vec2d(.25,-.75),new Vec2d(-.5,-.5),new Vec2d(-1,-1),new Vec2d(-.75,-1)});

        attack_axe_down.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,false,false,false,false,false});
        attack_axe_down.addAnimationSequence(playerAttackBox.position,
                new Vec2d[]{new Vec2d(0,0),new Vec2d(-.5,-.5),new Vec2d(-.5,-.5),new Vec2d(-.5,-.5),new Vec2d(0,0),new Vec2d(1,-.5)});

        attack_axe_right.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true,false,false,false,false,false});
        attack_axe_right.addAnimationSequence(playerAttackBox.position,
                new Vec2d[]{new Vec2d(0,0),new Vec2d(0,0),new Vec2d(-.5,-.25),new Vec2d(.5,-.5),new Vec2d(1,-1),new Vec2d(.75,-1)});
        AGNode N_attack_axe_up = new AGAnimation("attack_axe_up", attack_axe_up);
        AGNode N_attack_axe_left = new AGAnimation("attack_axe_left", attack_axe_left);
        AGNode N_attack_axe_down = new AGAnimation("attack_axe_down", attack_axe_down);
        AGNode N_attack_axe_right = new AGAnimation("attack_axe_right", attack_axe_right);

        //DEATH
        AnimationComponent deathAnimation = new SpriteAnimationComponent(FinalGame.getSpritePath("player"),
                spriteOffset, PLAYER_SIZE, 6, new Vec2d(0,20*64), new Vec2d(64,64), new Vec2d(64,0),
                .15);
        deathAnimation.addAnimationSequence(playerAttackBox.disabled, new Boolean[]{true, true, true, true, true, true});
        AGNode N_death = new AGAnimation("death", deathAnimation);

        //GROUPS

        //SWORD
        AGAnimationGroup idle_sword = new AGAnimationGroup("idle_sword",
                new AGNode[]{N_idle_sword_up, N_idle_sword_left, N_idle_sword_down, N_idle_sword_right},
                new Vec2d[]{new Vec2d(0,-1), new Vec2d(-1,0), new Vec2d(0,1), new Vec2d(1,0)});
        idle_sword.setInterruptible(true);

        AGAnimationGroup walk_sword = new AGAnimationGroup("walk_sword",
                new AGNode[]{N_walk_sword_up, N_walk_sword_left, N_walk_sword_down, N_walk_sword_right},
                new Vec2d[]{new Vec2d(0,-1), new Vec2d(-1,0), new Vec2d(0,1), new Vec2d(1,0)});
        walk_sword.setInterruptible(true);

        AGAnimationGroup attack_sword = new AGAnimationGroup("attack_sword", "idle_sword",
                new AGNode[]{N_attack_sword_up, N_attack_sword_left, N_attack_sword_down, N_attack_sword_right},
                new Vec2d[]{new Vec2d(0,-1), new Vec2d(-1,0), new Vec2d(0,1), new Vec2d(1,0)});
        attack_sword.setInterruptible(false);

        //AXE
        AGAnimationGroup idle_axe = new AGAnimationGroup("idle_axe",
                new AGNode[]{N_idle_axe_up, N_idle_axe_left, N_idle_axe_down, N_idle_axe_right},
                new Vec2d[]{new Vec2d(0,-1), new Vec2d(-1,0), new Vec2d(0,1), new Vec2d(1,0)});
        idle_axe.setInterruptible(true);

        AGAnimationGroup walk_axe = new AGAnimationGroup("walk_axe",
                new AGNode[]{N_walk_axe_up, N_walk_axe_left, N_walk_axe_down, N_walk_axe_right},
                new Vec2d[]{new Vec2d(0,-1), new Vec2d(-1,0), new Vec2d(0,1), new Vec2d(1,0)});
        walk_axe.setInterruptible(true);

        AGAnimationGroup attack_axe = new AGAnimationGroup("attack_axe", "idle_axe",
                new AGNode[]{N_attack_axe_up, N_attack_axe_left, N_attack_axe_down, N_attack_axe_right},
                new Vec2d[]{new Vec2d(0,-1), new Vec2d(-1,0), new Vec2d(0,1), new Vec2d(1,0)});
        attack_axe.setInterruptible(false);


        AGAnimationGroup idle = new AGAnimationGroup("idle",
                new AGNode[]{idle_sword, idle_axe},
                new Vec2d[]{new Vec2d(0,0), new Vec2d(0,1)});
        idle.setInterruptible(true);

        AGAnimationGroup walk = new AGAnimationGroup("walk",
                new AGNode[]{walk_sword, walk_axe},
                new Vec2d[]{new Vec2d(0,0), new Vec2d(0,1)});
        walk.setInterruptible(true);

        AGAnimationGroup attack = new AGAnimationGroup("attack", "idle",
                new AGNode[]{attack_sword, attack_axe},
                new Vec2d[]{new Vec2d(0,0), new Vec2d(0,1)});
        attack.setInterruptible(false);




        AGNode[] animationNodes = new AGNode[]{idle, walk, attack, N_death};
        AnimationGraphComponent agc = new AnimationGraphComponent(animationNodes);

        return agc;
    }

    public static class PlayerComponent extends Component{

        private Vec2d direction = new Vec2d(0,1);
        private double speed;

        private int currentWeapon = 0; // 0-sword 1-axe 2-bow
        private boolean justSwitched = false;

        public boolean hasAxe = false;

        private AnimationGraphComponent animationGraphComponent;

        public PlayerComponent(AnimationGraphComponent animationGraphComponent, double speed) {
            super();
            this.speed = speed;
            this.animationGraphComponent = animationGraphComponent;
        }

        public int getCurrentWeapon(){
            return this.currentWeapon;
        }

        public double getAttack(){
            if(this.currentWeapon == 0) return 1;
            if(this.currentWeapon == 1) return 2;
            return 1;
        }

        @Override
        public void onTick(long nanosSincePreviousTick){
            double dx = 0;
            double dy = 0;
            double dt = nanosSincePreviousTick/1000000000.0; //seconds since last tick

            Set<KeyCode> keyState = this.gameObject.gameWorld.getKeyState();

            boolean W = keyState.contains(KeyCode.W);
            boolean A = keyState.contains(KeyCode.A);
            boolean S = keyState.contains(KeyCode.S);
            boolean D = keyState.contains(KeyCode.D);
            boolean ATTACK = keyState.contains(KeyCode.SPACE);
            boolean E = keyState.contains(KeyCode.E);
            boolean Q = keyState.contains(KeyCode.Q);
            if(W) dy += speed * dt;
            if(S) dy -= speed * dt;
            if(A) dx += speed * dt;
            if(D) dx -= speed * dt;

            if(Q && !justSwitched){
                this.currentWeapon += 1;
                if(!hasAxe && this.currentWeapon == 1) this.currentWeapon += 1;
                this.currentWeapon = this.currentWeapon % 2;
                justSwitched = true;
            } else if(!Q){
                justSwitched = false;
            }

            if(!(W || A || S || D || ATTACK)){
                swing.stop();
                animationGraphComponent.queueAnimation("idle", true);
            } else if(!ATTACK) {
                swing.stop();
                this.direction = new Vec2d(-dx, -dy);
                animationGraphComponent.queueAnimation("walk");

                Vec2d pos = this.gameObject.getTransform().position;
                this.gameObject.getTransform().position = new Vec2d(pos.x - dx, pos.y - dy);
//                System.out.println(new Vec2d(pos.x - dx, pos.y - dy));
            } else {
                animationGraphComponent.queueAnimation("attack", true);

                swing.start();

            }

            CollisionComponent talk = (CollisionComponent)this.gameObject.getComponent("TalkCollisionComponent");
            if(talk != null){
                if(E){
                    talk.enable();
                } else {
                    talk.disable();
                }
            }
            animationGraphComponent.updateState(new Vec2d[]{new Vec2d(0,this.currentWeapon), this.direction});


            TintEffect tint = (TintEffect)this.gameObject.getComponent("TintEffect");
            HealthComponent healthComponent = (HealthComponent)this.gameObject.getComponent("HealthComponent");
            tint.setTint((1-healthComponent.getHealthRatio()) * .3);
        }

        @Override
        public int getSystemFlags() {
            return SystemFlag.TickSystem;
        }

        @Override
        public String getTag() {
            return "PlayerComponent";
        }
    }

}
