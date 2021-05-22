package engine;

import engine.game.GameWorld;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import engine.UIToolKit.UIElement;

public class Screen {

    public UIElement root;

    public Screen(){
        this.root = new UIElement(new Vec2d(0,0), new Vec2d(0,0));
    }

    public void addUIElement(UIElement element){
        this.root.addChild(element);
    }

    /**
     * Called periodically and used to update the state of your game.
     * @param nanosSincePreviousTick	approximate number of nanoseconds since the previous call
     */
    protected void onTick(long nanosSincePreviousTick) {
        this.root.onTick(nanosSincePreviousTick);
    }

    /**
     * Called after onTick().
     */
    protected void onLateTick() {
        this.root.onLateTick();
    }

    /**
     *  Called periodically and meant to draw graphical components.
     * @param g		a {@link GraphicsContext} object used for drawing.
     */
    protected void onDraw(GraphicsContext g) {
        this.root.onDraw(g);
    }

    /**
     * Called when a key is typed.
     * @param e		an FX {@link KeyEvent} representing the input event.
     */
    protected void onKeyTyped(KeyEvent e) {
        this.root.onKeyTyped(e);
    }

    /**
     * Called when a key is pressed.
     * @param e		an FX {@link KeyEvent} representing the input event.
     */
    protected void onKeyPressed(KeyEvent e) {
        this.root.onKeyPressed(e);
    }

    /**
     * Called when a key is released.
     * @param e		an FX {@link KeyEvent} representing the input event.
     */
    protected void onKeyReleased(KeyEvent e) {
        this.root.onKeyReleased(e);
    }

    /**
     * Called when the mouse is clicked.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    protected void onMouseClicked(MouseEvent e, Vec2d shift) { this.root.onMouseClicked(e, shift); }

    /**
     * Called when the mouse is pressed.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    protected void onMousePressed(MouseEvent e, Vec2d shift) {
        this.root.onMousePressed(e, shift);
    }

    /**
     * Called when the mouse is released.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    protected void onMouseReleased(MouseEvent e, Vec2d shift) {
        this.root.onMouseReleased(e, shift);
    }

    /**
     * Called when the mouse is dragged.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    protected void onMouseDragged(MouseEvent e, Vec2d shift) {
        this.root.onMouseDragged(e, shift);
    }

    /**
     * Called when the mouse is moved.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    protected void onMouseMoved(MouseEvent e, Vec2d shift) {
        this.root.onMouseMoved(e, shift);
    }

    /**
     * Called when the mouse wheel is moved.
     * @param e		an FX {@link ScrollEvent} representing the input event.
     */
    protected void onMouseWheelMoved(ScrollEvent e, Vec2d shift) {
        this.root.onMouseWheelMoved(e, shift);
    }

    /**
     * Called when the window's focus is changed.
     * @param newVal	a boolean representing the new focus state
     */
    protected void onFocusChanged(boolean newVal) {
        this.root.onFocusChanged(newVal);
    }

    /**
     * Called when the window is resized.
     * @param scale	the new size of the drawing area.
     */
    protected void onResize(Vec2d scale) {
        this.root.onResize(scale);
    }


}
