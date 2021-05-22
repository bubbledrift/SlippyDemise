package engine;

import engine.support.FXFrontEnd;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;


import java.util.Map;
import java.util.HashMap;

/**
 * This is your main Application class that you will contain your
 * 'draws' and 'ticks'. This class is also used for controlling
 * user input.
 */
public class Application extends FXFrontEnd {


  //TODO make screen handler
  protected Map<String, Screen> screens;
  protected Screen currentScreen;
  protected String currentScreenTag;

  protected Vec2d originalStageSize;

  protected Vec2d trueSize; //doesn't count the block bars used to match aspect ratio

  private Vec2d shift = new Vec2d(0,0);

  public Application(String title) {
    super(title);
    this.originalStageSize = this.currentStageSize;
  }

  public Application(String title, Vec2d windowSize, boolean debugMode, boolean fullscreen) {
    super(title, windowSize, debugMode, fullscreen);
    this.originalStageSize = this.currentStageSize;
  }

  public Vec2d getTrueSize(){
    return this.trueSize;
  }

  public void addScreen(Screen screen, String tag){
    this.screens.put(tag,screen);
    if(currentScreen == null){
      this.currentScreen = screen;
      this.currentScreenTag = tag;
    }
    this.onResize(this.currentStageSize);
  }

  public void setCurrentScreen(String tag){
    if(this.screens.get(tag) == null) return;
    this.currentScreen = this.screens.get(tag);
    this.currentScreenTag = tag;
    this.onResize(this.currentStageSize);
  }

  public String getCurrentScreenTag(){
    return this.currentScreenTag;
  }

  /**
   * Called periodically and used to update the state of your game.
   * @param nanosSincePreviousTick	approximate number of nanoseconds since the previous call
   */
  @Override
  protected void onTick(long nanosSincePreviousTick) {
    this.currentScreen.onTick(nanosSincePreviousTick);
  }

  /**
   * Called after onTick().
   */
  @Override
  protected void onLateTick() {
    this.currentScreen.onLateTick();
  }

  /**
   *  Called periodically and meant to draw graphical components.
   * @param g		a {@link GraphicsContext} object used for drawing.
   */
  @Override
  protected void onDraw(GraphicsContext g) {
    //TODO Center Screen in Window
    //Needs to fix mouse position handling



    g.translate(this.shift.x,this.shift.y);
    this.currentScreen.onDraw(g);
    g.translate(-this.shift.x,-this.shift.y);

    g.setFill(Color.BLACK);
    g.fillRect(0,0, shift.x,this.currentStageSize.y);
    g.fillRect(this.currentStageSize.x-shift.x,0, shift.x,this.currentStageSize.y);
    g.fillRect(0,0, this.currentStageSize.x,shift.y);
    g.fillRect(0,this.currentStageSize.y-shift.y, this.currentStageSize.x,shift.y);


  }

  /**
   * Called when a key is typed.
   * @param e		an FX {@link KeyEvent} representing the input event.
   */
  @Override
  protected void onKeyTyped(KeyEvent e) {
    this.currentScreen.onKeyTyped(e);
  }

  /**
   * Called when a key is pressed.
   * @param e		an FX {@link KeyEvent} representing the input event.
   */
  @Override
  protected void onKeyPressed(KeyEvent e) {
    this.currentScreen.onKeyPressed(e);
  }

  /**
   * Called when a key is released.
   * @param e		an FX {@link KeyEvent} representing the input event.
   */
  @Override
  protected void onKeyReleased(KeyEvent e) {
    this.currentScreen.onKeyReleased(e);
  }

  /**
   * Called when the mouse is clicked.
   * @param e		an FX {@link MouseEvent} representing the input event.
   */
  @Override
  protected void onMouseClicked(MouseEvent e) {
    this.currentScreen.onMouseClicked(e, this.shift.smult(-1));
  }

  /**
   * Called when the mouse is pressed.
   * @param e		an FX {@link MouseEvent} representing the input event.
   */
  @Override
  protected void onMousePressed(MouseEvent e) {
    this.currentScreen.onMousePressed(e, this.shift.smult(-1));
  }

  /**
   * Called when the mouse is released.
   * @param e		an FX {@link MouseEvent} representing the input event.
   */
  @Override
  protected void onMouseReleased(MouseEvent e) {
    this.currentScreen.onMouseReleased(e, this.shift.smult(-1));
  }

  /**
   * Called when the mouse is dragged.
   * @param e		an FX {@link MouseEvent} representing the input event.
   */
  @Override
  protected void onMouseDragged(MouseEvent e) {
    this.currentScreen.onMouseDragged(e, this.shift.smult(-1));
  }

  /**
   * Called when the mouse is moved.
   * @param e		an FX {@link MouseEvent} representing the input event.
   */
  @Override
  protected void onMouseMoved(MouseEvent e) {
    this.currentScreen.onMouseMoved(e, this.shift.smult(-1));
  }

  /**
   * Called when the mouse wheel is moved.
   * @param e		an FX {@link ScrollEvent} representing the input event.
   */
  @Override
  protected void onMouseWheelMoved(ScrollEvent e) {
    this.currentScreen.onMouseWheelMoved(e, this.shift.smult(-1));
  }

  /**
   * Called when the window's focus is changed.
   * @param newVal	a boolean representing the new focus state
   */
  @Override
  protected void onFocusChanged(boolean newVal) {
    this.currentScreen.onFocusChanged(newVal);
  }

  /**
   * Called when the window is resized.
   * @param newSize	the new size of the drawing area.
   */
  @Override
  protected void onResize(Vec2d newSize) {
    currentStageSize = newSize;
    double scale = Math.min(newSize.x / this.originalStageSize.x, newSize.y / this.originalStageSize.y);
    currentScreen.onResize(new Vec2d(scale, scale));


    double true_width = this.originalStageSize.x * scale;
    double true_height = this.originalStageSize.y * scale;
    this.trueSize = new Vec2d(true_width,true_height);

    double shift_x = 0;
    double shift_y = 0;
    if(true_width < this.currentStageSize.x){
      //actual window too wide
      shift_x = (this.currentStageSize.x - true_width)/2;
    } else {
      //actual window too tall
      shift_y = (this.currentStageSize.y - true_height)/2;
    }
    this.shift = new Vec2d(shift_x, shift_y);


  }

  /**
   * Called when the app is shutdown.
   */
  @Override
  protected void onShutdown() {

  }

  /**
   * Called when the app is starting up.s
   */
  @Override
  protected void onStartup() {
    this.screens = new HashMap<String,Screen>();
  }

}
