package engine.game.components;

import engine.game.systems.SystemFlag;

public class BooleanComponent extends Component {

    private boolean bool;
    //Add string name if need be.

    public BooleanComponent(boolean bool) {
        super();
        this.bool = bool;
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.None;
    }

    @Override
    public String getTag() {
        return "BooleanComponent";
    }

    public boolean getBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }
}
