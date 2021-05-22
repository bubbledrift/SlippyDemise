package engine.game;

import engine.UIToolKit.UIViewport;
import engine.game.components.Component;
import engine.game.components.TransformComponent;
import engine.game.systems.*;

import engine.support.Vec2d;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class GameWorld {

    //TODO handle inputs faster than ticks.
    private Set<KeyCode> keyState;
    private Vec2d mousePosition = new Vec2d(0,0);
    private boolean mouseDown = false;

    protected TickSystem tickSystem = new TickSystem();
    protected RenderSystem renderSystem = new RenderSystem();
    protected CollisionSystem collisionSystem = new CollisionSystem();
    protected KeyEventSystem keyEventSystem = new KeyEventSystem();
    protected MouseEventSystem mouseEventSystem = new MouseEventSystem();
    protected LightingSystem lightingSystem = new LightingSystem();
    protected ScreenEffectSystem screenEffectSystem = new ScreenEffectSystem();

    private GeneralSystem[] systemsList = {tickSystem, renderSystem, collisionSystem, keyEventSystem, mouseEventSystem
            ,lightingSystem, screenEffectSystem};

    private List<GameObject> gameObjects;
    private List<GameObject> addQueue;
    private List<GameObject> removeQueue;

    private Map<Integer, UIViewport> viewportMap = new HashMap<Integer, UIViewport>();

    private boolean imageSmoothing = false;

    //Root GameObject in gameworld. Can be used to add global components
    private GameObject root;

    //Only one region can be loaded at a time
    private Region region = null;

    public GameWorld(){
        this.keyState = new HashSet<KeyCode>();
        this.gameObjects = new ArrayList<GameObject>();

        this.addQueue = new ArrayList<GameObject>();
        this.removeQueue = new ArrayList<GameObject>();

        this.root = new GameObject(this,-9999);//arbitrary negative layer
        this.addGameObject(this.root);
    }

    public void setImageSmoothing(boolean imageSmoothing){
        this.imageSmoothing = imageSmoothing;
    }

    public void linkViewport(int id, UIViewport viewport){
        this.viewportMap.put(id,viewport);
    }

    public UIViewport getViewport(int id){
        return this.viewportMap.get(id);
    }

    public void onTick(long nanosSincePreviousTick) {
        processQueues();
        this.tickSystem.onTick(nanosSincePreviousTick);
        this.collisionSystem.onTick(nanosSincePreviousTick);
        this.screenEffectSystem.onTick(nanosSincePreviousTick);
    }

    public void processQueues(){
        while(!addQueue.isEmpty()) {
            this.processGameObject(this.addQueue.remove(0));
        }
        while(!removeQueue.isEmpty()) {
            this.deprocessGameObject(this.removeQueue.remove(0));
        }
    }

    public void onLateTick(){
        this.tickSystem.onLateTick();
        this.collisionSystem.onLateTick();
    }

    public void onDraw(GraphicsContext g) {
        g.setImageSmoothing(this.imageSmoothing);

        this.screenEffectSystem.preEffect(g);
        this.renderSystem.onDraw(g);
        this.renderSystem.onLateDraw(g);
        this.screenEffectSystem.postEffect(g);
    }


    public void addGameObject(GameObject gameObject){
        this.addQueue.add(gameObject);
        if(this.region != null){
            this.region.addGameObject(gameObject);
        }
    }
    public void removeGameObject(GameObject gameObject){
        this.removeQueue.add(gameObject);
        if(this.region != null){
            this.region.removeGameObject(gameObject);
        }
    }

    public void addGameObjectToRootWorld(GameObject gameObject){
        this.addQueue.add(gameObject);

    }
    public void removeGameObjectFromRootWorld(GameObject gameObject){
        this.removeQueue.add(gameObject);
    }

    public void loadRegion(Region region){
        if(this.region != null){//Only one region can be loaded at a time
            this.unloadRegion();
        }
        this.region = region;
        this.addQueue.addAll(region.getGameObjects());
    }

    public void unloadRegion(){
        if(this.region == null) return; //nothing to unload
        this.removeQueue.addAll(this.region.getGameObjects());
        this.region = null;
    }

    public GameObject getRoot(){
        return root;
    }

    private void processGameObject(GameObject gameObject){
        this.gameObjects.add(gameObject);
        for(Component c : gameObject.componentList) {
            int flags = c.getSystemFlags();
            for(GeneralSystem system : systemsList){
                if ((flags & system.getSystemFlag()) != 0) {
                    system.addComponent(c);
                }
            }
        }
        gameObject.setLOADED_INTO_GAMEWORLD(true);
    }

    private void deprocessGameObject(GameObject gameObject){
        for(Component c : gameObject.componentList) {
            int flags = c.getSystemFlags();
            for(GeneralSystem system : systemsList){
                if ((flags & system.getSystemFlag()) != 0) {
                    system.removeComponent(c);
                }
            }
        }
        this.gameObjects.remove(gameObject);
        gameObject.setLOADED_INTO_GAMEWORLD(false);
    }

    public void processComponent(Component component){
        int flags = component.getSystemFlags();
        for(GeneralSystem system : systemsList){
            if ((flags & system.getSystemFlag()) != 0 && !system.hasComponent(component)) {
                system.addComponent(component);
            }
        }
    }

    public void deprocessComponet(Component component){
        int flags = component.getSystemFlags();
        for(GeneralSystem system : systemsList){
            if ((flags & system.getSystemFlag()) != 0 && system.hasComponent(component)) {
                system.removeComponent(component);
            }
        }
    }

    public List<GameObject> getGameObjects(){
        return this.gameObjects;
    }

    public void clearAllGameObjects(){
        for (GameObject o : this.gameObjects) {
            this.removeQueue.add(o);
        }
        for (GameObject o : this.removeQueue) {
            this.deprocessGameObject(o);
        }
        this.removeQueue.clear();
        this.gameObjects.clear();
    }

    public Set<KeyCode> getKeyState(){
        return this.keyState;
    }

    public Vec2d getMousePosition(){
        return this.mousePosition;
    }

    public Boolean getMouseDown(){
        return this.mouseDown;
    }

    /**
     * Called when a key is typed.
     * @param e		an FX {@link KeyEvent} representing the input event.
     */
    public void onKeyTyped(KeyEvent e) {
        keyEventSystem.onKeyTyped(e);
    }

    /**
     * Called when a key is pressed.
     * @param e		an FX {@link KeyEvent} representing the input event.
     */
    public void onKeyPressed(KeyEvent e) {
        this.keyState.add(e.getCode());
        keyEventSystem.onKeyPressed(e);
    }

    /**
     * Called when a key is released.
     * @param e		an FX {@link KeyEvent} representing the input event.
     */
    public void onKeyReleased(KeyEvent e) {
        this.keyState.remove(e.getCode());
        keyEventSystem.onKeyReleased(e);
    }

    /**
     * Called when the mouse is clicked.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    public void onMouseClicked(Vec2d e) {
        mouseEventSystem.onMouseClicked(e);
    }

    /**
     * Called when the mouse is pressed.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    public void onMousePressed(Vec2d e) {
        this.mouseDown = true;
        this.mousePosition = e;
        mouseEventSystem.onMousePressed(e);
    }

    /**
     * Called when the mouse is released.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    public void onMouseReleased(Vec2d e) {
        this.mouseDown = false;
        this.mousePosition = e;
        mouseEventSystem.onMouseReleased(e);
    }

    /**
     * Called when the mouse is dragged.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    public void onMouseDragged(Vec2d e) {
        this.mousePosition = e;
        mouseEventSystem.onMouseDragged(e);
    }

    /**
     * Called when the mouse is moved.
     * @param e		an FX {@link MouseEvent} representing the input event.
     */
    public void onMouseMoved(Vec2d e) {
        this.mousePosition = e;
        mouseEventSystem.onMouseMoved(e);
    }

    /**
     * Called when the mouse wheel is moved.
     * @param e		an FX {@link ScrollEvent} representing the input event.
     */
    public void onMouseWheelMoved(ScrollEvent e) {
        mouseEventSystem.onMouseWheelMoved(e);
    }



    public void saveToXMLFile(String path){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            //add elements to Document

            Element rootElement = doc.createElement("GameWorld");

            for(GameObject g :this.gameObjects){
                rootElement.appendChild(g.getXML(doc));
            }

            doc.appendChild(rootElement);
            //for output to file, console
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult file = new StreamResult(new File(path));
            transformer.transform(source, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface CallBack{
        void callBack(Component c);
    }

    public void loadFromXML(String path, CallBack callBack){
        this.clearAllGameObjects();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        List<Component> partiallyLoadedComponents = new ArrayList<Component>();

        try {
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.parse(path);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("GameObject");
            for(int i = 0; i < nList.getLength(); i++){
                Node n = nList.item(i);
                GameObject g = new GameObject(this,Integer.parseInt(n.getAttributes().getNamedItem("layer").getNodeValue()));
                this.addGameObject(g);

                NodeList childList = n.getChildNodes();
                for(int j = 0; j < childList.getLength(); j++){
                    Node component = childList.item(j);
                    if(component.getNodeType() != Node.ELEMENT_NODE) continue;
                    try {
                        Element element = (Element) component;
                        Component c = Component.getComponentFromXML(element);
                        if(c.NOT_FULLY_LOADED){
                            partiallyLoadedComponents.add(c);
                        }
                        if (element.getNodeName().endsWith("TransformComponent")) {
                            g.setTransform((TransformComponent) c);
                        } else {
                            g.addComponent(c);
                        }
                    }  catch (ClassNotFoundException e) {
                        System.err.println("Failed to find component for node:" + component.toString());
                        e.printStackTrace();
                    } catch(NoSuchMethodException e){
                        System.err.println("Component does not have loading implemented" + component.toString());
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        System.err.println(component.getNodeName());
                        e.printStackTrace();
                    }
                }

            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        for(Component c : partiallyLoadedComponents){
            callBack.callBack(c);
            c.NOT_FULLY_LOADED = false;
        }


    }

    public LightingSystem getLightingSystem() {
        return lightingSystem;
    }
}
