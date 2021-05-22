package engine;

import engine.support.Vec2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


//TODO need way to search for certain tiles.
//TODO need way to create collision boxes for multiple tiles at once.
public class MapGeneration {

    private static final Random random = new Random();

    /**
     * Class to represent the generated dungeon.
     */
    public static class RoomDungeon{
        public int[][] map;
        public List<Room> rooms;

        public Vec2i start;
        public Vec2i end;
        public RoomDungeon(Vec2i size){
            map = new int[size.x][size.y];
            rooms = new ArrayList<Room>();
        }
    }

    /**
     * Class to represent the rooms of a dungeon.
     */
    public static class Room{
        public Vec2i position;
        public Vec2i size;
        public Room(Vec2i position, Vec2i size){
            this.position = position;
            this.size = size;
        }
    }

    /**
     * Finds starting location. Furthest top left tile of given value
     */
    public static Vec2i getStart(int[][] map, int val){
        for(int i = 0; i < map.length;i++){
            for(int j = 0; j < map[0].length; j++){
                if(map[i][j] == val){
                    return new Vec2i(i,j);
                }
            }
        }
        return new Vec2i(0,0);
    }

    /**
     * Finds ending location. Furthest top right tile of given value
     */
    public static Vec2i getEnd(int[][] map, int val){
        for(int i = map.length - 1; i >= 0;i--){
            for(int j = 0; j < map[0].length; j++){
                if(map[i][j] == val){
                    return new Vec2i(i,j);
                }
            }
        }
        return new Vec2i(0,0);
    }


    public static RoomDungeon generateDungeonMap(Vec2i size, Vec2i minRoomSize, int variablility, long seed){
        random.setSeed(seed);
        RoomDungeon dungeon = new RoomDungeon(size);
        fill(dungeon.map,new Vec2i(0,0), new Vec2i(size.x,size.y), 1);
        spacePartition(dungeon, new Vec2i(0,0),new Vec2i(size.x,size.y), minRoomSize,true, variablility);
        fill(dungeon.map,new Vec2i(0,0), new Vec2i(1, size.y), 1);
        fill(dungeon.map,new Vec2i(0,0), new Vec2i(size.x, 1), 1);
        fill(dungeon.map,new Vec2i(size.x-1,0), new Vec2i(1, size.y), 1);
        fill(dungeon.map,new Vec2i(0,size.y-1), new Vec2i(size.x, 1), 1);

        dungeon.start = getStart(dungeon.map, 0);
        dungeon.end = getEnd(dungeon.map, 0);
        return dungeon;
    }

    public static void spacePartition(RoomDungeon dungeon, Vec2i pos, Vec2i size, Vec2i minSize, boolean parity, int var){

        if((parity && size.x <= minSize.x + (var + 2)*2)
                || (!parity && size.y <= minSize.y + (var + 2)*2)){
            //Vec2i shift1 = new Vec2i((int)(Math.random()*(var + 1)) + 1,(int)(Math.random()*(var + 1)) + 1);
            //Vec2i shift2 = new Vec2i((int)(Math.random()*(var + 1)) + 1,(int)(Math.random()*(var + 1)) + 1);
            //fill(dungeon.map,pos.plus(shift1),(size.minus(shift1)).minus(shift2), 0);
            //dungeon.rooms.add(new Room(pos.plus(shift1),(size.minus(shift1)).minus(shift2)));

            fill(dungeon.map,pos.plus(new Vec2i(1,1)),(size.minus(new Vec2i(1,1))).minus(new Vec2i(2,2)), 0);
            dungeon.rooms.add(new Room(pos.plus(new Vec2i(1,1)),(size.minus(new Vec2i(1,1))).minus(new Vec2i(2,2))));
            return;
        }

        if(parity){
            int split = (int)(random.nextGaussian()*size.x/4) + size.x/2;
            split = Math.min(size.x - minSize.x - 1,Math.max(minSize.x + 1,split));
            spacePartition(dungeon, new Vec2i(pos.x, pos.y), new Vec2i(split,size.y), minSize, !parity, var);
            spacePartition(dungeon, new Vec2i(pos.x + split, pos.y), new Vec2i(size.x - split,size.y), minSize, !parity,var);
            int path = (int)(random.nextDouble() * (size.y - 2*(var + 1))) + pos.y + var + 1;
            fillPathToVal(dungeon.map,new Vec2i(pos.x + split,path), 0, true);
        } else {
            int split = (int)(random.nextGaussian()*size.y/4) + size.y/2;
            split = Math.min(size.y - minSize.y - 1,Math.max(minSize.y +1,split));
            spacePartition(dungeon, new Vec2i(pos.x, pos.y), new Vec2i(size.x,split), minSize, !parity,var);
            spacePartition(dungeon, new Vec2i(pos.x,pos.y + split), new Vec2i(size.x,size.y - split), minSize, !parity,var);
            int path = (int)(random.nextDouble() * (size.x - 2*(var + 1))) + pos.x + var + 1;
            fillPathToVal(dungeon.map,new Vec2i(path,pos.y + split), 0, false);
        }
    }

    public static void fill(int[][] arr, Vec2i pos, Vec2i size, int val){
        for(int i = pos.x; i < pos.x+size.x;i++){
            for(int j = pos.y; j < pos.y+size.y;j++){
                arr[i][j] = val;
            }
        }
    }

    public static void fillPathToVal(int[][] arr, Vec2i pos, int val, boolean isHorizontal){
        if(isHorizontal){
            for(int i = pos.x; i >= 0; i--){
                arr[i][pos.y] = val;
                if(arr[i][pos.y-1] == val || arr[i][pos.y+1] == val){
                    break;
                }
            }
            for(int i = pos.x + 1; i < arr.length; i++){
                arr[i][pos.y] = val;
                if(arr[i][pos.y-1] == val || arr[i][pos.y+1] == val){
                    break;
                }
            }
        } else {
            for(int i = pos.y; i >= 0; i--){
                arr[pos.x][i] = val;
                if(arr[pos.x-1][i] == val || arr[pos.x + 1][i] == val){
                    break;
                }
            }
            for(int i = pos.y + 1; i < arr[0].length; i++){
                arr[pos.x][i] = val;
                if(arr[pos.x-1][i] == val || arr[pos.x + 1][i] == val){
                    break;
                }
            }
        }
    }


}
