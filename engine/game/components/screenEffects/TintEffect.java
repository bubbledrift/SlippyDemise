package engine.game.components.screenEffects;

import engine.UIToolKit.UIViewport;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TintEffect extends ScreenEffectComponent{


    private int viewport_id;
    private UIViewport viewport;

    private Color color;
    private double opacity;


    public TintEffect(int viewport_id, Color color, double opacity){
        this.viewport_id = viewport_id;
        this.color = color;
    }

    @Override
    public void preEffect(GraphicsContext g) {

    }

    @Override
    public void postEffect(GraphicsContext g) {
        if(this.viewport == null){
            this.viewport = this.gameObject.gameWorld.getViewport(this.viewport_id);
        }
        if(this.viewport != null){
            Vec2d corner = this.viewport.getGameWorldViewCorner();
            Vec2d size = this.viewport.getGameWorldViewSize();

            g.setFill(Color.rgb((int)(color.getRed()*255),(int)(color.getGreen()*255),(int)(color.getBlue()*255),opacity));
            g.fillRect(corner.x,corner.y, size.x, size.y);
        }
    }

    public void setTint(double opacity){
        this.opacity = opacity;
    }

    @Override
    public String getTag() {
        return "TintEffect";
    }
}
