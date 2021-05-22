package projects.final_project.levels.tileMaps;

import engine.game.tileSystem.SpriteTileVariant;
import engine.game.tileSystem.Tile;
import engine.game.tileSystem.TileMap;
import engine.game.collisionShapes.AABShape;
import engine.game.collisionShapes.Shape;
import engine.support.Vec2d;
import projects.final_project.FinalGame;

import java.util.ArrayList;

public class WorldTileMap {

    public static TileMap createTileMap(){
        ArrayList<Tile> tileTypes = new ArrayList<>();

        Vec2d TS = new Vec2d(32,32);

        //grass tile
        Tile grassTile = new Tile("grass", FinalGame.getSpritePath("tile_sprite_sheet"));
        grassTile.addVariant(new SpriteTileVariant(1,0,0,1,"edge_tl", new Vec2d(0,1).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(1,0,0,0,"edge_t", new Vec2d(1,1).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(1,1,0,0,"edge_tr", new Vec2d(2,1).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(0,0,0,1,"edge_l", new Vec2d(0,2).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(0,0,0,0,"edge_none", new Vec2d(1,2).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(0,1,0,0,"edge_r", new Vec2d(2,2).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(0,0,1,1,"edge_bl", new Vec2d(0,3).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(0,0,1,0,"edge_b", new Vec2d(1,3).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(0,1,1,0,"edge_br", new Vec2d(2,3).smult(32), TS));

        grassTile.addVariant(new SpriteTileVariant(0,1,0,1,"edge_lr", new Vec2d(0,4).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(1,1,0,1,"edge_ltr", new Vec2d(1,4).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(1,0,1,0,"edge_tb", new Vec2d(2,4).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(1,0,1,1,"edge_ltb", new Vec2d(0,5).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(1,1,1,1,"edge_ltrb", new Vec2d(1,5).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(1,1,1,0,"edge_trb", new Vec2d(2,5).smult(32), TS));
        grassTile.addVariant(new SpriteTileVariant(0,1,1,1,"edge_lrb", new Vec2d(1,6).smult(32), TS));

        grassTile.set4DirectionRestrictions(
                (String type, int height)->{
                    if(height > 0)
                        return new String[]{"edge_tl","edge_t","edge_tr","edge_ltr","edge_tb","edge_ltb","edge_ltrb","edge_trb"};
                    return new String[]{"edge_l","edge_none","edge_r","edge_bl","edge_b","edge_br","edge_lr","edge_lrb"};
                },
                (String type, int height)->{
                    if(height > 0)
                        return new String[]{"edge_tr","edge_r","edge_br","edge_lr","edge_ltr","edge_ltrb","edge_trb","edge_lrb"};
                    return new String[]{"edge_tl","edge_t","edge_l","edge_none","edge_bl","edge_b","edge_tb","edge_ltb"};
                },
                (String type, int height)->{
                    if(type.equals("stairsV")){
                        return new String[]{"edge_tl","edge_t","edge_tr","edge_l","edge_none","edge_r","edge_lr","edge_ltr"};
                    }
                    if(height > 0)
                        return new String[]{"edge_bl","edge_b","edge_br","edge_tb","edge_ltb","edge_ltrb","edge_trb","edge_lrb"};
                    return new String[]{"edge_tl","edge_t","edge_tr","edge_l","edge_none","edge_r","edge_lr","edge_ltr"};
                },
                (String type, int height)->{
                    if(height > 0)
                        return new String[]{"edge_tl","edge_l","edge_bl","edge_lr","edge_ltr","edge_ltb","edge_ltrb","edge_lrb"};
                    return new String[]{"edge_t","edge_tr","edge_none","edge_r","edge_b","edge_br","edge_tb","edge_trb"};
                });
        grassTile.setAllVariantCollisionThickness(.1);
        tileTypes.add(grassTile);

        //wall tile
        Tile wallTile = new Tile("wall", FinalGame.getSpritePath("tile_sprite_sheet"));
        Shape tileCollision = new AABShape(new Vec2d(0,0), new Vec2d(2,2));
        wallTile.addVariant(new SpriteTileVariant(tileCollision,"up_lr", new Vec2d(3,1).smult(32), TS));
        wallTile.addVariant(new SpriteTileVariant(tileCollision,"down_lr", new Vec2d(3,2).smult(32), TS));

        wallTile.addVariant(new SpriteTileVariant(tileCollision,"up_l", new Vec2d(3,3).smult(32), TS));
        wallTile.addVariant(new SpriteTileVariant(tileCollision,"up", new Vec2d(4,3).smult(32), TS));
        wallTile.addVariant(new SpriteTileVariant(tileCollision,"up_r", new Vec2d(5,3).smult(32), TS));

        wallTile.addVariant(new SpriteTileVariant(tileCollision,"down_l", new Vec2d(3,4).smult(32), TS));
        wallTile.addVariant(new SpriteTileVariant(tileCollision,"down", new Vec2d(4,4).smult(32), TS));
        wallTile.addVariant(new SpriteTileVariant(tileCollision,"down_r", new Vec2d(5,4).smult(32), TS));

        wallTile.set4DirectionRestrictions(
                (String type, int height)->{
                    return new String[]{"up_lr","down_lr","up_l","up","up_r","down_l","down","down_r"};
                },
                (String type, int height)->{
                    if(height >= 0 && type.equals("grass"))
                        return new String[]{"up_lr","down_lr","up_r","down_r"};
                    return new String[]{"up_l","up","down_l","down"};
                },
                (String type, int height)->{
                    if(type.equals("wall")) return new String[]{"up_lr","up_l","up","up_r"};
                    if(type.equals("grass")) return new String[]{"down_lr","down_l","down","down_r"};
                    return new String[]{"up_lr","down_lr","up_l","up","up_r","down_l","down","down_r"};
                },
                (String type, int height)->{
                    if(height >= 0 && type.equals("grass"))
                        return new String[]{"up_lr","down_lr","up_l","down_l"};
                    return new String[]{"up","up_r","down","down_r"};
                });
        tileTypes.add(wallTile);

        //caveDoor tile
        Tile caveDoorTile = new Tile("caveDoor", FinalGame.getSpritePath("tile_sprite_sheet"));
        caveDoorTile.addVariant(new SpriteTileVariant(1,1,0,1,"gap_l", new Vec2d(6,1).smult(32), TS));
        caveDoorTile.addVariant(new SpriteTileVariant(1,1,0,1,"gap_r", new Vec2d(6,2).smult(32), TS));
        caveDoorTile.addVariant(new SpriteTileVariant(1,1,0,1,"gap_lr", new Vec2d(6,3).smult(32), TS));
        caveDoorTile.addVariant(new SpriteTileVariant(1,1,0,1,"gap_none", new Vec2d(6,4).smult(32), TS));

        caveDoorTile.set4DirectionRestrictions(
                (String type, int height)->{
                    return null;
                },
                (String type, int height)->{
                    if(height >= 0 && type.equals("grass"))
                        return new String[]{"gap_lr","gap_r"};
                    return new String[]{"gap_l","gap_none"};
                },
                (String type, int height)->{
                    return null;
                },
                (String type, int height)->{
                    if(height >= 0 && type.equals("grass"))
                        return new String[]{"gap_lr","gap_l"};
                    return new String[]{"gap_r","gap_none"};
                });
        caveDoorTile.setAllVariantCollisionThickness(.25);
        tileTypes.add(caveDoorTile);

        //stairs up tile
        Tile stairsV = new Tile("stairsV", FinalGame.getSpritePath("tile_sprite_sheet"));
        stairsV.addVariant(new SpriteTileVariant(0,0,0,1,"stairv_tl_grass", new Vec2d(11,1).smult(32), TS));
        stairsV.addVariant(new SpriteTileVariant(0,1,0,0,"stairv_tr_grass", new Vec2d(12,1).smult(32), TS));
        stairsV.addVariant(new SpriteTileVariant(0,1,0,1,"stairv_tlr_grass", new Vec2d(13,1).smult(32), TS));
        stairsV.addVariant(new SpriteTileVariant(0,0,0,0,"stairv_t_grass", new Vec2d(14,1).smult(32), TS));

        stairsV.addVariant(new SpriteTileVariant(0,0,0,1,"stairv_l_grass", new Vec2d(11,2).smult(32), TS));
        stairsV.addVariant(new SpriteTileVariant(0,1,0,0,"stairv_r_grass", new Vec2d(12,2).smult(32), TS));
        stairsV.addVariant(new SpriteTileVariant(0,1,0,1,"stairv_lr_grass", new Vec2d(13,2).smult(32), TS));
        stairsV.addVariant(new SpriteTileVariant(0,0,0,0,"stairv", new Vec2d(14,2).smult(32), TS));

        stairsV.addVariant(new SpriteTileVariant(0,0,0,0,"stairv_b_grass", new Vec2d(14,3).smult(32), TS));

        stairsV.addVariant(new SpriteTileVariant(0,0,0,1,"stairv_l_wall", new Vec2d(11,6).smult(32), TS));
        stairsV.addVariant(new SpriteTileVariant(0,1,0,0,"stairv_r_wall", new Vec2d(12,6).smult(32), TS));
        stairsV.addVariant(new SpriteTileVariant(0,1,0,1,"stairv_lr_wall", new Vec2d(13,6).smult(32), TS));

        stairsV.addVariant(new SpriteTileVariant(0,0,0,1,"stairv_l_wall_b_grass", new Vec2d(11,10).smult(32), TS));
        stairsV.addVariant(new SpriteTileVariant(0,1,0,0,"stairv_r_wall_b_grass", new Vec2d(12,10).smult(32), TS));
        stairsV.addVariant(new SpriteTileVariant(0,1,0,1,"stairv_lr_wall_b_grass", new Vec2d(13,10).smult(32), TS));

        stairsV.set4DirectionRestrictions(
                (String type, int height)->{
                    if(type.equals("grass")){
                        return null;
                    }
                    return new String[]{"stairv_l_grass","stairv_r_grass","stairv_lr_grass","stairv_b_grass","stairv",
                            "stairv_l_wall","stairv_r_wall","stairv_lr_wall",
                            "stairv_l_wall_b_grass","stairv_r_wall_b_grass","stairv_lr_wall_b_grass"};
                },
                (String type, int height)->{
                    if(type.equals("grass")  && height == 1)
                        return new String[]{"stairv_tr_grass","stairv_tlr_grass","stairv_r_grass","stairv_lr_grass"};
                    if(type.equals("stairsV"))
                        return new String[]{"stairv_tl_grass", "stairv_t_grass", "stairv_l_grass", "stairv", "stairv_b_grass",
                                "stairv_l_wall","stairv_l_wall_b_grass"};
                    return new String[]{"stairv_r_wall","stairv_lr_wall",
                            "stairv_r_wall_b_grass","stairv_lr_wall_b_grass"};
                },
                (String type, int height)->{
                    if(type.equals("grass")){
                        return null;
                    }
                    return new String[]{"stairv_l_grass","stairv_r_grass","stairv_lr_grass","stairv_b_grass","stairv",
                            "stairv_l_wall","stairv_r_wall","stairv_lr_wall",
                            "stairv_tl_grass","stairv_tr_grass","stairv_tlr_grass", "stairv_t_grass"};
                },
                (String type, int height)->{
                    if(type.equals("grass") && height == 1)
                        return new String[]{"stairv_tl_grass","stairv_tlr_grass","stairv_l_grass","stairv_lr_grass"};
                    if(type.equals("stairsV"))
                        return new String[]{"stairv_tr_grass", "stairv_t_grass", "stairv_r_grass", "stairv", "stairv_b_grass",
                                "stairv_r_wall","stairv_r_wall_b_grass"};
                    return new String[]{"stairv_l_wall","stairv_lr_wall",
                            "stairv_l_wall_b_grass","stairv_lr_wall_b_grass"};
                });
        stairsV.setAllVariantCollisionThickness(.5);
        tileTypes.add(stairsV);


        TileMap tileMap = new TileMap(tileTypes, TileMap.RestrictionMethod.FourDirectional);
        return tileMap;
    }
}
