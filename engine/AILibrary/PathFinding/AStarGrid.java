package engine.AILibrary.PathFinding;

import engine.support.Vec2i;

import java.util.*;

public class AStarGrid {


    public interface Heuristic{
        public double eval(Vec2i loc);
    }

    private static class GridLocation{
        public Vec2i loc;
        public double value;
        public GridLocation(Vec2i loc, double value){
            this.loc = loc;
            this.value = value;
        }
    }

    /**
     * Finds shortest path from start location to goal location on the grid. Assumed to only move up down left and right
     * @param grid 2D array where 1 represents impassible locations and 1 represents passable.
     * @param start starting grid location
     * @param goal ending grid location
     * @param h optional heuristic. if null Euclidean distance is used.
     * @return
     */
    public static List<Vec2i> solve(int[][] grid, Vec2i start, Vec2i goal, Heuristic h){
        if(h == null){ //if null replace with default heuristic
            h = new Heuristic() {
                @Override

                public double eval(Vec2i loc) {
                    double dx = (loc.x - goal.x);
                    double dy = (loc.y - goal.y);
                    return Math.sqrt(dx*dx + dy*dy);
                }
            };
        }

        PriorityQueue<GridLocation> edge = new PriorityQueue<GridLocation>(10, AStarGrid::compareGridLocations);
        edge.add(new GridLocation(start,0));

        Map<Vec2i, Vec2i> cameFrom = new HashMap<Vec2i, Vec2i>();
        Map<Vec2i, Double> g = new HashMap<Vec2i, Double>(); //distance to node
        g.put(start,0.0);
        Map<Vec2i, Double> f = new HashMap<Vec2i, Double>(); //distance from start to goal through this node
        f.put(start,h.eval(start));
        //f = g + h

        while(!edge.isEmpty()){

            Vec2i current = edge.poll().loc;
            if(current.x == goal.x && current.y == goal.y){
                return reconstructPath(cameFrom, current);
            }

            LinkedList<Vec2i> neighbors = new LinkedList<Vec2i>();
            if(current.x != 0 && grid[current.x - 1][current.y] == 0)
                neighbors.add(new Vec2i(current.x - 1,current.y));
            if(current.x != grid.length-1 && grid[current.x + 1][current.y] == 0)
                neighbors.add(new Vec2i(current.x + 1,current.y));
            if(current.y != 0 && grid[current.x][current.y - 1] == 0)
                neighbors.add(new Vec2i(current.x,current.y - 1));
            if(current.y != grid[0].length-1 && grid[current.x][current.y + 1] == 0)
                neighbors.add(new Vec2i(current.x,current.y + 1));


            for(Vec2i neighbor: neighbors){
                double score = g.get(current) + 1;
                if(!g.containsKey(neighbor) || score < g.get(neighbor)){
                    cameFrom.put(neighbor,current);
                    g.put(neighbor, score);
                    f.put(neighbor, score + h.eval(neighbor));
                    if(!edge.contains(neighbor)){
                        edge.add(new GridLocation(neighbor,score + h.eval(neighbor)));
                    }
                }
            }
        }
        //No path was found
        return null;
    }

    private static List<Vec2i> reconstructPath(Map<Vec2i, Vec2i> cameFrom, Vec2i current){
        LinkedList<Vec2i> path = new LinkedList<Vec2i>();
        path.add(current);
        while(cameFrom.containsKey(current)){
            current = cameFrom.get(current);
            path.addFirst(current);
        }
        return path;
    }

    private static int compareGridLocations(GridLocation l1, GridLocation l2) {
        double a = l1.value - l2.value;
        if(a > 0){
            return 1;
        } else if (a < 0){
            return -1;
        }
        return 0;
    }
}
