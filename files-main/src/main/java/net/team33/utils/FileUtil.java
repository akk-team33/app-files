//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.utils;

import java.io.File;

public class FileUtil {
    public FileUtil() {
    }

    public static File getAccessible(File path) {
        File parent = path.getAbsoluteFile().getParentFile();
        return !parent.isDirectory() && !parent.mkdirs() ? null : path;
    }

    public static File getAccessible(String path) {
        return getAccessible(new File(path));
    }
}
