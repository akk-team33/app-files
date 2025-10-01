package de.team33.files.gamma.ui;

import de.team33.patterns.serving.alpha.Variable;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

public final class CWDInput {

    private CWDInput(final Context context) {
    }

    public static CWDInput with(final Context context) {
        return new CWDInput(context);
    }

    public Component ui() {
        // TODO, preliminary ...
        return new JLabel(getClass().getSimpleName());
    }

    public interface Context {

        Variable<Path> cwd();
    }
}
