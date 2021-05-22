package projects.WizTesting;

import engine.game.GameWorld;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class WizGameWorld extends GameWorld {

    public WizGameWorld(){

    }

    @Override
    public void onDraw(GraphicsContext g) {

        /*ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(-0.1);
        colorAdjust.setHue(-0.1);
        colorAdjust.setBrightness(-0.7);
        colorAdjust.setSaturation(-0.2);

        g.beginPath();
        g.arc(0,0,20,20,0,360);
        g.clip();
        //Could I make an if statement?
        this.renderSystem.onDraw(g);


        g.setEffect(colorAdjust);
        g.beginPath();
        g.clip();*/



        this.renderSystem.onDraw(g);
        /*g.setFill(Color.rgb(0,0,0,0.8));
        g.fillRect(0,0,8,8);*/
        /*g.setFill(Color.BLACK);
        g.fillArc(0,0,20,20,0,360, ArcType.ROUND);*/
    }
}
