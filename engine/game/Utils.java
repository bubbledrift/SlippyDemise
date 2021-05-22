package engine.game;

import java.lang.reflect.Method;

public class Utils {


    public static Method getMethod(String className, String methodName, Class[] cArg) throws ClassNotFoundException, NoSuchMethodException {
        Class cls = Class.forName(className);
        return cls.getMethod(methodName, cArg);
    }
}
