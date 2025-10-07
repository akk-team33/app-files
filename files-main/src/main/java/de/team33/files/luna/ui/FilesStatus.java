package de.team33.files.luna.ui;

import javax.swing.*;
import java.awt.*;

public final class FilesStatus {

    private FilesStatus(final Context context) {
    }

    public static FilesStatus by(final Context context) {
        return new FilesStatus(context);
    }

    public Component ui() {
        // TODO, preliminary ...
        return new JLabel(getClass().getSimpleName());
    }

    public interface Context {
    }
}