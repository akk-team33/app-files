package de.team33.files.ui;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.alpha.visual.JTables;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;

public class FileTable {

    private final JTable table;
    private final Component component;

    private FileTable(final Retrievable<? extends Path> path) {
        this.table = JTables.builder()
                            .setModel(new Model(path))
//                            .setup(jTable -> JTableHeaders.charger(jTable.getTableHeader())
//                                                          .setDefaultRenderer(headRenderer(context))
//                                                          .on(de.team33.sphinx.alpha.activity.Event.MOUSE_CLICKED,
//                                                              new InfoTable.HEADER_MOUSE_LISTENER(jTable, context)::mouseClicked))
//                            .setRowHeight(cr.getPreferredSize().height + 3)
//                            .setDefaultRenderer(FileInfo.class, cr)
                            .setShowGrid(false)
                            .setRowSelectionAllowed(true)
                            .setColumnSelectionAllowed(false)
//                            .on(Event.MOUSE_CLICKED, new InfoTable.MOUSE_LISTENER(context)::mouseClicked)
//                            .setup(table -> table.getSelectionModel()
//                                                 .addListSelectionListener(new InfoTable.SelectionListener(table)))
//                            .setup(table -> FS.getRegister().add(new InfoTable.LSTNR_UPDINFO(table, context)))
                            .build();
        this.component = new JScrollPane(table);
    }

    public static FileTable serving(final Retrievable<? extends Path> path) {
        return new FileTable(path);
    }

    public final Component component() {
        return component;
    }

    private enum Column {
        NAME("Name", String.class, FileEntry::name),
        UPDATE("Last Modified", Instant.class, FileEntry::lastModified),
        SIZE("Size", Long.class, FileEntry::size);

        private final String title;
        private final Class<?> type;
        private final Function<FileEntry, ?> getter;

        <T> Column(final String title, final Class<T> type, final Function<FileEntry, T> getter) {
            this.title = title;
            this.type = type;
            this.getter = getter;
        }

        private static Column of(final String title) {
            return Stream.of(values())
                         .filter(value -> value.title.equals(title))
                         .findAny()
                         .orElseThrow(() -> new NoSuchElementException("not found: " + title));
        }
    }

    private static class Model extends AbstractTableModel {

        private volatile List<FileEntry> entries = List.of();

        private Model(final Retrievable<? extends Path> path) {
            path.retrieve(this::onSetPath);
        }

        private void onSetPath(final Path path) {
            this.entries = FileEntry.of(path)
                                    .resolved()
                                    .entries()
                                    .toList();
            fireTableDataChanged();
        }

        @Override
        public final int getRowCount() {
            return entries.size();
        }

        @Override
        public final int getColumnCount() {
            return Column.values().length;
        }

        @Override
        public final Object getValueAt(final int rowIndex, final int columnIndex) {
            return Column.values()[columnIndex].getter.apply(entries.get(rowIndex));
        }

        @Override
        public final String getColumnName(final int column) {
            return Column.values()[column].title;
        }

        @Override
        public final int findColumn(final String columnName) {
            return List.of(Column.values()).indexOf(Column.of(columnName));
        }

        @Override
        public final Class<?> getColumnClass(final int column) {
            return Column.values()[column].type;
        }
    }

    private static class CellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(final JTable table,
                                                       final Object value,
                                                       final boolean isSelected,
                                                       final boolean hasFocus,
                                                       final int row,
                                                       final int column) {
            throw new UnsupportedOperationException("not yet implemented");
        }
    }
}
