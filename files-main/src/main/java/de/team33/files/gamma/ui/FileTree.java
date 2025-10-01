package de.team33.files.gamma.ui;

import javax.swing.*;
import java.awt.*;

public final class FileTree {

    private FileTree(final Context context) {
    }

    public static FileTree with(final Context context) {
        return new FileTree(context);
    }

    public Component main() {
        // TODO, preliminary ...
        return new JLabel(getClass().getSimpleName());
    }

    public interface Context {
    }
}
