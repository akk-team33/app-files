package de.team33.files.luna.ui;

import javax.swing.*;
import java.awt.*;

public final class FileTablePanel {

    private FileTablePanel(final Context context) {
    }

    public static FileTablePanel by(final Context context) {
        return new FileTablePanel(context);
    }

    public Component ui() {
        // TODO, preliminary ...
        return new JLabel(getClass().getSimpleName());
    }

    public interface Context {
    }
}