package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LateRectComponent extends Component {

    private Color color;

    public LateRectComponent(Color color) {
        this.color = color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.RenderSystem;
    }

    @Override
    public String getTag() {
        return "LateRectComponent";
    }

    @Override
    public void onLateDraw(GraphicsContext g){

        Vec2d pos = this.gameObject.getTransform().position;
        Vec2d size = this.gameObject.getTransform().size;
        g.setFill(this.color);
        g.fillRect(pos.x,pos.y,size.x,size.y);
    };
}
