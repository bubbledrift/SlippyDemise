package engine.AILibrary.BehaviorTree.Nodes;

import engine.AILibrary.BehaviorTree.BTNode;
import engine.AILibrary.BehaviorTree.BTNodeStatus;

public abstract class BTActionNode implements BTNode {

    public BTActionNode(){}

    @Override
    public abstract BTNodeStatus update(double seconds);

}
