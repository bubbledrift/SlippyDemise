package projects.final_project.levels.tileMaps;

import engine.game.collisionShapes.AABShape;
import engine.game.collisionShapes.PolygonShape;
import engine.game.tileSystem.SpriteTileVariant;
import engine.game.tileSystem.Tile;
import engine.game.tileSystem.TileMap;
import engine.support.Vec2d;
import projects.final_project.FinalGame;

import java.util.ArrayList;

public class CaveTileMap {

    public static TileMap createTileMap(){
        ArrayList<Tile> tileTypes = new ArrayList<>();

        Vec2d TS = new Vec2d(24,24);

        //cave tile
        Tile caveTile = new Tile("cave", FinalGame.getSpritePath("cave_sprite_sheet"));
        caveTile.addVariant(new SpriteTileVariant(0,0,0,0,"edge_none", new Vec2d(4,14).smult(24), TS));

        tileTypes.add(caveTile);

        Tile exitTile = new Tile("exit", FinalGame.getSpritePath("cave_sprite_sheet"));
        exitTile.addVariant(new SpriteTileVariant(0,1,0,1,"exit", new Vec2d(2,10).smult(24), TS));
//        exitTile.setAllVariantCollisionThickness(.2);
        tileTypes.add(exitTile);

        //wall tile
        Tile wallTile = new Tile("wall", FinalGame.getSpritePath("cave_sprite_sheet"));
        wallTile.addVariant(new SpriteTileVariant(new PolygonShape(
                new Vec2d[]{new Vec2d(0,0), new Vec2d(0,2), new Vec2d(1.5,2), new Vec2d(2,1.5), new Vec2d(2,0), new Vec2d(0,0)}),
                "br", new Vec2d(4,10).smult(24), TS));
        wallTile.addVariant(new SpriteTileVariant(new PolygonShape(
                new Vec2d[]{new Vec2d(0,0), new Vec2d(0,1.5), new Vec2d(.5,2), new Vec2d(2,2),new Vec2d(2,0), new Vec2d(0,0)}),
                "bl", new Vec2d(6,10).smult(24), TS));
        wallTile.addVariant(new SpriteTileVariant(new PolygonShape(
                new Vec2d[]{new Vec2d(0,0), new Vec2d(0,2), new Vec2d(2,2),new Vec2d(2,.5),new Vec2d(1.5,0), new Vec2d(0,0)}),
                "tr", new Vec2d(4,12).smult(24), TS));
        wallTile.addVariant(new SpriteTileVariant(new PolygonShape(
                new Vec2d[]{new Vec2d(0,.5), new Vec2d(0,2), new Vec2d(2,2), new Vec2d(2,0),new Vec2d(.5,0),new Vec2d(0,.5)}),
                "tl", new Vec2d(6,12).smult(24), TS));

        wallTile.addVariant(new SpriteTileVariant(new AABShape(new Vec2d(.5,.5), new Vec2d(1.5,1.5)),"w_br", new Vec2d(7,10).smult(24), TS));
        wallTile.addVariant(new SpriteTileVariant(new AABShape(new Vec2d(0,.5), new Vec2d(1.5,1.5)),"w_bl", new Vec2d(8,10).smult(24), TS));
        wallTile.addVariant(new SpriteTileVariant(new AABShape(new Vec2d(.5,0), new Vec2d(1.5,1.5)),"w_tr", new Vec2d(7,11).smult(24), TS));
        wallTile.addVariant(new SpriteTileVariant(new AABShape(new Vec2d(0,0), new Vec2d(1.5,1.5)),"w_tl", new Vec2d(8,11).smult(24), TS));

        wallTile.addVariant(new SpriteTileVariant(new AABShape(new Vec2d(.5,0), new Vec2d(1.5,2)), "l", new Vec2d(6,11).smult(24), TS));
        wallTile.addVariant(new SpriteTileVariant(new AABShape(new Vec2d(0,0), new Vec2d(1.5,2)), "r", new Vec2d(4,11).smult(24), TS));
        wallTile.addVariant(new SpriteTileVariant(new AABShape(new Vec2d(0,.5), new Vec2d(2,1.5)), "t", new Vec2d(5,12).smult(24), TS));
        wallTile.addVariant(new SpriteTileVariant(new AABShape(new Vec2d(0,0), new Vec2d(2,1.5)), "b", new Vec2d(5,10).smult(24), TS));

        //Doesn't need collision bc surrounded
        wallTile.addVariant(new SpriteTileVariant(0,0,0,0,"inner", new Vec2d(0,11).smult(24), TS));

        wallTile.set8DirectionRestrictions(
                (String type, int height)->{
                    if(type.equals("wall") || type.equals("exit"))
                        return new String[]{"inner", "br", "bl", "tr", "tl", "b", "l", "r", "w_tr", "w_tl"};
                    return new String[]{"t", "w_br", "w_bl", "w_tr", "w_tl"};
                },
                (String type, int height)->{
                    if(type.equals("wall") || type.equals("exit"))
                        return new String[]{"inner", "br", "bl", "tr", "tl", "t", "b", "l", "w_br", "w_tr"};
                    return new String[]{"r", "w_br", "w_bl", "w_tr", "w_tl"};
                },
                (String type, int height)->{
                    if(type.equals("wall") || type.equals("exit"))
                        return new String[]{"inner", "br", "bl", "tr", "tl", "t", "l", "r", "w_br", "w_bl"};
                    return new String[]{"b", "w_br", "w_bl", "w_tr", "w_tl"};
                },
                (String type, int height)->{
                    if(type.equals("wall") || type.equals("exit"))
                        return new String[]{"inner", "br", "bl", "tr", "tl", "t", "b", "r", "w_bl", "w_tl"};
                    return new String[]{"l", "w_br", "w_bl", "w_tr", "w_tl"};
                },
                (String type, int height)->{
                    if(type.equals("wall") || type.equals("exit"))
                        return new String[]{"inner", "br", "bl", "tl", "t", "b", "l", "r", "w_br", "w_bl", "w_tr", "w_tl"};
                    return new String[]{"tr", "t", "b", "l", "r", "w_br", "w_bl", "w_tl"};
                },
                (String type, int height)->{
                    if(type.equals("wall") || type.equals("exit"))
                        return new String[]{"inner", "bl", "tr", "tl", "t", "b", "l", "r", "w_br", "w_bl", "w_tr", "w_tl"};
                    return new String[]{"br", "t", "b", "l", "r", "w_bl", "w_tr", "w_tl"};
                },
                (String type, int height)->{
                    if(type.equals("wall") || type.equals("exit"))
                        return new String[]{"inner", "br", "tr", "tl", "t", "b", "l", "r", "w_br", "w_bl", "w_tr", "w_tl"};
                    return new String[]{"bl", "t", "b", "l", "r", "w_br", "w_tr", "w_tl"};
                },
                (String type, int height)->{
                    if(type.equals("wall") || type.equals("exit"))
                        return new String[]{"inner", "br", "bl", "tr", "t", "b", "l", "r", "w_br", "w_bl", "w_tr", "w_tl"};
                    return new String[]{"tl", "t", "b", "l", "r", "w_br", "w_bl", "w_tr"};
                }
        );

        tileTypes.add(wallTile);

        return new TileMap(tileTypes, TileMap.RestrictionMethod.EightDirectional);
    }
}
