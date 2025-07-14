//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassUtil {
    public ClassUtil() {
    }

    private static Type[] getActualTypeArguments(Class<?> genClass, Type impGeneric) {
        if (impGeneric instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)impGeneric;
            Type raw = pt.getRawType();
            if (raw instanceof Class) {
                Class<?> cRaw = (Class)raw;
                if (genClass.isAssignableFrom(cRaw)) {
                    return pt.getActualTypeArguments();
                }
            }
        }

        return null;
    }

    public static Type[] getActualTypeArguments(Class<?> genClass, Class<?> impClass) {
        Type[] ret = (Type[])null;
        Class sif;
        if (genClass.isInterface()) {
            Type[] var6;
            int var5 = (var6 = impClass.getGenericInterfaces()).length;

            int var4;
            for(var4 = 0; var4 < var5; ++var4) {
                Type impGenericSIF = var6[var4];
                ret = getActualTypeArguments(genClass, impGenericSIF);
                if (ret != null) {
                    return ret;
                }
            }

            Class[] var8;
            var5 = (var8 = impClass.getInterfaces()).length;

            for(var4 = 0; var4 < var5; ++var4) {
                sif = var8[var4];
                if (genClass.isAssignableFrom(sif)) {
                    ret = getActualTypeArguments(genClass, sif);
                    if (ret != null) {
                        return ret;
                    }
                }
            }
        }

        if (!impClass.isInterface()) {
            ret = getActualTypeArguments(genClass, impClass.getGenericSuperclass());
            if (ret != null) {
                return ret;
            }

            sif = impClass.getSuperclass();
            if (sif != null && genClass.isAssignableFrom(sif)) {
                ret = getActualTypeArguments(genClass, sif);
                if (ret != null) {
                    return ret;
                }
            }
        }

        return null;
    }

    public static Class<?> getActualClassArgument(Class<?> genClass, Class<?> impClass) {
        Type[] types = getActualTypeArguments(genClass, impClass);
        return types.length == 1 && types[0] instanceof Class ? (Class)types[0] : null;
    }
}
