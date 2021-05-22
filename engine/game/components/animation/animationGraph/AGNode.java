package engine.game.components.animation.animationGraph;

import engine.game.GameObject;
import engine.game.components.animation.AnimationComponent;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AGNode{
    public String name;

    public String onFinishTransitionTo = null;

    public boolean interruptible = false;

    public AGNode(String name){
        this.name = name;
    }

    public AGNode(String name, String onFinishTransitionTo){
        this.name = name;
        this.onFinishTransitionTo = onFinishTransitionTo;
    }

    public void setInterruptible(boolean interruptible){
        this.interruptible = interruptible;
    }

    public void updateState(Vec2d[] newState){return;}

    public abstract boolean justFinished();

    public abstract void restart();

    public abstract void setGameObject(GameObject g);

    public abstract void onTick(long nanosSincePreviousTick);

    public abstract void onLateTick();

    public abstract void onDraw(GraphicsContext g);

    public abstract Element getXML(Document doc);

}