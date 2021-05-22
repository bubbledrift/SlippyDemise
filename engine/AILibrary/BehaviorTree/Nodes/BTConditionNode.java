package engine.AILibrary.BehaviorTree.Nodes;

import engine.AILibrary.BehaviorTree.BTNode;
import engine.AILibrary.BehaviorTree.BTNodeStatus;

public abstract class BTConditionNode implements BTNode {

    public BTConditionNode(){}

    @Override
    public BTNodeStatus update(double seconds) {
        return this.checkCondition(seconds) ? BTNodeStatus.SUCCESS : BTNodeStatus.FAIL;
    }

    @Override
    public void reset() {}

    /**
     * Return FAIL if condition is false and  SUCCESS if condition is true
     * @return BTNodeStatus.FAIL or BTNodeStatus.SUCCESS
     */
    public abstract boolean checkCondition(double seconds);

}
