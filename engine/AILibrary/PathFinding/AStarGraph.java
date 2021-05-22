package engine.AILibrary.PathFinding;

import java.util.*;

public class AStarGraph {


    public abstract class Node{
        public double value; //used for A* algorithm
        public abstract List<Node> getNeighbors();
        public abstract double costToNeighbor(Node n);
    }

    public interface Heuristic{
        public double eval(Node n);
    }

    /**
     * Finds Shortest path from start to goal using A* algorithm
     * @param start starting node for algorithm
     * @param goal goal node which needs to be reached
     * @param h heuristic, should return smaller values for nodes closer to the goal node.
     * @return Returns a list of nodes which is the path that should be taken to get from the start to the goal.
     */
    public static List<Node> solve(Node start, Node goal, Heuristic h){
        PriorityQueue<Node> edge = new PriorityQueue<Node>(10, AStarGraph::compareNodes);
        edge.add(start);

        Map<Node, Node> cameFrom = new HashMap<Node, Node>();
        Map<Node, Double> g = new HashMap<Node, Double>(); //distance to node
        g.put(start,0.0);
        Map<Node, Double> f = new HashMap<Node, Double>(); //distance from start to goal through this node
        f.put(start,h.eval(start));
        //f = g + h

        while(!edge.isEmpty()){
            Node current = edge.poll();
            if(current == goal){
                return reconstructPath(cameFrom, current);
            }

            for(Node neighbor: current.getNeighbors()){
                double score = g.get(current) + current.costToNeighbor(neighbor);
                if(!g.containsKey(neighbor) || score < g.get(neighbor)){
                    cameFrom.put(neighbor,current);
                    g.put(neighbor, score);
                    f.put(neighbor, score + h.eval(neighbor));
                    if(!edge.contains(neighbor)){
                        neighbor.value = score + h.eval(neighbor);
                        edge.add(neighbor);
                    }
                }
            }
        }
        //No path was found
        return null;
    }

    private static List<Node> reconstructPath(Map<Node,Node> cameFrom,Node current){
        LinkedList<Node> path = new LinkedList<Node>();
        path.add(current);
        while(cameFrom.containsKey(current)){
            current = cameFrom.get(current);
            path.addFirst(current);
        }
        return path;
    }

    private static int compareNodes(Node n1, Node n2) {
        double a = n1.value - n2.value;
        if(a > 0){
            return 1;
        } else if (a < 0){
            return -1;
        }
        return 0;
    }

}
