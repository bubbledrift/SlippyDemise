package projects.final_project.assets.sounds;

import engine.game.GameObject;
import engine.game.components.Component;
import engine.game.components.animation.animationGraph.AGAnimationGroup;
import engine.game.components.animation.animationGraph.AGNode;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;

public class AnimationGraphComponent extends Component {

    private AGNode[] nodes;

    private HashMap<String, Integer> nodesLookup;
    private int currentNode = 0;

    private String nextAnimation = null;
    private boolean transitionWithInterupt = false;


    //TODO Animations should have flag for if they are interruptible
    //TODO there should be a proper queue for animations and a "fade out" time
    //Should function like key input system in smash.

    public AnimationGraphComponent(AGNode[] nodes) {
        this.nodes = nodes;
        assert(nodes.length > 0);

        nodesLookup = new HashMap<>();
        for(int i = 0; i < nodes.length; i++){
            assert(!nodesLookup.containsKey(nodes[i].name));
            nodesLookup.put(nodes[i].name,i);
        }
    }

    public void updateState(Vec2d[] state){
        if(this.nodes[this.currentNode] instanceof AGAnimationGroup){
            AGAnimationGroup g = (AGAnimationGroup)(this.nodes[this.currentNode]);
            g.updateState(state);
        }
    }

    public void queueAnimation(String name){
        if(this.nodes[this.currentNode].name.equals(name)) return;
        this.nextAnimation = name;
        this.transitionWithInterupt = false;
    }

    public void queueAnimation(String name, boolean transitionWithInterupt){
        if(this.nodes[this.currentNode].name.equals(name)) return;
        this.nextAnimation = name;
        this.transitionWithInterupt = transitionWithInterupt;
    }

    private void transitionToQueuedAnimation(){
        currentNode = this.nodesLookup.get(this.nextAnimation);
        this.nextAnimation = null;
        this.transitionWithInterupt = false;
    }

    public String getCurrentAnimation(){
        return this.nodes[this.currentNode].name;
    }

    public boolean justFinished(){
        return this.nodes[this.currentNode].justFinished();
    }

    public void onTick(long nanosSincePreviousTick) {
        AGNode current = this.nodes[this.currentNode];
        if(current.interruptible && transitionWithInterupt && this.nextAnimation != null){
            current.restart();
            transitionToQueuedAnimation();
        }

        current.onTick(nanosSincePreviousTick);
        if(current.justFinished()){
            if(this.nextAnimation != null){
                current.restart();
                transitionToQueuedAnimation();
            } else if(current.onFinishTransitionTo != null) {
                current.restart();
                currentNode = this.nodesLookup.get(current.onFinishTransitionTo);
            }
        }
    }

    public void onLateTick() {
        this.nodes[this.currentNode].onLateTick();
    }

    public void onDraw(GraphicsContext g) {
        this.nodes[this.currentNode].onDraw(g);
    }

    @Override
    public void setGameObject(GameObject g) {
        super.setGameObject(g);
        for(int i = 0; i < nodes.length; i++){
            nodes[i].setGameObject(g);
        }
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.RenderSystem | SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "AnimationGraphComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("currentNode", Integer.toString(currentNode));
        component.setAttribute("nextAnimation", nextAnimation);
        component.setAttribute("cropSize", Boolean.toString(transitionWithInterupt));
        for(AGNode n : nodes){
            component.appendChild(n.getXML(doc));
        }
        return component;
    }



    public static Component loadFromXML(Element n) {
        return null; //TODO
    }

}
