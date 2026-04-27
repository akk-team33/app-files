package de.team33.files.luna.ui;

import de.team33.files.luna.context.UIContext;

import javax.swing.*;
import java.awt.*;

public final class FilesStatus {

    private FilesStatus(final UIContext context) {
    }

    public static FilesStatus by(final UIContext context) {
        return new FilesStatus(context);
    }

    public Component ui() {
        // TODO, preliminary ...
        return new JLabel(getClass().getSimpleName());
    }
}