package engine.game.components;

import engine.game.GameObject;
import engine.game.systems.SystemFlag;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class TimerComponent extends Component{

    public interface OnTimerFunction{
        void onTimer(GameObject gameObject);
    }

    private OnTimerFunction onTimerFunction;
    private double cycleTime;

    private double time = 0; //variable used for clock

    public TimerComponent(double seconds) {
        super();
        this.cycleTime = seconds;
    }

    public void linkTimerCallback(OnTimerFunction onTimerFunction){
        this.onTimerFunction = onTimerFunction;
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        this.time -= nanosSincePreviousTick/1000000000.0;
        while(this.time < 0){
            this.time += this.cycleTime;
            if(this.onTimerFunction != null)
                this.onTimerFunction.onTimer(gameObject);
        }
    }

    @Override
    public void onLateTick(){};

    @Override
    public int getSystemFlags() {
        return SystemFlag.TickSystem;
    }

    @Override
    public String getTag() {
        return "TimerComponent";
    }

    public Element getXML(Document doc){
        Element component = doc.createElement(this.getClass().getName());
        component.setAttribute("cycleTime", Double.toString(cycleTime));
        component.setAttribute("time", Double.toString(time));
        return component;
    }

    public static Component loadFromXML(Element n) {
        NamedNodeMap attr = n.getAttributes();
        double cycleTime = Double.parseDouble(attr.getNamedItem("cycleTime").getNodeValue());
        double time = Double.parseDouble(attr.getNamedItem("time").getNodeValue());
        TimerComponent c = new TimerComponent(cycleTime);
        c.time = time;
        return c;
    }
}
