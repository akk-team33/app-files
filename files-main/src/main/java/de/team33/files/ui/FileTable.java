package de.team33.files.ui;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.alpha.visual.JLabels;
import de.team33.sphinx.alpha.visual.JTables;
import net.team33.fscalc.ui.rsrc.Ico;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;

public class FileTable {

    private final TableCellRenderer fileEntryRenderer = new JTable().getDefaultRenderer(String.class);
    private final TableCellRenderer lastModifiedRenderer = new JTable().getDefaultRenderer(String.class);
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
                            .setDefaultRenderer(FileEntry.class, this::newFileEntryCell)
                            .setDefaultRenderer(Instant.class, this::newLastModifiedCell)
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

    private static <R> R cast(final Class<R> rClass, final Object value) {
        if (rClass.isInstance(value)) {
            return rClass.cast(value);
        } else {
            final Class<?> vClass = (null == value) ? null : value.getClass();
            throw new IllegalStateException(
                    "<value> is expected to be an instance of %s - but was %s".formatted(rClass, vClass));
        }
    }

    private static JLabel jLabel(final Object candidate) {
        return cast(JLabel.class, candidate);
    }

    private Component newLastModifiedCell(final JTable jTable,
                                          final Object value,
                                          final boolean isSelected,
                                          final boolean hasFocus,
                                          final int row,
                                          final int col) {
        final Component result =
                lastModifiedRenderer.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, col);
        jLabel(result).setText(LocalDateTime.ofInstant(cast(Instant.class, value), ZoneId.systemDefault())
                                            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                                                     .withLocale(Locale.getDefault())));
        return result;
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    private Component newFileEntryCell(final JTable jTable,
                                       final Object value,
                                       final boolean isSelected,
                                       final boolean hasFocus,
                                       final int row,
                                       final int col) {
        final Component result =
                fileEntryRenderer.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, col);
        final FileEntry entry = cast(FileEntry.class, value);
        return JLabels.charger(jLabel(result))
                      .setText(entry.name())
                      .setIcon(entry.isDirectory() ? Ico.CLSDIR : Ico.FILE)
                      .charged();
    }

    public final Component component() {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL);
        return component;
    }

    private enum Column {
        NAME("Name", FileEntry.class, Function.identity()),
        UPDATE("Last Modified", Instant.class, FileEntry::lastModified),
        SIZE("Size", Long.class, FileEntry::size);

        private final String title;
        private final Class<?> type;
        private final Function<FileEntry, ?> toValue;

        <T> Column(final String title,
                   final Class<T> type,
                   final Function<FileEntry, T> toValue) {
            this.title = title;
            this.type = type;
            this.toValue = toValue;
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

        private static Column column(final int index) {
            return Column.values()[index];
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
        public final Object getValueAt(final int rowIndex, final int colIndex) {
            return column(colIndex).toValue.apply(entries.get(rowIndex));
        }

        @Override
        public final String getColumnName(final int colIndex) {
            return column(colIndex).title;
        }

        @Override
        public final int findColumn(final String columnName) {
            return List.of(Column.values())
                       .indexOf(Column.of(columnName));
        }

        @Override
        public final Class<?> getColumnClass(final int colIndex) {
            return column(colIndex).type;
        }
    }
}
