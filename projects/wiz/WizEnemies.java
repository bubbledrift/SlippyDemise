package projects.wiz;

import engine.AILibrary.BehaviorTree.*;
import engine.AILibrary.BehaviorTree.Nodes.*;
import engine.AILibrary.PathFinding.AStarGrid;
import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.collisionShapes.AABShape;
import engine.game.components.*;
import engine.game.components.animation.SpriteAnimationComponent;
import engine.game.systems.CollisionSystem;
import engine.support.Vec2d;
import engine.support.Vec2i;

import java.util.List;

public class WizEnemies {


    private static final int ENEMY_LAYER = CollisionSystem.CollisionMask.layer1;
    private static final int ENEMY_MASK = CollisionSystem.CollisionMask.layer0 | CollisionSystem.CollisionMask.layer1;

    private static final double ENEMY_SPEED = 2;

    //spawner that creates enemies when near player
    public static GameObject createSpawner(GameWorld gameWorld, double spawnTime, Vec2d pos, GameObject player, int[][] grid){
        GameObject spawner = new GameObject(gameWorld, 2);
        spawner.addComponent(new SpriteComponent(WizGame.getSpritePath("spawner"),
                new Vec2d(0,0), new Vec2d(2,2)));
        spawner.addComponent(new IDComponent("spawner"));
        spawner.addComponent(new TimerComponent(spawnTime));

        spawner.getTransform().position = pos;
        spawner.getTransform().size = new Vec2d(2,2);
        return spawner;
    }

    public static void spawnCallback(GameObject player){
        //TODO need to store info in game object for this to work.
//        if(Math.abs(player.getTransform().position.x - pos.x) > 15){
//            return;
//        }
//        if(Math.abs(player.getTransform().position.y - pos.y) > 15){
//            return;
//        }
//        Vec2d offset = new Vec2d(Math.random()-.5,Math.random()-.5);
//        gameWorld.addGameObject(createEnemy(gameWorld, pos.plus(offset), player, grid));
    }

    //stationary enemy that kills player on contact
    public static GameObject createEnemy(GameWorld gameWorld, Vec2d pos, GameObject player, int[][] grid){
        GameObject enemy = new GameObject(gameWorld, 2);
        enemy.addComponent(new SpriteAnimationComponent(WizGame.getSpritePath("enemy"),
                new Vec2d(0,0), new Vec2d(2,2), 5, new Vec2d(0,128), new Vec2d(64,64), .1));
        enemy.addComponent(new IDComponent("enemy"));
        enemy.addComponent(new CollisionComponent(new AABShape(new Vec2d(.3,.3),new Vec2d(1.4,1.4)),
                false, true, ENEMY_LAYER, ENEMY_MASK));

        HealthComponent healthComponent = new HealthComponent(5);
        healthComponent.linkDeathCallback(WizEnemies::enemyDeathCallback);
        enemy.addComponent(healthComponent);

        enemy.addComponent(new BehaviorTreeComponent(createEnemyAI(enemy,player,grid)));
        enemy.addComponent(new ValueComponent(100));
        enemy.getTransform().position = pos;
        enemy.getTransform().size = new Vec2d(2,2);
        return enemy;
    }

    private static void enemyDeathCallback(GameObject enemy){
        CollisionComponent collision = (CollisionComponent)enemy.getComponent("CollisionComponent");
        collision.setCollisionLayer(CollisionSystem.CollisionMask.NONE);

        SpriteAnimationComponent animation = (SpriteAnimationComponent)enemy.getComponent("SpriteAnimationComponent");
        animation.resetAnimation(WizGame.getSpritePath("enemy"),
                new Vec2d(0,0), new Vec2d(2,2), 5,
                new Vec2d(0,64), new Vec2d(64,64),new Vec2d(64,0), .1); //death animation
        DelayEventComponent delayEventComponent = new DelayEventComponent(.5);
        delayEventComponent.linkEventCallback(WizEnemies::enemyRemoveCallback);
        enemy.addComponent(delayEventComponent);
    }

    private static void enemyRemoveCallback(GameObject gameObject){
        gameObject.gameWorld.removeGameObject(gameObject);
    }


    private static BTNode createEnemyAI(GameObject enemy, GameObject player, int[][] grid){
        BTSelectorNode root = new BTSelectorNode();

        BTSequenceNode followPlayer = new BTSequenceNode();

        followPlayer.linkChild(new BTConditionNode() {
            @Override
            public boolean checkCondition(double seconds) {
                return enemy.getTransform().position.dist(player.getTransform().position) < 10;
            }
        });

        followPlayer.linkChild(new BTActionNode() {
            @Override
            public BTNodeStatus update(double seconds) {
                SpriteAnimationComponent animation = (SpriteAnimationComponent)enemy.getComponent("SpriteAnimationComponent");
                animation.resetAnimation(WizGame.getSpritePath("enemy"),
                        new Vec2d(0,0), new Vec2d(2,2), 5,
                        new Vec2d(0,0), new Vec2d(64,64),new Vec2d(64,0), .1); //angry animation
                return BTNodeStatus.SUCCESS;
            }

            @Override
            public void reset() {

            }
        });

        followPlayer.linkChild(new BTActionNode() {
            private List<Vec2i> path;
            private int current_path_step;
            private double timer = 0;
            @Override
            public BTNodeStatus update(double seconds) {
                Vec2i enemy_pos = new Vec2i(Math.round((float)(enemy.getTransform().position.x/2)),
                        Math.round((float)(enemy.getTransform().position.y/2)));
                if(this.path == null) {
                    Vec2i player_pos = new Vec2i(Math.round((float)(player.getTransform().position.x/2)),
                            Math.round((float)(player.getTransform().position.y/2)));
                    this.path = AStarGrid.solve(grid, enemy_pos, player_pos, null);
                    current_path_step = 1;
                    timer = 0;
                    return BTNodeStatus.RUNNING;
                }
                if(path.size() < 2){
                    Vec2d dir = player.getTransform().position.minus(enemy.getTransform().position);
                    enemy.getTransform().position = enemy.getTransform().position.plus(dir.normalize().smult(ENEMY_SPEED));
                    return BTNodeStatus.RUNNING;
                }

                timer += seconds*ENEMY_SPEED;
                if(timer > 1){
                    path = null;
                    return BTNodeStatus.SUCCESS;
                }
                Vec2i lastStep = path.get(current_path_step-1);
                Vec2i nextStep = path.get(current_path_step);
                enemy.getTransform().position = new Vec2d((double)(2*lastStep.x)*(1-timer) + timer*(double)(2*nextStep.x),
                        (double)(2*lastStep.y)*(1-timer) + timer*(double)(2*nextStep.y));
                return BTNodeStatus.RUNNING;
            }

            @Override
            public void reset() {
                path = null;
                timer = 0;
            }
        });

        BTSequenceNode idle = new BTSequenceNode();

        idle.linkChild(new BTActionNode() {
            @Override
            public BTNodeStatus update(double seconds) {
                SpriteAnimationComponent animation = (SpriteAnimationComponent)enemy.getComponent("SpriteAnimationComponent");
                animation.resetAnimation(WizGame.getSpritePath("enemy"),
                        new Vec2d(0,0), new Vec2d(2,2), 5,
                        new Vec2d(0,128), new Vec2d(64,64),new Vec2d(64,0), .1); //idle animation
                return BTNodeStatus.SUCCESS;
            }

            @Override
            public void reset() {

            }
        });

        idle.linkChild(new BTActionNode() {
            @Override
            public BTNodeStatus update(double seconds) {
                return BTNodeStatus.RUNNING; //do nothing
            }
            @Override
            public void reset() {

            }
        });

        root.linkChild(followPlayer);
        root.linkChild(idle);
        return root;
    }

}
