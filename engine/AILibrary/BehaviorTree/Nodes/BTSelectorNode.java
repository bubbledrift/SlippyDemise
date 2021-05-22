package engine.AILibrary.BehaviorTree.Nodes;

import engine.AILibrary.BehaviorTree.BTCompositeNode;
import engine.AILibrary.BehaviorTree.BTNodeStatus;

public class BTSelectorNode extends BTCompositeNode{

    @Override
    public BTNodeStatus update(double seconds) {
        BTNodeStatus status = BTNodeStatus.FAIL;
        int currentChild = 0;
        while(status == BTNodeStatus.FAIL && currentChild < this.children.size()){
            status = this.children.get(currentChild).update(seconds);
            currentChild++;
        }
        //reset other nodes
        while( currentChild < this.children.size()){
            this.children.get(currentChild).reset();
            currentChild++;
        }
        return status;
    }
}
