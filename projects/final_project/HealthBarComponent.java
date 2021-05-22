package projects.final_project;

import engine.game.components.Component;
import engine.game.components.HealthComponent;
import engine.game.systems.SystemFlag;
import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HealthBarComponent extends Component {

    private Color color;
    private Vec2d position, size;

    private HealthComponent healthComponent;

    private boolean center;

    public HealthBarComponent(Color color, Vec2d position, Vec2d size, HealthComponent healthComponent, boolean center){
        this.color = color;
        this.healthComponent = healthComponent;
        this.position = position;
        this.size = size;
        this.center = center;
    }

    @Override
    public void onDraw(GraphicsContext g){
        Vec2d pos = this.gameObject.getTransform().position.plus(this.position);
        Vec2d size = this.size;
        g.setFill(this.color);
        double W = size.x * this.healthComponent.getHealthRatio();
        if(this.center){
            g.fillRect(pos.x - W/2, pos.y-size.y/2, W, size.y);
        } else {
            g.fillRect(pos.x, pos.y, W, size.y);
        }
    };

    @Override
    public int getSystemFlags() {
        return SystemFlag.RenderSystem;
    }

    @Override
    public String getTag() {
        return "HealthBarComponent";
    }
}
