package net.team33.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ClassUtil {

    private static Type[] getActualTypeArguments(final Class<?> genClass, final Type impGeneric) {
        if (impGeneric instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType)impGeneric;
            final Type raw = pt.getRawType();
            if (raw instanceof Class) {
                final Class<?> cRaw = (Class)raw;
                if (genClass.isAssignableFrom(cRaw)) {
                    return pt.getActualTypeArguments();
                }
            }
        }

        return null;
    }

    public static Type[] getActualTypeArguments(final Class<?> genClass, final Class<?> impClass) {
        Type[] ret = null;
        Class sif;
        if (genClass.isInterface()) {
            final Type[] var6;
            int var5 = (var6 = impClass.getGenericInterfaces()).length;

            int var4;
            for(var4 = 0; var4 < var5; ++var4) {
                final Type impGenericSIF = var6[var4];
                ret = getActualTypeArguments(genClass, impGenericSIF);
                if (ret != null) {
                    return ret;
                }
            }

            final Class[] var8;
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

    public static Class<?> getActualClassArgument(final Class<?> genClass, final Class<?> impClass) {
        final Type[] types = getActualTypeArguments(genClass, impClass);
        return types.length == 1 && types[0] instanceof Class ? (Class)types[0] : null;
    }
}
