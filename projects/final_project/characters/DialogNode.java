package projects.final_project.characters;

public class DialogNode{
    public String text;
    public double time; //-1 for unlimited (until enter)
    public String[] options = null; //null if no options
    public DialogNode[] nextNodes = null; //if no options then just first element in array

    public DialogNode(String text){
        this.text = text;
        this.time = -1;
    }
    public DialogNode(String text, double time){
        this.text = text;
        this.time = time;
    }

    public void setNextNode(DialogNode node){
        this.nextNodes = new DialogNode[]{node};
    }

    public void setOptions(String[] options, DialogNode[] nodes){
        this.options = options;
        this.nextNodes = nodes;
    }
}