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
import engine.game.systems.SystemFlag;
import javafx.scene.paint.Color;
import projects.final_project.HealthBarComponent;
import projects.final_project.MiscElements;
import projects.final_project.Player;
import projects.final_project.assets.sounds.AnimationGraphComponent;
import engine.game.systems.CollisionSystem;
import engine.support.Vec2d;
import projects.final_project.FinalGame;

import java.util.concurrent.ThreadLocalRandom;

public class NhoKlu {

    public static void placeNhoKlu(GameWorld gameWorld, Vec2d position, Vec2d sillyGoombaPosition){
        GameObject nhoKlu = new GameObject(gameWorld, 1);
        AnimationGraphComponent agc = Characters.getCharacterAnimationGraph(new Vec2d(2,2), "Nho-Klu");
        agc.updateState(new Vec2d[]{new Vec2d(0,1)});
        nhoKlu.addComponent(agc);

        CollisionComponent talk = new CollisionComponent(new CircleShape(new Vec2d(0,0),1),
                false, false, FinalGame.TALK_LAYER, FinalGame.TALK_MASK);
        talk.linkCollisionCallback(NhoKlu::speakToNhoKlu);
        nhoKlu.addComponent(talk);

        DialogComponent dc = new DialogComponent(getDialog1(), Characters.createTextBox(), Characters.createOptionBox());
        nhoKlu.addComponent(dc);

        nhoKlu.getTransform().position = position;
        gameWorld.addGameObject(nhoKlu);

        placeSillyGoomba(gameWorld, sillyGoombaPosition, nhoKlu);
    }

    public static void speakToNhoKlu(CollisionSystem.CollisionInfo collisionInfo){
        DialogComponent dc = (DialogComponent)collisionInfo.gameObjectSelf.getComponent("DialogComponent");
        if(dc == null) return;
        dc.startDialog();
    }

    private static DialogNode getDialog1(){
        DialogNode A = new DialogNode("Hello traveler. I see you have an sword there.");
        DialogNode B = new DialogNode("Could you help me kill that Silly Goomba over there?\n" +
                "It has been following me for the past 3 days and my hammer is much too \n" +
                "blunt to get through its skin.");
        DialogNode C = new DialogNode("Just walk up to the Goomba and press SPACE.\n" +
                "Just be careful! Those Goomba's are covered in Slippy Juice and \n" +
                "that stuff is super poisonous.");

        A.setNextNode(B);
        B.setNextNode(C);
        return A;
    }

    private static DialogNode getDialog2(){
        DialogNode A = new DialogNode("Impressive! You seem to be quite good with that sword.");
        DialogNode A2 = new DialogNode("A dead Goomba is the best Goomba, I always say.");

        DialogNode B = new DialogNode("Not long ago an evil tyrant took over these lands.\n" +
                "He stole from our people and summoned monsters to make us live in fear.");
        DialogNode C = new DialogNode("His name is Slippy the Toad and that Goomba is one of his creations.\n" +
                "If only someone could defeat this villain...\n" +
                "I have been unable to return home for many years.");

        DialogNode bye = new DialogNode("Farewell Traveler... and good luck.");

        A.setNextNode(A2);
        A2.setOptions(new String[]{"Where did \nthe Goomba come from?", "See ya!"}, new DialogNode[]{B,bye});
        B.setNextNode(C);
        C.setNextNode(bye);
        return A;
    }




    private static final Vec2d GOOMBA_SIZE = new Vec2d(1.13,1);

    public static void placeSillyGoomba(GameWorld gameWorld, Vec2d pos, GameObject nhoKlu){
        GameObject enemy = new GameObject(gameWorld, 1);

        AnimationGraphComponent agc = getSillyGoombaAnimationGraph();
        enemy.addComponent(agc);
        enemy.addComponent(new NhoKlu.SillyGoombaMovementComponent(agc,nhoKlu));

        CollisionComponent hitCollisionComponent = new CollisionComponent(new AABShape(new Vec2d(-.46,-.9),new Vec2d(0.7,0.65)),
                false, false, CollisionSystem.CollisionMask.NONE, FinalGame.ATTACK_MASK);
        hitCollisionComponent.linkCollisionCallback(NhoKlu::onHitCallback);
        enemy.addComponent(hitCollisionComponent);

        HealthComponent healthComponent = new HealthComponent(5);
        healthComponent.linkDeathCallback(NhoKlu::enemyDeathCallback);
        enemy.addComponent(healthComponent);

        enemy.addComponent(new HealthBarComponent(Color.RED, new Vec2d(0,-1.5), new Vec2d(.8,.1), healthComponent, true));

        enemy.addComponent(new IDComponent("silly_goomba"));

        enemy.getTransform().position = pos;
        enemy.getTransform().size = new Vec2d(2,2);
        gameWorld.addGameObject(enemy);
    }

    public static void onHitCallback(CollisionSystem.CollisionInfo collisionInfo){
        if(collisionInfo.gameObjectSelf.getComponent("ShakeComponent") == null) {
            collisionInfo.gameObjectSelf.addComponent(new ShakeComponent(.1, .1));
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

        DelayEventComponent delayEventComponent = new DelayEventComponent(.1);
        delayEventComponent.linkEventCallback(NhoKlu::enemyRemoveCallback);
        enemy.addComponent(delayEventComponent);


    }

    private static void enemyRemoveCallback(GameObject gameObject){
        SillyGoombaMovementComponent sgmc = (SillyGoombaMovementComponent)gameObject.getComponent("SillyGoombaMovementComponent");
        DialogComponent dc = (DialogComponent)sgmc.nhoKlu.getComponent("DialogComponent");
        sgmc.nhoKlu.removeComponent(dc);
        dc = new DialogComponent(getDialog2(), Characters.createTextBox(), Characters.createOptionBox());
        sgmc.nhoKlu.addComponent(dc);
        dc.onTick(0);
        gameObject.gameWorld.removeGameObject(gameObject);
    }

    private static AnimationGraphComponent getSillyGoombaAnimationGraph(){
        Vec2d spriteOffset = new Vec2d(-GOOMBA_SIZE.x/2,-GOOMBA_SIZE.y);
        Vec2d cropSize = new Vec2d(25,22);

        AnimationComponent idle_down = new SpriteAnimationComponent(FinalGame.getSpritePath("goomba"),
                spriteOffset, GOOMBA_SIZE, 1, new Vec2d(0,0*22), cropSize, new Vec2d(25,0), .05);

        AGNode[] animationNodes = new AGNode[]{new AGAnimation("idle", idle_down)};
        AnimationGraphComponent agc = new AnimationGraphComponent(animationNodes);

        return agc;
    }

    private static class SillyGoombaMovementComponent extends Component{

        private String state = "being silly";

        private AnimationGraphComponent animationGraphComponent;
        public GameObject nhoKlu;

        public SillyGoombaMovementComponent(AnimationGraphComponent animationGraphComponent, GameObject nhoKlu) {
            super();
            this.animationGraphComponent = animationGraphComponent;
            this.nhoKlu = nhoKlu;
        }

        @Override
        public int getSystemFlags() {
            return SystemFlag.None;
        }

        @Override
        public String getTag() {
            return "SillyGoombaMovementComponent";
        }
    }

}
