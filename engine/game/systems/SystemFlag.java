package engine.game.systems;

public class SystemFlag {
    public static final int None = 0;
    public static final int RenderSystem = 1;
    public static final int TickSystem = 2;
    public static final int CollisionSystem = 4;
    public static final int KeyEventSystem = 8;
    public static final int MouseEventSystem = 16;
    public static final int LightingSystem = 32;
    public static final int ScreenEffectSystem = 64;
}
