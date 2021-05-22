package engine.game.components;

import engine.UIToolKit.UIElement;
import engine.game.GameObject;
import engine.game.components.animation.AnimationField;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class Component {

    protected GameObject gameObject;

    public AnimationField<Boolean> disabled = new AnimationField<Boolean>(false);

    public boolean NOT_FULLY_LOADED = false;

    public void disable(){
        this.disabled.value = true;
    }

    public void enable(){
        this.disabled.value = false;
    }

    public boolean isDisabled(){
        return this.disabled.value;
    }

    public GameObject getGameObject(){
        return this.gameObject;
    }

    public void setGameObject(GameObject g){
        this.gameObject = g;
    }

    public double getRenderOrdering(){return this.gameObject.getTransform().position.y;}

    public abstract int getSystemFlags();

    public abstract String getTag();

    public void onTick(long nanosSincePreviousTick){};

    public void onLateTick(){};

    public void onDraw(GraphicsContext g){};

    public void onLateDraw(GraphicsContext g){};

    /*
     *
     * Key and Mouse events are passed down from GameObject
     */
    public void onKeyTyped(KeyEvent e) {}

    public void onKeyPressed(KeyEvent e) {}

    public void onKeyReleased(KeyEvent e) {}

    public void onMouseClicked(Vec2d e) {}

    public void onMousePressed(Vec2d e) {}

    public void onMouseReleased(Vec2d e) {}

    public void onMouseDragged(Vec2d e) {}

    public void onMouseMoved(Vec2d e) {}

    public void onMouseWheelMoved(ScrollEvent e) {}

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("class", this.getClass().getName());
        return component;
    }

    public static Component getComponentFromXML(Element n) throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        Class cls = Class.forName(n.getTagName());
        Class[] cArg = new Class[]{Element.class};
        Method m = cls.getMethod("loadFromXML", cArg);
        return (Component) m.invoke(null,n);
    }

    public void NOT_FULLY_LOADED(){
        this.NOT_FULLY_LOADED = true;
    }


}
