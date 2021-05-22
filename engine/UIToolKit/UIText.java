package engine.UIToolKit;

import engine.support.FontMetrics;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.paint.*;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


public class UIText extends UIElement{

    protected String text;
    protected Font font;
    private FontMetrics fontMetrics;

    //TODO alignment options, center, left, right, top, bottom
    private boolean centered = false;

    //TODO text color options: filled versus stroke
    protected Color textColor;

    public UIText(Vec2d position, Vec2d size, String text, Color textColor) {
        super(position, size);

        this.text = text;
        this.font = Font.getDefault();
        this.fontMetrics = new FontMetrics(this.text);

        this.textColor = textColor;
    }

    public UIText(Vec2d position, Vec2d size, String text, Color textColor, Font font) {
        super(position, size);

        this.text = text;
        this.font = font;
        this.fontMetrics = new FontMetrics(this.text);

        this.textColor = textColor;
    }
    public UIText(Vec2d position, Vec2d size, boolean centered, String text, Color textColor, Font font) {
        super(position, size);
        this.centered = centered;

        this.text = text;
        this.font = font;

        this.fontMetrics = new FontMetrics(this.text);

        this.textColor = textColor;
    }


    /**
     *  Called periodically and meant to draw graphical components.
     * @param g		a {@link GraphicsContext} object used for drawing.
     */
    @Override
    public void onDraw(GraphicsContext g) {
        Vec2d pos = this.getOffset();

        if(this.centered) {
            g.setTextAlign(TextAlignment.CENTER);
        } else {
            g.setTextAlign(TextAlignment.LEFT);
        }
        g.setFill(this.textColor);
        g.setFont(this.font);
        g.fillText(this.text,pos.x,pos.y);
    }
}
