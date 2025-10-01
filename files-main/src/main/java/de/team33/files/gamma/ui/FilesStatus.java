package de.team33.files.gamma.ui;

import javax.swing.*;
import java.awt.*;

public final class FilesStatus {

    private FilesStatus(final Context context) {
    }

    public static FilesStatus with(final Context context) {
        return new FilesStatus(context);
    }

    public Component ui() {
        // TODO, preliminary ...
        return new JLabel(getClass().getSimpleName());
    }

    public interface Context {
    }
}
