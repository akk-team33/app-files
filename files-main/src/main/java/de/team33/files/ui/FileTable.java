package de.team33.files.ui;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.serving.alpha.Gettable;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.alpha.visual.JLabels;
import de.team33.sphinx.alpha.visual.JTables;
import net.team33.fscalc.ui.rsrc.Ico;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

public class FileTable {

    // TODO: parameterized, preliminary ...
    private static final Icons ICONS = new Icons() {
        @Override
        public Icon stdFolder() {
            return Ico.CLSDIR;
        }

        @Override
        public Icon stdFile() {
            return Ico.FILE;
        }
    };

    private final JTable table;
    private final Component component;

    private FileTable(final Retrievable<? extends Path> cwd) {
        final Columns columns = new Columns(List.of(Column.values()));
        this.table = JTables.builder()
                            .setModel(new Model(columns, cwd))
                            .setDefaultRenderer(FileEntry.class, new CellRenderer(columns, ICONS, cwd))
                            .setup(jTable -> jTable.getTableHeader()
                                                   .setDefaultRenderer(new HeadRenderer(columns)))
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

    public final Component component() {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL);
        return component;
    }

    private enum Column {

        NAME____("Name", cwd -> FileEntry::name,
                 SwingConstants.LEADING),
        ABS_PATH("Abs. Path", cwd -> entry -> entry.path().toString(),
                 SwingConstants.LEADING),
        REL_PATH("Path", cwd -> entry -> cwd.relativize(entry.path()).toString(),
                 SwingConstants.LEADING),
        ABS_DIR_("Abs. Parent", cwd -> entry -> entry.path().getParent().toString(),
                 SwingConstants.LEADING),
        REL_DIR_("Parent", cwd -> entry -> cwd.relativize(entry.path().getParent()).toString(),
                 SwingConstants.LEADING),
        UPDATE__("Last Modified", cwd -> entry -> dateTime(entry.lastModified()),
                 SwingConstants.TRAILING),
        UPDATE_D("Last Modified Date", cwd -> entry -> date(entry.lastModified()),
                 SwingConstants.TRAILING),
        UPDATE_T("Last Modified Time", cwd -> entry -> time(entry.lastModified()),
                 SwingConstants.TRAILING),
        SIZE____("Size", cwd -> entry -> "%,d".formatted(entry.size()),
                 SwingConstants.TRAILING);

        private static final ZoneId ZONE_ID =
                ZoneId.systemDefault();
        private static final DateTimeFormatter DATE_TIME_FORMATTER =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                 .withLocale(Locale.getDefault());
        private static final DateTimeFormatter TIME_FORMATTER =
                DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
                                 .withLocale(Locale.getDefault());
        private static final DateTimeFormatter DATE_FORMATTER =
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                 .withLocale(Locale.getDefault());

        private final String title;
        private final Function<Path, Function<FileEntry, String>> toText;
        private final int alignment;

        Column(final String title, final Function<Path, Function<FileEntry, String>> toText, final int alignment) {
            this.title = title;
            this.toText = toText;
            this.alignment = alignment;
        }

        private static String time(final Instant instant) {
            return LocalTime.ofInstant(instant, ZONE_ID)
                            .format(TIME_FORMATTER);
        }

        private static String date(final Instant instant) {
            return LocalDate.ofInstant(instant, ZONE_ID)
                            .format(DATE_FORMATTER);
        }

        private static String dateTime(final Instant instant) {
            return LocalDateTime.ofInstant(instant, ZONE_ID)
                                .format(DATE_TIME_FORMATTER);
        }

        static Column of(final String columnName) {
            return Stream.of(values())
                         .filter(value -> value.title.equals(columnName))
                         .findAny()
                         .orElseThrow();
        }
    }

    private interface Icons {

        Icon stdFolder();

        Icon stdFile();
    }

    private static final class Columns {

        private final List<Column> backing;

        private Columns(final Collection<Column> columns) {
            this.backing = List.copyOf(columns);
        }

        final Column get(final int index) {
            return backing.get(index);
        }

        final int size() {
            return backing.size();
        }

        final int indexOf(final Column column) {
            return backing.indexOf(column);
        }
    }

    private static final class HeadRenderer implements TableCellRenderer {

        private final TableCellRenderer backing = new JTable().getTableHeader().getDefaultRenderer();
        private final Columns columns;

        private HeadRenderer(final Columns columns) {
            this.columns = columns;
        }

        @Override
        public final Component getTableCellRendererComponent(final JTable table,
                                                             final Object value,
                                                             final boolean isSelected,
                                                             final boolean hasFocus,
                                                             final int rowIndex,
                                                             final int colIndex) {
            final Column column =
                    columns.get(colIndex);
            final Component result =
                    backing.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, colIndex);
            jLabel(result).setHorizontalAlignment(column.alignment);
            return result;
        }
    }

    private static final class CellRenderer implements TableCellRenderer {

        private final TableCellRenderer backing = new JTable().getDefaultRenderer(String.class);
        private final Columns columns;
        private final Icons icons;
        private final Gettable<? extends Path> cwd;

        private CellRenderer(final Columns columns, final Icons icons, final Gettable<? extends Path> cwd) {
            this.columns = columns;
            this.icons = icons;
            this.cwd = cwd;
        }

        @Override
        public final Component getTableCellRendererComponent(final JTable table,
                                                             final Object value,
                                                             final boolean isSelected,
                                                             final boolean hasFocus,
                                                             final int rowIndex,
                                                             final int colIndex) {
            final FileEntry entry =
                    cast(FileEntry.class, value);
            final Column column =
                    columns.get(colIndex);
            final Component result =
                    backing.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, colIndex);
            return JLabels.charger(jLabel(result))
                          .setText(column.toText.apply(cwd.get()).apply(entry))
                          .setIcon(iconOf(entry, colIndex))
                          .setHorizontalAlignment(column.alignment)
                          .charged();
        }

        @SuppressWarnings("ReturnOfNull")
        private Icon iconOf(final FileEntry entry, final int colIndex) {
            if (0 < colIndex) {
                return null;
            } else if (entry.isDirectory()) {
                return icons.stdFolder();
            } else {
                return icons.stdFile();
            }
        }
    }

    private static final class Model extends AbstractTableModel {

        private final Columns columns;
        private volatile List<FileEntry> entries = List.of();

        private Model(final Columns columns, final Retrievable<? extends Path> cwd) {
            this.columns = columns;
            cwd.retrieve(this::onSetPath);
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
            return columns.size();
        }

        @Override
        public final Object getValueAt(final int rowIndex, final int colIndex) {
            return entries.get(rowIndex);
        }

        @Override
        public final String getColumnName(final int colIndex) {
            return columns.get(colIndex).title;
        }

        @Override
        public final int findColumn(final String columnName) {
            return columns.indexOf(Column.of(columnName));
        }

        @Override
        public final Class<?> getColumnClass(final int colIndex) {
            return FileEntry.class;
        }
    }
}
