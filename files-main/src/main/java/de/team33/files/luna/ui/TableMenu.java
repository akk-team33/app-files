package de.team33.files.luna.ui;

import javax.swing.*;
import java.awt.*;

public final class TableMenu {

    private TableMenu(final Context context) {
    }

    public static TableMenu by(final Context context) {
        return new TableMenu(context);
    }

    public Component ui() {
        // TODO, preliminary ...
        return new JLabel(getClass().getSimpleName());
    }

    public interface Context {
    }
}