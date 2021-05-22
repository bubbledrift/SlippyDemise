package engine.game.components;

import engine.AILibrary.BehaviorTree.BTNode;
import engine.game.GameObject;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class BehaviorTreeComponent extends Component{

    private BTNode root;

    public BehaviorTreeComponent(BTNode root) {
        super();
        this.root = root;
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        if(root == null) return;
        root.update((double)(nanosSincePreviousTick)/ 1000000000.0);
    }

    @Override
    public void onLateTick(){};

    @Override
    public int getSystemFlags() {
        return SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "BehaviorTreeComponent";
    }


    public static Component loadFromXML(Element n) {
        return new BehaviorTreeComponent(null);
    }
}
