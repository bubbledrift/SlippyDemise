package engine.UIToolKit;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.ArrayList;
import java.util.List;

public class UIElement {

    protected UIElement parent = null;
    protected List<UIElement> children;

    public Vec2d position; //relative to parent UIElement
    public Vec2d size;

    //For rescaling need to remember original size
    protected Vec2d originalPosition;
    protected Vec2d originalSize;

    protected Vec2d scale = new Vec2d(1,1); //remember scale to immediately scale children

    protected boolean shouldScale = true; //should this element be scaled on resizes

    public UIElement(Vec2d position, Vec2d size){
        this.children = new ArrayList<>();

        this.position = position;
        this.size = size;

        this.originalPosition = position;
        this.originalSize = size;
    }

    public void addChild(UIElement child){
        child.parent = this;
        this.children.add(child);
        child.onResize(this.scale);
    }

    //TODO add chache-ing (for very nested UI)
    protected Vec2d getOffset(){
        if(this.parent == null){
            return new Vec2d(0,0);
        }
        Vec2d parentOffset = this.parent.getOffset();
        return new Vec2d(parentOffset.x + this.position.x,parentOffset.y + this.position.y);
    }


    //check if mouse in bounds of UIElement
    protected boolean mouseInBounds(MouseEvent e, Vec2d shift){
        Vec2d pos = this.getOffset(); // need absolute position for mouse

        if(e.getX() + shift.x < pos.x || pos.x + this.size.x < e.getX() + shift.x) return false;
        if(e.getY() + shift.y < pos.y || pos.y + this.size.y < e.getY() + shift.y) return false;
        return true;
    }

    protected boolean mouseInBounds(double x, double y){
        Vec2d pos = this.getOffset(); // need absolute position for mouse

        if(x < pos.x || pos.x + this.size.x < y) return false;
        if(x < pos.y || pos.y + this.size.y < y) return false;
        return true;
    }

    public void onTick(long nanosSincePreviousTick) {
        for(UIElement child : this.children){
            child.onTick(nanosSincePreviousTick);
        }
    }

    public void onLateTick() {
        for(UIElement child : this.children){
            child.onLateTick();
        }
    }

    public void onDraw(GraphicsContext g) {
        for(UIElement child : this.children){
            child.onDraw(g);
        }
    }

    /**
     * Called when a key is typed.
     * @param e		an FX {@link KeyEvent} representing the input event.
     */
    public void onKeyTyped(KeyEvent e) {
        for(UIElement child : this.children){
            child.onKeyTyped(e);
        }
    }

    /**
     * Called when a key is pressed.
     * @param e		an FX {@link KeyEvent} representing the input event.
     */
    public void onKeyPressed(KeyEvent e) {
        for(UIElement child : this.children){
            child.onKeyPressed(e);
        }
    }

    /**
     * Called when a key is released.
     * @param e		an FX {@link KeyEvent} representing the input event.
     */
    public void onKeyReleased(KeyEvent e) {
        for(UIElement child : this.children){
            child.onKeyReleased(e);
        }
    }

    /**
     * Called when the mouse is clicked.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    public void onMouseClicked(MouseEvent e, Vec2d shift) {
        for(UIElement child : this.children){
            child.onMouseClicked(e, shift);
        }
    }

    /**
     * Called when the mouse is pressed.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    public void onMousePressed(MouseEvent e, Vec2d shift) {
        for(UIElement child : this.children){
            child.onMousePressed(e, shift);
        }
    }

    /**
     * Called when the mouse is released.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    public void onMouseReleased(MouseEvent e, Vec2d shift) {
        for(UIElement child : this.children){
            child.onMouseReleased(e, shift);
        }
    }

    /**
     * Called when the mouse is dragged.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    public void onMouseDragged(MouseEvent e, Vec2d shift) {
        for(UIElement child : this.children){
            child.onMouseDragged(e, shift);
        }
    }

    /**
     * Called when the mouse is moved.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    public void onMouseMoved(MouseEvent e, Vec2d shift) {
        for(UIElement child : this.children){
            child.onMouseMoved(e, shift);
        }
    }

    /**
     * Called when the mouse wheel is moved.
     * @param e		an FX {@link ScrollEvent} representing the input event.
     */
    public void onMouseWheelMoved(ScrollEvent e, Vec2d shift) {
        for(UIElement child : this.children){
            child.onMouseWheelMoved(e, shift);
        }
    }

    /**
     * Called when the window's focus is changed.
     * @param newVal	a boolean representing the new focus state
     */
    public void onFocusChanged(boolean newVal) {
        for(UIElement child : this.children){
            child.onFocusChanged(newVal);
        }
    }

    /**
     * Called when the window is resized.
     * @param scale	the new size of the drawing area.
     */
    public void onResize(Vec2d scale) {
        this.scale = scale;
        this.position = new Vec2d(this.originalPosition.x * scale.x, this.originalPosition.y * scale.y);
        this.size = new Vec2d(this.originalSize.x * scale.x, this.originalSize.y * scale.y);

        for(UIElement child : this.children){
            child.onResize(scale);
        }
    }
}
