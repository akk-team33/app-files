package de.team33.files.eris.ui;

import javax.swing.*;
import java.awt.*;

public final class FileTable {

    private FileTable(final Context context) {
    }

    public static FileTable by(final Context context) {
        return new FileTable(context);
    }

    public Component ui() {
        // TODO, preliminary ...
        return new JLabel(getClass().getSimpleName());
    }

    public interface Context {
    }
}