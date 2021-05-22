package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;

public class ProximityComponent extends Component {

    public interface WithinRange{
        void withinRange(GameObject gameObject, double distance);
    }

    private WithinRange withinRange;
    private double distance, trigger;
    private GameObject other;

    public ProximityComponent(GameObject other, double trigger) {
        this.other = other;
        this.trigger = trigger;
    }

    public void linkProximityCallback(WithinRange withinRange){
        this.withinRange = withinRange;
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        distance = other.getTransform().position.dist(gameObject.getTransform().position);
        if(distance < trigger){
            if(withinRange != null)
                //System.out.println("player pos: " +other.getTransform().position);
                this.withinRange.withinRange(this.gameObject, distance);
        }
    }

    @Override
    public void onLateTick(){};

    @Override
    public int getSystemFlags() {
        return SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "ProximityComponent";
    }
}
