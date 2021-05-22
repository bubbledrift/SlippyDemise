package engine.UIToolKit;

import engine.support.FontMetrics;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javafx.scene.text.TextAlignment;

public class UIButton extends UIElement{

    //TODO alignment options, center, left, right, top, bottom
    //TODO maybe better to have option for additional child

    protected Color backgroundColor;
    protected Color borderColor;

    private boolean hovered = false;
    protected Vec2d hoverScale = new Vec2d(.9, .9);

    protected MouseClickedFunction mouseClickedFunction = () -> {return;};


    public UIButton(Vec2d position, Vec2d size, Color backgroundColor, Color borderColor) {
        super(position, size);
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
    }

    public UIButton(Vec2d position, Vec2d size, Color backgroundColor, Color borderColor, Vec2d hoverScale) {
        super(position, size);
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;

        this.hoverScale = hoverScale;
    }


    /**
     *  Called periodically and meant to draw graphical components.
     * @param g		a {@link GraphicsContext} object used for drawing.
     */
    @Override
    public void onDraw(GraphicsContext g) {
        Vec2d pos = this.getOffset();
        if(this.hovered){
            this.onDrawHovered(g, pos);
        } else {
            this.onDrawRegular(g, pos);
        }
        super.onDraw(g);
    }

    /**
     * Called to draw regular button state
     * @param g     a {@link GraphicsContext} object used for drawing.
     * @param pos   absolute position of button on screen. Used for drawing
     */
    public void onDrawRegular(GraphicsContext g, Vec2d pos){
        g.setFill(this.backgroundColor);
        g.fillRect(pos.x, pos.y, this.size.x, this.size.y);
    }

    /**
     * Called to draw button state when hovered over by the mouse
     * @param g     a {@link GraphicsContext} object used for drawing.
     * @param pos   absolute position of button on screen. Used for drawing
     */
    public void onDrawHovered(GraphicsContext g, Vec2d pos){
        g.setFill(this.borderColor);
        g.fillRect(pos.x, pos.y, this.size.x, this.size.y);

        double shift = Math.min (this.size.x - this.size.x * this.hoverScale.x, this.size.y - this.size.y * this.hoverScale.y);
        g.setFill(this.backgroundColor);
        g.fillRect(pos.x + shift/2, pos.y + shift/2, this.size.x - shift, this.size.y -shift);
    }

    /**
     * Called when the mouse is moved.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    @Override
    public void onMouseMoved(MouseEvent e, Vec2d shift) {
        super.onMouseMoved(e, shift);
        this.hovered = this.mouseInBounds(e,shift);
    }

    @Override
    public void onMouseClicked(MouseEvent e, Vec2d shift) {
        super.onMouseClicked(e,shift);
        if(this.mouseInBounds(e,shift)){
            this.mouseClickedFunction.onClick();
        }
    }

    public void setOnMouseClicked(MouseClickedFunction m){
        this.mouseClickedFunction = m;
    }

    public interface MouseClickedFunction{
        void onClick();
    }
}
