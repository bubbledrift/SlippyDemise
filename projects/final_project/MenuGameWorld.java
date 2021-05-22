package projects.final_project;

import engine.UIToolKit.UIViewport;
import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.AABShape;
import engine.game.components.*;
import engine.game.systems.CollisionSystem;
import engine.game.systems.SystemFlag;
import engine.game.tileSystem.TileMap;
import engine.support.Vec2d;
import projects.final_project.assets.sounds.AnimationGraphComponent;
import projects.final_project.characters.Goomba;
import projects.final_project.levels.tileMaps.WorldTileMap;

import java.util.concurrent.ThreadLocalRandom;

public class MenuGameWorld {

    public static final int LAYER = CollisionSystem.CollisionMask.layer0;

    public GameWorld gameWorld;
    private UIViewport viewport;
    private GameObject player;

    public MenuGameWorld(GameWorld gameWorld, UIViewport viewport) {
        this.gameWorld = gameWorld;
        this.viewport = viewport;

        gameWorld.linkViewport(0, this.viewport);
    }

    public void init() {
        this.player = new GameObject(this.gameWorld);
        player.addComponent(new CameraComponent(0, new Vec2d(0,0)));
        this.gameWorld.addGameObject(player);

        TileMap worldTileMap = WorldTileMap.createTileMap();


        setTerrain(worldTileMap);
        worldTileMap.addTilesToGameWorld(this.gameWorld, 0, 2, LAYER,LAYER);
        addGameObjects(gameWorld);

        this.player.getTransform().position = new Vec2d(10,10);
    }

    public void addGameObjects(GameWorld gameWorld){
        NaturalElements.placeTree(gameWorld, 1, new Vec2d(2,8));
        NaturalElements.placeChicken(gameWorld, 1, new Vec2d(1,9));
        NaturalElements.placeTree(gameWorld, 1, new Vec2d(18,16));

        NaturalElements.placeBush(gameWorld, 1, new Vec2d(16.5,8.5), 2);
        NaturalElements.placeRockFlowers(gameWorld, 1, new Vec2d(10,14), 2);
        NaturalElements.placeStump(gameWorld, 1, new Vec2d(2,15));

        placeGoomba(gameWorld, new Vec2d(5,8));
        placeGoomba(gameWorld, new Vec2d(12,15));
        placeGoomba(gameWorld, new Vec2d(10,5));
    }

    public void setTerrain(TileMap tileMap){
        int[][] tiles_int = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 1, 3, 0, 1},
                {0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                {1, 2, 2, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0},
        };

        int[][] heights = new int[][]{
                {1, 1, 3, 3, 3, 3, 3, 3, 3, 1, 0},
                {1, 1, 2, 2, 2, 2, 2, 2, 2, 1, 0},
                {1, 1, 2, 2, 2, 2, 2, 2, 2, 1, 1},
                {1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };

        String[] index = new String[]{"grass", "wall", "stairsV", "caveDoor"};
        String[][] tiles = new String[tiles_int.length][tiles_int[0].length];
        for(int i = 0; i < tiles_int.length; i++){
            for(int j = 0; j < tiles_int[0].length; j++){
                tiles[i][j] = index[tiles_int[i][j]];
            }
        }

        tileMap.setTiles(tiles);
        tileMap.setHeights(heights);
        tileMap.setExteriorTile("grass",1);
    }

    public static void placeGoomba(GameWorld gameWorld, Vec2d pos){
        GameObject enemy = new GameObject(gameWorld, 1);

        AnimationGraphComponent agc = Goomba.getGoombaAnimationGraph();
        enemy.addComponent(agc);
        enemy.addComponent(new SimpleGoombaMovementComponent(1, agc));


        enemy.addComponent(new CollisionComponent(new AABShape(new Vec2d(-.46,-.3),new Vec2d(0.7,0.65)),
                false, true, CollisionSystem.CollisionMask.ALL, CollisionSystem.CollisionMask.ALL));

        enemy.getTransform().position = pos;
        gameWorld.addGameObject(enemy);
    }

    public static class SimpleGoombaMovementComponent extends Component {

        private Vec2d direction = new Vec2d(0,0);
        private double speed;
        private double time = 2;

        private String state = "idle"; // idle, follow, prep, charge

        private AnimationGraphComponent animationGraphComponent;

        public SimpleGoombaMovementComponent(double speed, AnimationGraphComponent animationGraphComponent) {
            super();
            this.speed = speed;
            this.animationGraphComponent = animationGraphComponent;
            this.animationGraphComponent.queueAnimation("idle");
        }

        @Override
        public void onTick(long nanosSincePreviousTick){

            double dt = nanosSincePreviousTick/1000000000.0; //seconds since last tick
            time -= dt;

            if(dt > .1) dt = .1;

            if(time <= 0) {
                int pickDirection = ThreadLocalRandom.current().nextInt(0, 4);
                if(pickDirection == 0) direction = new Vec2d(0,1);
                else if(pickDirection == 1) direction = new Vec2d(0,-1);
                else if(pickDirection == 2) direction = new Vec2d(-1,0);
                else if(pickDirection == 3) direction = new Vec2d(1,0);
                else direction = new Vec2d(0,0);
                time = Math.random()*2.0 + 1;
            }

            if(direction.x == 0 && direction.y == 0) {
                this.animationGraphComponent.queueAnimation("idle");
            } else {
                this.animationGraphComponent.queueAnimation("walk");
            }

            this.animationGraphComponent.updateState(new Vec2d[]{this.direction});
            Vec2d pos = this.gameObject.getTransform().position;
            this.gameObject.getTransform().position = new Vec2d(pos.x + direction.x * dt * speed, pos.y + direction.y * dt * speed);
        }

        @Override
        public int getSystemFlags() {
            return SystemFlag.TickSystem;
        }


        @Override
        public String getTag() {
            return "SimpleGoombaMovementComponent";
        }
    }
}
