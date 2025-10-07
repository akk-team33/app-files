
package de.team33.files.luna.ui;

import javax.swing.*;
import java.awt.*;

public final class FileTree {

    private FileTree(final Context context) {
    }

    public static FileTree by(final Context context) {
        return new FileTree(context);
    }

    public Component ui() {
        // TODO, preliminary ...
        return new JLabel(getClass().getSimpleName());
    }

    public interface Context {
    }
}