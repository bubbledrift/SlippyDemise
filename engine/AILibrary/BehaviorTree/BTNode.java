package engine.AILibrary.BehaviorTree;

public interface BTNode {
    public abstract BTNodeStatus update(double seconds);
    public abstract void reset();
}
