package de.team33.files.gamma.ui;

import javax.swing.*;
import java.awt.*;

public final class FileTable {

    private FileTable(final Context context) {
    }

    public static FileTable with(final Context context) {
        return new FileTable(context);
    }

    public Component main() {
        // TODO, preliminary ...
        return new JLabel(getClass().getSimpleName());
    }

    public interface Context {
    }
}
