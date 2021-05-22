package engine.game.components.animation.animationGraph;

import engine.game.GameObject;
import engine.game.components.animation.AnimationComponent;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AGAnimationGroup  extends AGNode {

    private AGNode[] animationComponents;
    private Vec2d[] stateSpacePosition;

    private Vec2d state = new Vec2d(0,0);

    private int current_animation = 0;
    private boolean stateUpdated = true;

    public AGAnimationGroup(String name, AGNode[] animationComponents, Vec2d[] stateSpacePosition) {
        super(name);
        this.animationComponents = animationComponents;
        this.stateSpacePosition = stateSpacePosition;
        assert(animationComponents.length == stateSpacePosition.length);
        assert(animationComponents.length>0);
    }

    public AGAnimationGroup(String name, String onFinishTransitionTo, AGNode[] animationComponents, Vec2d[] stateSpacePosition){
        super(name, onFinishTransitionTo);
        this.animationComponents = animationComponents;
        this.stateSpacePosition = stateSpacePosition;
        assert(animationComponents.length == stateSpacePosition.length);
        assert(animationComponents.length>0);
    }

    @Override
    public void updateState(Vec2d[] newState){
        if(newState.length > 0){
            this.state = newState[0];
            stateUpdated = true;
            Vec2d[] shortened = new Vec2d[newState.length-1];
            for(int i = 1; i < newState.length; i++){
                shortened[i-1] = newState[i];
            }
            for(AGNode agn : this.animationComponents){
                agn.updateState(shortened);
            }
        }
    }

    public boolean justFinished(){
        return animationComponents[this.current_animation].justFinished();
    }

    public void restart(){
        this.animationComponents[this.current_animation].restart();
    }

    @Override
    public void onTick(long nanosSincePreviousTick) {
        if(stateUpdated){
            int original_animation = this.current_animation;
            double mag = this.state.minus(stateSpacePosition[this.current_animation]).mag();
            for(int i = 0; i < stateSpacePosition.length; i++){
                double m = this.state.minus(stateSpacePosition[i]).mag();
                if(m < mag){
                    mag = m;
                    this.current_animation = i;
                }
            }
            stateUpdated = false;
            if(original_animation != current_animation){
                animationComponents[original_animation].restart();
            }
        }
        animationComponents[this.current_animation].onTick(nanosSincePreviousTick);
    }

    public void setGameObject(GameObject g){
        for(AGNode ac: this.animationComponents){
            ac.setGameObject(g);
        }
    }

    @Override
    public void onLateTick() {
        animationComponents[this.current_animation].onLateTick();
    }

    @Override
    public void onDraw(GraphicsContext g) {
        animationComponents[this.current_animation].onDraw(g);
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("state", state.toString());
        component.setAttribute("current_animation", Integer.toString(current_animation));
        component.setAttribute("stateUpdated", Boolean.toString(stateUpdated));
        for(int i = 0; i < this.animationComponents.length; i++){
            Element p = doc.createElement("AnimationGroupElement");
            p.setAttribute("stateSpacePosition", this.stateSpacePosition[i].toString());
            p.appendChild(this.animationComponents[i].getXML(doc));
            component.appendChild(p);
        }
        return component;
    }

    public static AGNode loadFromXML(Element n){
        return null; //TODO
    }
}
