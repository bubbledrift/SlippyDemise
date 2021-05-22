package projects.final_project.characters;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.CircleShape;
import engine.game.components.CollisionComponent;
import projects.final_project.assets.sounds.AnimationGraphComponent;
import engine.game.systems.CollisionSystem;
import engine.support.Vec2d;
import projects.final_project.FinalGame;

public class OldWoman {

    public static void placeOldWoman(GameWorld gameWorld, Vec2d position){
        GameObject oldMan = new GameObject(gameWorld, 1);
        AnimationGraphComponent agc = Characters.getCharacterAnimationGraph(new Vec2d(2,2), "old_woman");
        agc.updateState(new Vec2d[]{new Vec2d(0,1)});
        oldMan.addComponent(agc);

        CollisionComponent talk = new CollisionComponent(new CircleShape(new Vec2d(0,0),1),
                false, false, FinalGame.TALK_LAYER, FinalGame.TALK_MASK);
        talk.linkCollisionCallback(OldWoman::speakToOldWoman);
        oldMan.addComponent(talk);

        DialogComponent dc = new DialogComponent(getDialog(), Characters.createTextBox(), Characters.createOptionBox());
        oldMan.addComponent(dc);

        oldMan.getTransform().position = position;
        gameWorld.addGameObject(oldMan);
    }

    public static void speakToOldWoman(CollisionSystem.CollisionInfo collisionInfo){
        DialogComponent dc = (DialogComponent)collisionInfo.gameObjectSelf.getComponent("DialogComponent");
        if(dc == null) return;
        dc.startDialog();
    }

    private static DialogNode getDialog(){
        DialogNode A = new DialogNode("My poor Henrietta...");
        DialogNode B = new DialogNode("Slippy had another episode yesterday. He raided our home and stole my chicken.");
        DialogNode C = new DialogNode("At least he didn't steal our life savings that I hid behind the bed in our house.");

        DialogNode D = new DialogNode("Shhh... just don't tell the Toad.");

        A.setNextNode(B);
        B.setNextNode(C);
        C.setNextNode(D);
        return A;
    }

}
