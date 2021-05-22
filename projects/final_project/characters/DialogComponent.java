package projects.final_project.characters;

import engine.game.components.TextBoxComponent;
import engine.game.components.screenEffects.ScreenEffectComponent;
import engine.game.systems.SystemFlag;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DialogComponent extends ScreenEffectComponent {



    private DialogNode dialog;
    private DialogNode currentNode;

    private TextBoxComponent text;
    private TextBoxComponent options;

    private int selection = 0;

    private double currentTime = -1;
    private boolean isOpen = false;

    public DialogComponent(DialogNode dialog, TextBoxComponent text, TextBoxComponent options){
        this.dialog = dialog;
        this.text = text;
        this.options = options;
    }

    public void startDialog(){
        if(this.isOpen) return;
        this.currentNode = this.dialog;
        this.text.open();
        this.text.setText(this.currentNode.text);
        this.options.open();
        this.currentTime = this.currentNode.time;
        isOpen = true;
    }

    public void endDialog(){
        if(!this.isOpen) return;
        this.text.close();
        this.options.close();
        this.currentTime = -1;
        isOpen = false;
    }

    private void transition(){
        if(this.currentNode.nextNodes == null){
            this.endDialog();
            return;
        }

        if(this.currentNode.options == null){
            this.currentNode = this.currentNode.nextNodes[0];
        } else {
            this.currentNode = this.currentNode.nextNodes[selection];
        }
        if(this.currentNode == null){
            this.endDialog();
        } else {
            this.text.rewrite(this.currentNode.text);
            this.options.rewrite(this.getOptionsText());
            this.currentTime = this.currentNode.time;
        }
    }

    private String getOptionsText(){
        if(this.currentNode.options == null){
            return "";
        }
        String r = "";
        for(int i = 0; i < this.currentNode.options.length; i++){
            r += i == this.selection ? "*" : "  ";
            r += this.currentNode.options[i];
            r += i == this.selection ? "*" : "  ";
            if(i != this.currentNode.options.length-1){
                r += "\n";
            }
        }
        return r;
    }

    @Override
    public void preEffect(GraphicsContext g) {}

    @Override
    public void postEffect(GraphicsContext g) {
        if(!this.isOpen) return;
        this.text.onDraw(g);
        if(this.currentNode.options != null) {
            this.options.onDraw(g);
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if(this.currentNode == null) return;
        if(e.getCode() == KeyCode.E || e.getCode() == KeyCode.ENTER){
            this.transition();
        }
        if(e.getCode() == KeyCode.ALT){
            this.endDialog();
        }
        if(e.getCode() == KeyCode.I | e.getCode() == KeyCode.UP){
            this.selection = (this.selection + 1) % this.currentNode.nextNodes.length;
            this.options.rewriteInstant(getOptionsText());
        }
        if(e.getCode() == KeyCode.K | e.getCode() == KeyCode.DOWN){
            this.selection --;
            if(this.selection < 0) this.selection = this.currentNode.nextNodes.length - 1;
            this.options.rewriteInstant(getOptionsText());
        }
    }

    @Override
    public void onTick(long nanosSincePreviousTick){
        if(this.gameObject != null){
            this.text.setGameObject(this.gameObject);
            this.options.setGameObject(this.gameObject);
        }
        this.text.onTick(nanosSincePreviousTick);
        this.options.onTick(nanosSincePreviousTick);

        if(this.currentTime == -1) return;

        this.currentTime -= nanosSincePreviousTick/1000000000.0;

        if (this.currentTime < 0){
            this.transition();
        }
    }

    @Override
    public int getSystemFlags() {
        return SystemFlag.ScreenEffectSystem | SystemFlag.TickSystem | SystemFlag.KeyEventSystem;
    }


    @Override
    public String getTag() {
        return "DialogComponent";
    }
}
