package engine.AILibrary.BehaviorTree;

import java.util.LinkedList;
import java.util.List;

public abstract class BTCompositeNode implements BTNode {
    protected List<BTNode> children;
    protected int lastRunning;

    public BTCompositeNode(){
        this.children = new LinkedList<BTNode>();
        lastRunning = 0;
    }

    @Override
    public void reset(){
        this.lastRunning = 0; //start from first child
        for(BTNode child : children){
            child.reset();
        }
    }

    public void linkChild(BTNode n){
        this.children.add(n);
    }

    public void removeChild(BTNode n){
        this.children.remove(n);
    }

    public List<BTNode> getChildren(){
        return children;
    }
}
