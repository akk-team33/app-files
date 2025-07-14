//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.fscalc.info;

import net.team33.fscalc.task.Controller;

import java.io.File;

public interface FileInfo {
    File getPath();

    boolean isDefinite();

    long getTotalSize();

    long getAverageSize();

    long getFileCount();

    long getDirCount();

    long getErrorCount();

    void calculate(Controller var1);

    void reset();
}
