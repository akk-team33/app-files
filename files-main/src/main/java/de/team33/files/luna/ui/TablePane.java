package de.team33.files.luna.ui;

import de.team33.files.luna.context.UIContext;
import de.team33.sphinx.metis.JPanels;

import javax.swing.*;
import java.awt.*;

public final class TablePane {

    private final JPanel panel;

    private TablePane(final UIContext context) {
        this.panel = JPanels.builder(new BorderLayout())
                            .add(TableMenu.by(context.tableViewConfig()).ui(), BorderLayout.PAGE_START)
                            .add(FileTable.by(context).ui(), BorderLayout.CENTER)
                            .build();
    }

    public static TablePane by(final UIContext context) {
        return new TablePane(context);
    }

    public Component ui() {
        return panel;
    }
}