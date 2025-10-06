package de.team33.files.gamma.ui;

import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.patterns.streamable.galatea.Streamable;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JCheckBoxes;
import de.team33.sphinx.metis.JMenuBars;
import de.team33.sphinx.metis.JMenuItems;
import de.team33.sphinx.metis.JMenus;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class FileTableMenu {

    private final JMenuBar bar;

    private FileTableMenu(final FileTable table, final Context context) {
        final Streamable<JComponent> columnItems =
                () -> table.availableColumns().stream()
                           .map(column -> JCheckBoxes.builder()
                                                     .setText(column.title())
                                                     .setup(cb -> table.columns()
                                                                       .subscribe(Retrievable.Mode.INIT,
                                                                                  list -> cb.setSelected(list.contains(column))))
                                                     .subscribe(Channel.ACTION_PERFORMED,
                                                                event -> toggle(table.columns(), column))
                                                     .build());
        this.bar = JMenuBars.builder()
                            .add(JMenus.builder()
                                       .setText("Columns")
                                       .add(JMenuItems.builder()
                                                      .setText("Resize all")
                                                      .subscribe(Channel.ACTION_PERFORMED, event -> table.resizeColumns())
                                                      .build())
                                       .add(new JSeparator())
                                       .forEach(columnItems, JMenus.Setup::add)
                                       .add(new JSeparator())
                                       .add(new JMenuItem("(done)"))
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

    private static void toggle(final Variable<List<FileTable.Column>> columns, final FileTable.Column column) {
        final List<FileTable.Column> newColumns = new ArrayList<>(columns.get());
        if (newColumns.contains(column)) {
            newColumns.remove(column);
        } else {
            newColumns.add(column);
        }
        columns.set(newColumns);
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
