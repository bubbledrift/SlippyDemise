package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.util.Set;

public class WASDMovementComponent extends Component{

    private double speed; // per second

    public WASDMovementComponent(double speed) {
        super();
        this.speed = speed;
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        double dx = 0;
        double dy = 0;
        double dt = nanosSincePreviousTick/1000000000.0; //seconds since last tick

        Set<KeyCode> keyState = this.gameObject.gameWorld.getKeyState();
        if(keyState.contains(KeyCode.W))
            dy += speed * dt;
        if(keyState.contains(KeyCode.A))
            dx += speed * dt;
        if(keyState.contains(KeyCode.S))
            dy -= speed * dt;
        if(keyState.contains(KeyCode.D))
            dx -= speed * dt;

        Vec2d pos = this.gameObject.getTransform().position;
        this.gameObject.getTransform().position = new Vec2d(pos.x - dx, pos.y - dy);
        System.out.println(this.gameObject.getTransform().position);
    }
    @Override
    public void onLateTick(){};


    @Override
    public int getSystemFlags() {
        return SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "WASDMovementComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("speed", Double.toString(speed));
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        Double speed = Double.parseDouble(attr.getNamedItem("speed").getNodeValue());
        WASDMovementComponent c = new WASDMovementComponent(speed);
        return c;
    }
}
