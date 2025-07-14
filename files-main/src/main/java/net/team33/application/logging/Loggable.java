package net.team33.application.logging;

import java.util.Date;

public interface Loggable {
    Level getLevel();

    Date getDate();

    Thread getThread();

    String getText();

    Throwable getException();
}
