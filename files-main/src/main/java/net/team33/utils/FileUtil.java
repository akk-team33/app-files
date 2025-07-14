package net.team33.utils;

import java.io.File;

public class FileUtil {

    public static File getAccessible(File path) {
        File parent = path.getAbsoluteFile().getParentFile();
        return !parent.isDirectory() && !parent.mkdirs() ? null : path;
    }

    public static File getAccessible(String path) {
        return getAccessible(new File(path));
    }
}
