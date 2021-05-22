package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import javafx.scene.input.KeyEvent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class KeyPressedComponent extends Component{

    public interface OnKeyFunction{
        void onPress(GameObject gameObject, KeyEvent e);
    }

    private OnKeyFunction onKeyFunction;

    public KeyPressedComponent() {
        super();
    }

    public void linkKeyCallback(OnKeyFunction onKeyFunction){
        this.onKeyFunction = onKeyFunction;
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if(this.onKeyFunction != null)
           this.onKeyFunction.onPress(this.gameObject, e);
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.KeyEventSystem;
    }

    @Override
    public String getTag() {
        return "KeyPressedComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("onKeyFunction", this.onKeyFunction.getClass().getName());
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        KeyPressedComponent c = new KeyPressedComponent();
        c.NOT_FULLY_LOADED();
        return c;
    }
}

