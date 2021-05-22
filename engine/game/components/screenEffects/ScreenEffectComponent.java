package engine.game.components.screenEffects;

import engine.game.components.Component;
import engine.game.systems.SystemFlag;
import javafx.scene.canvas.GraphicsContext;

public abstract class ScreenEffectComponent extends Component {

    public abstract void preEffect(GraphicsContext g);

    public abstract void postEffect(GraphicsContext g);

    @Override
    public int getSystemFlags() {
        return SystemFlag.ScreenEffectSystem;
    }

    @Override
    public abstract String getTag();
}
