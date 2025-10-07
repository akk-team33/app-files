package de.team33.files.luna.ui;

import javax.swing.*;
import java.awt.*;

public final class CWDInput {

    private CWDInput(final Context context) {
    }

    public static CWDInput by(final Context context) {
        return new CWDInput(context);
    }

    public Component ui() {
        // TODO, preliminary ...
        return new JLabel(getClass().getSimpleName());
    }

    public interface Context {
    }
}