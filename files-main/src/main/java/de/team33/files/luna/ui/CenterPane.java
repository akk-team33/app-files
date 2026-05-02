package de.team33.files.luna.ui;

import de.team33.files.luna.context.UIContext;
import de.team33.sphinx.metis.JSplitPanes;

import javax.swing.*;
import java.awt.*;

public final class CenterPane {

    private final JSplitPane splitPane;

    private CenterPane(final UIContext context) {
        this.splitPane = JSplitPanes.builder()
                                    .setLeftComponent(FileTree.by(context).ui())
                                    .setRightComponent(TablePane.by(context).ui())
                                    .build();
    }

    public static CenterPane by(final UIContext context) {
        return new CenterPane(context);
    }

    public Component ui() {
        return splitPane;
    }
}