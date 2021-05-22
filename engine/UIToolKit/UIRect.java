package engine.UIToolKit;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class UIRect extends UIElement{

    protected Color color;

    public UIRect(Vec2d position, Vec2d size, Color color) {
        super(position, size);

        this.color = color;
    }

    /**
     *  Called periodically and meant to draw graphical components.
     * @param g		a {@link GraphicsContext} object used for drawing.
     */
    @Override
    public void onDraw(GraphicsContext g) {
        Vec2d pos = this.getOffset();

        g.setFill(this.color);
        g.fillRect(pos.x,pos.y,this.size.x,this.size.y);
        super.onDraw(g);
    }
}
