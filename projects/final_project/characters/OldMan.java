package projects.final_project.characters;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.CircleShape;
import engine.game.components.CollisionComponent;
import projects.final_project.Player;
import projects.final_project.assets.sounds.AnimationGraphComponent;
import engine.game.systems.CollisionSystem;
import engine.support.Vec2d;
import projects.final_project.FinalGame;

public class OldMan {

    public static void placeOldMan(GameWorld gameWorld, Vec2d position){
        GameObject oldMan = new GameObject(gameWorld, 1);
        AnimationGraphComponent agc = Characters.getCharacterAnimationGraph(new Vec2d(2,2), "old_man");
        agc.updateState(new Vec2d[]{new Vec2d(0,1)});
        oldMan.addComponent(agc);

        CollisionComponent talk = new CollisionComponent(new CircleShape(new Vec2d(0,0),1),
                false, false, FinalGame.TALK_LAYER, FinalGame.TALK_MASK);
        talk.linkCollisionCallback(OldMan::speakToOldMan);
        oldMan.addComponent(talk);

        DialogComponent dc = new DialogComponent(getDialog(), Characters.createTextBox(), Characters.createOptionBox());
        oldMan.addComponent(dc);

        oldMan.getTransform().position = position;
        gameWorld.addGameObject(oldMan);
    }

    public static void speakToOldMan(CollisionSystem.CollisionInfo collisionInfo){
        Player.PlayerComponent playerComponent = (Player.PlayerComponent)collisionInfo.gameObjectOther.getComponent("PlayerComponent");
        if(playerComponent == null) return;
        playerComponent.hasAxe = true;
        DialogComponent dc = (DialogComponent)collisionInfo.gameObjectSelf.getComponent("DialogComponent");
        if(dc == null) return;
        dc.startDialog();
    }

    private static DialogNode getDialog(){
        DialogNode A = new DialogNode("Oh, what a wonderful day it would have been!\n" +
                "I was going to chop down my tree and then rest my cold old bones\n" +
                "by the fireplace this winter.");
        DialogNode B = new DialogNode("But that DARN TOAD STOLE MY TREEE!");
        DialogNode C = new DialogNode("*cough* *cough*\n" +
                "Oh well, I guess I don't need my axe anymore.");
        DialogNode D = new DialogNode("Here, you can have it. Maybe you can make better use of it.\n" +
                "\n" +
                "Just press Q to switch to your axe and get swinging");

        DialogNode E = new DialogNode("Kids these days. Always swinging things all over the place.");

        A.setNextNode(B);
        B.setNextNode(C);
        C.setNextNode(D);
        D.setNextNode(E);
        return A;
    }

}
