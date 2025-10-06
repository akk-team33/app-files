package de.team33.files.gamma.ui;

import de.team33.patterns.streamable.galatea.Streamable;
import de.team33.sphinx.metis.JCheckBoxMenuItems;
import de.team33.sphinx.metis.JMenuBars;
import de.team33.sphinx.metis.JMenuItems;
import de.team33.sphinx.metis.JMenus;

import javax.swing.*;
import java.awt.*;

public final class FileTableMenu {

    private final JMenuBar bar;

    private FileTableMenu(final FileTable table, final Context context) {
        final Streamable<JMenuItem> columnItems =
                () -> table.availableColumns().stream()
                           .map(column -> JCheckBoxMenuItems.builder()
                                                            .setText(column.title())
                                                            .build());
        this.bar = JMenuBars.builder()
                            .add(JMenus.builder()
                                       .setText("Columns")
                                       .forEach(columnItems, JMenus.Setup::add)
                                       .build())
                            .add(JMenus.builder()
                                       .setText("Actions")
                                       .add(JMenuItems.builder().setText("Item1")
                                                      .build())
                                       .add(JMenuItems.builder().setText("Item2")
                                                      .build())
                                       .add(JMenuItems.builder().setText("Item3")
                                                      .build())
                                       .build())
                            .build();
    }

    public static FileTableMenu with(final FileTable table, final Context context) {
        return new FileTableMenu(table, context);
    }

    public Component ui() {
        return bar;
    }

    public interface Context {
    }
}
