//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.team33.application.logging;

import java.util.Date;

public interface Loggable {
    Level getLevel();

    Date getDate();

    Thread getThread();

    String getText();

    Throwable getException();
}
