package engine.AILibrary.BehaviorTree.Nodes;

import engine.AILibrary.BehaviorTree.*;

public class BTSequenceNode extends BTCompositeNode {

    @Override
    public BTNodeStatus update(double seconds) {
        BTNodeStatus status = BTNodeStatus.SUCCESS;
        for(int i = lastRunning; i < this.children.size(); i++){
            status = this.children.get(i).update(seconds);
            if(status != BTNodeStatus.SUCCESS){
                lastRunning = i;
                return status;
            }
        }
        return status;
    }
}
