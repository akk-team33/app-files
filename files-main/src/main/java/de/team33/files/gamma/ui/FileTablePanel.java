package de.team33.files.gamma.ui;

import de.team33.sphinx.metis.JPanels;

import javax.swing.*;
import java.awt.*;

public final class FileTablePanel {

    private final JPanel panel;

    private FileTablePanel(final Context context) {
        this.panel = JPanels.builder(new BorderLayout())
                            .add(FileTableMenu.with(context).ui(), BorderLayout.PAGE_START)
                            .add(FileTable.with(context).ui(), BorderLayout.CENTER)
                            .build();
    }

    public static FileTablePanel with(final Context context) {
        return new FileTablePanel(context);
    }

    public Component ui() {
        return panel;
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    public interface Context extends FileTable.Context, FileTableMenu.Context {
    }
}
