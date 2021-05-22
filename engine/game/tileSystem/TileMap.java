package engine.game.tileSystem;

import engine.game.GameObject;
import engine.game.GameWorld;
import engine.game.components.Component;
import engine.support.Vec2d;

import java.util.*;

public class TileMap {

    private Vec2d offset = new Vec2d(0,0);

    private String[][] tiles;
    private int[][] heightmap;

    private HashMap<String, Tile> tileTypes;
    private HashMap<Integer, List<Component>> tileComponents;

    public enum RestrictionMethod{FourDirectional, EightDirectional, General}
    private RestrictionMethod restriction;

    private String exteriorType;
    private int exteriorHeight;

    public TileMap(List<Tile> tileTypes, RestrictionMethod restriction){
        this.tileTypes = new HashMap<String, Tile>();
        this.tileComponents = new HashMap<Integer, List<Component>>();
        this.restriction = restriction;

        for(Tile t: tileTypes){
            this.tileTypes.put(t.type,t);
        }
    }

    public void setTiles(String[][] tiles){
        this.tiles = tiles;
    }

    public void setOffset(Vec2d offset){
        this.offset = offset;
    }

    public void setExteriorTile(String exteriorType, int exteriorHeight){
        this.exteriorType = exteriorType;
        this.exteriorHeight = exteriorHeight;
    }

    public void addComponentToTile(int i, int j, Component c){
        if(this.tiles == null){
            System.err.println("Component not added at " + i + " " + j + ". Tiles need to be added first.");
            return;
        }
        if(this.tileComponents.get(i * this.tiles.length + j) == null){
            this.tileComponents.put(i * this.tiles.length + j, new ArrayList<Component>());
        }
        this.tileComponents.get(i * this.tiles.length + j).add(c);
    }

    public void setHeights(int[][] heightmap){
        this.heightmap = heightmap;
    }

    private int getHeight(int i, int j){
        if(this.heightmap == null) return 0;
        return this.heightmap[i][j];
    }

    public void addTilesToGameWorld(GameWorld gameWorld, int layer, double tileSize, int collisionLayer, int collisionMask){
        if(this.tiles == null){
            System.err.println("Tile Map Not Set");
            return;
        }

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                Tile t = this.tileTypes.get(tiles[i][j]);
                TileVariant tv = null;
                if(this.restriction == RestrictionMethod.FourDirectional) {
                    tv = this.get4DirectionalVariantAt(t, i, j);
                } else if(this.restriction == RestrictionMethod.EightDirectional){
                    tv = this.get8DirectionalVariantAt(t, i, j);
                } else if(this.restriction == RestrictionMethod.General){
                    tv = this.getGeneralVariantAt(t, i, j);
                }
                if(tv == null) continue;
                GameObject tile = tv.constructGameObject(new Vec2d(j,i).smult(tileSize),
                        new Vec2d(tileSize,tileSize), t.getTileSheetPath(), gameWorld, layer);
                for(Component c : tv.getCollisionComponents(tile, new Vec2d(tileSize,tileSize), collisionLayer, collisionMask)){
                    tile.addComponent(c);
                }
                List<Component> additionalComponents = this.tileComponents.get(i * this.tiles.length + j);
                if(additionalComponents != null)
                    for(Component c : additionalComponents){
                        tile.addComponent(c);
                    }
                gameWorld.addGameObject(tile);
            }
        }
    }

    private TileVariant get4DirectionalVariantAt(Tile t, int i, int j){
        Set<String> variants = new HashSet<String>(Arrays.asList(t.getVariants()));
        String type;
        int height;
        if(j > 0 || this.exteriorType != null) {
            type = j > 0 ? this.tileTypes.get(tiles[i][j - 1]).type : this.exteriorType;
            height = j > 0 ? this.getHeight(i, j - 1) : this.exteriorHeight;
            if (t.left != null) {
                String[] res = t.left.getVariantRestriction(type, this.getHeight(i, j) - height);
                if (res != null)
                    variants.retainAll(Arrays.asList(res));
            }
        }
        if(i < tiles.length - 1 || this.exteriorType != null){
            type = i < tiles.length - 1 ? this.tileTypes.get(tiles[i + 1][j]).type : this.exteriorType;
            height = i < tiles.length - 1 ? this.getHeight(i + 1, j) : this.exteriorHeight;
            if(t.down != null) {
                String[] res = t.down.getVariantRestriction(type, this.getHeight(i, j) - height);
                if(res != null)
                    variants.retainAll(Arrays.asList(res));
            }
        }
        if(j < tiles[0].length - 1 || this.exteriorType != null){
            type = j < tiles[0].length - 1 ? this.tileTypes.get(tiles[i][j+1]).type : this.exteriorType;
            height = j < tiles[0].length - 1 ? this.getHeight(i, j+1) : this.exteriorHeight;
            if(t.right != null) {
                String[] res = t.right.getVariantRestriction(type, this.getHeight(i, j) - height);
                if(res != null)
                    variants.retainAll(Arrays.asList(res));
            }
        }
        if(i > 0 || this.exteriorType != null){
            type = i > 0 ? this.tileTypes.get(tiles[i-1][j]).type : this.exteriorType;
            height = i > 0 ? this.getHeight(i-1, j) : this.exteriorHeight;
            if(t.up != null) {
                String[] res = t.up.getVariantRestriction(type, this.getHeight(i, j) - height);
                if(res != null)
                    variants.retainAll(Arrays.asList(res));
            }
        }
        if(variants.size() == 0){
            System.err.println("No Tile Options Left for tile: " + t.type + " at " + i + "," + j);
            return null;
        }
        return t.getVariant(variants.iterator().next());
    }

    private TileVariant get8DirectionalVariantAt(Tile t, int i, int j){
        Set<String> variants = new HashSet<String>(Arrays.asList(t.getVariants()));
        String type;
        int height;
        if(j > 0 || this.exteriorType != null) {
            type = j > 0 ? this.tileTypes.get(tiles[i][j - 1]).type : this.exteriorType;
            height = j > 0 ? this.getHeight(i, j - 1) : this.exteriorHeight;
            if (t.left != null) {
                String[] res = t.left.getVariantRestriction(type, this.getHeight(i, j) - height);
                if (res != null)
                    variants.retainAll(Arrays.asList(res));
            }
        }
        if(i < tiles.length - 1 || this.exteriorType != null){
            type = i < tiles.length - 1 ? this.tileTypes.get(tiles[i + 1][j]).type : this.exteriorType;
            height = i < tiles.length - 1 ? this.getHeight(i + 1, j) : this.exteriorHeight;
            if(t.down != null) {
                String[] res = t.down.getVariantRestriction(type, this.getHeight(i, j) - height);
                if(res != null)
                    variants.retainAll(Arrays.asList(res));
            }
        }
        if(j < tiles[0].length - 1 || this.exteriorType != null){
            type = j < tiles[0].length - 1 ? this.tileTypes.get(tiles[i][j+1]).type : this.exteriorType;
            height = j < tiles[0].length - 1 ? this.getHeight(i, j+1) : this.exteriorHeight;
            if(t.right != null) {
                String[] res = t.right.getVariantRestriction(type, this.getHeight(i, j) - height);
                if(res != null)
                    variants.retainAll(Arrays.asList(res));
            }
        }
        if(i > 0 || this.exteriorType != null){
            type = i > 0 ? this.tileTypes.get(tiles[i-1][j]).type : this.exteriorType;
            height = i > 0 ? this.getHeight(i-1, j) : this.exteriorHeight;
            if(t.up != null) {
                String[] res = t.up.getVariantRestriction(type, this.getHeight(i, j) - height);
                if(res != null)
                    variants.retainAll(Arrays.asList(res));
            }
        }
        if((j > 0 && i < tiles.length - 1) || this.exteriorType != null){
            type = j > 0 && i < tiles.length - 1 ? this.tileTypes.get(tiles[i+1][j-1]).type : this.exteriorType;
            height = j > 0 && i < tiles.length - 1 ? this.getHeight(i+1, j-1) : this.exteriorHeight;
            if(t.down_left != null) {
                String[] res = t.down_left.getVariantRestriction(type, this.getHeight(i, j) - height);
                if(res != null)
                    variants.retainAll(Arrays.asList(res));
            }
        }
        if((j < tiles[0].length - 1 && i < tiles.length - 1) || this.exteriorType != null){
            type = j < tiles[0].length - 1 && i < tiles.length - 1 ? this.tileTypes.get(tiles[i+1][j+1]).type : this.exteriorType;
            height = j < tiles[0].length - 1 && i < tiles.length - 1 ? this.getHeight(i+1, j+1) : this.exteriorHeight;
            if(t.down_right != null) {
                String[] res = t.down_right.getVariantRestriction(type, this.getHeight(i, j) - height);
                if(res != null)
                    variants.retainAll(Arrays.asList(res));
            }
        }
        if((j < tiles[0].length - 1 && i > 0) || this.exteriorType != null){
            type = j < tiles[0].length - 1 && i > 0 ? this.tileTypes.get(tiles[i-1][j+1]).type : this.exteriorType;
            height = j < tiles[0].length - 1 && i > 0 ? this.getHeight(i-1, j+1) : this.exteriorHeight;
            if(t.up_right != null) {
                String[] res = t.up_right.getVariantRestriction(type, this.getHeight(i, j) - height);
                if(res != null)
                    variants.retainAll(Arrays.asList(res));
            }
        }
        if((i > 0 && j > 0) || this.exteriorType != null){
            type = i > 0 && j > 0 ? this.tileTypes.get(tiles[i-1][j-1]).type : this.exteriorType;
            height = i > 0 && j > 0 ? this.getHeight(i-1, j-1) : this.exteriorHeight;
            if(t.up_left != null) {
                String[] res = t.up_left.getVariantRestriction(type, this.getHeight(i, j) - height);
                if(res != null)
                    variants.retainAll(Arrays.asList(res));
            }
        }
        if(variants.size() == 0){
            System.err.println("No Tile Options Left for tile: " + t.type + " at " + i + "," + j);
            return null;
        }
        return t.getVariant(variants.iterator().next());
    }

    private TileVariant getGeneralVariantAt(Tile t, int i, int j) {
        Set<String> variants = new HashSet<String>(Arrays.asList(t.getVariants()));
        String[] types = new String[8];
        int[] heights = new int[8];
        if(i > 0 || this.exteriorType != null){
            types[0] = i > 0 ? this.tileTypes.get(tiles[i-1][j]).type : this.exteriorType;
            heights[0] = i > 0 ? this.getHeight(i, j) - this.getHeight(i-1, j) : this.exteriorHeight;
        }
        if((j < tiles[0].length - 1 && i > 0) || this.exteriorType != null){
            types[1] = j < tiles[0].length - 1 && i > 0 ? this.tileTypes.get(tiles[i-1][j+1]).type : this.exteriorType;
            heights[1] = j < tiles[0].length - 1 && i > 0 ? this.getHeight(i, j) - this.getHeight(i-1, j+1) : this.exteriorHeight;
        }
        if(j < tiles[0].length - 1 || this.exteriorType != null){
            types[2] = j < tiles[0].length - 1 ? this.tileTypes.get(tiles[i][j+1]).type : this.exteriorType;
            heights[2] = j < tiles[0].length - 1 ? this.getHeight(i, j) - this.getHeight(i, j+1) : this.exteriorHeight;
        }
        if((j < tiles[0].length - 1 && i < tiles.length - 1) || this.exteriorType != null){
            types[3] = j < tiles[0].length - 1 && i < tiles.length - 1 ? this.tileTypes.get(tiles[i+1][j+1]).type : this.exteriorType;
            heights[3] = j < tiles[0].length - 1 && i < tiles.length - 1 ? this.getHeight(i, j) - this.getHeight(i+1, j+1) : this.exteriorHeight;
        }
        if(i < tiles.length - 1 || this.exteriorType != null){
            types[4] = i < tiles.length - 1 ? this.tileTypes.get(tiles[i + 1][j]).type : this.exteriorType;
            heights[4] = i < tiles.length - 1 ? this.getHeight(i, j) - this.getHeight(i + 1, j) : this.exteriorHeight;
        }
        if((j > 0 && i < tiles.length - 1) || this.exteriorType != null){
            types[5] = j > 0 && i < tiles.length - 1 ? this.tileTypes.get(tiles[i+1][j-1]).type : this.exteriorType;
            heights[5] = j > 0 && i < tiles.length - 1 ? this.getHeight(i, j) - this.getHeight(i+1, j-1) : this.exteriorHeight;
        }
        if(j > 0 || this.exteriorType != null) {
            types[6] = j > 0 ? this.tileTypes.get(tiles[i][j - 1]).type : this.exteriorType;
            heights[6] = j > 0 ?  this.getHeight(i, j) - this.getHeight(i, j - 1) : this.exteriorHeight;
        }
        if((i > 0 && j > 0) || this.exteriorType != null){
            types[7] = i > 0 && j > 0 ? this.tileTypes.get(tiles[i-1][j-1]).type : this.exteriorType;
            heights[7] = i > 0 && j > 0 ? this.getHeight(i, j) - this.getHeight(i-1, j-1) : this.exteriorHeight;
        }
        if(t.generalRestriction != null) {
            String[] res = t.generalRestriction.getVariantRestriction(types, heights);
            if (res != null)
                variants.retainAll(Arrays.asList(res));
        }
        if (variants.size() == 0) {
            System.err.println("No Tile Options Left for tile: " + t.type + " at " + i + "," + j);
            return null;
        }
        return t.getVariant(variants.iterator().next());
    }


}
