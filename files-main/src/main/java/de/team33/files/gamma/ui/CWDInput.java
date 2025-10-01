package de.team33.files.gamma.ui;

import javax.swing.*;
import java.awt.*;

public final class CWDInput {

    private CWDInput(final Context context) {
    }

    public static CWDInput with(final Context context) {
        return new CWDInput(context);
    }

    public Component main() {
        // TODO, preliminary ...
        return new JLabel(getClass().getSimpleName());
    }

    public interface Context {
    }
}
