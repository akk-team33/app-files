package de.team33.files.ui;

import de.team33.patterns.io.phobos.FileEntry;
import de.team33.patterns.serving.alpha.Gettable;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.gamma.table.CellRenderer;
import de.team33.sphinx.gamma.table.HeadRenderer;
import de.team33.sphinx.gamma.table.Property;
import de.team33.sphinx.gamma.table.RowModel;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JButtons;
import de.team33.sphinx.metis.JPanels;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static java.util.function.Predicate.not;
import static javax.swing.JTable.AUTO_RESIZE_OFF;

public final class FileTable {

    private static final int MARGIN = 8;
    private static final Locale LOCALE = Locale.getDefault();
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private static final Comparator<FileEntry> ENTRY_SIZE =
            Comparator.comparing(FileEntry::size, Long::compareTo);
    private static final Comparator<FileEntry> ENTRY_LAST_MODIFIED =
            Comparator.comparing(FileEntry::lastModified, Instant::compareTo);
    private static final Comparator<String> STRING_IGNORE_CASE =
            String::compareToIgnoreCase;
    private static final Comparator<String> STRING_RESPECT_CASE =
            String::compareTo;
    private static final Comparator<String> STRING_NORMAL =
            STRING_IGNORE_CASE.thenComparing(STRING_RESPECT_CASE);
    private static final Comparator<FileEntry> ENTRY_NAME =
            Comparator.comparing(FileEntry::name, STRING_NORMAL);
    private static final Comparator<Path> PATH_NORMAL =
            Comparator.comparing(Path::toString, STRING_NORMAL);
    private static final Comparator<FileEntry> ENTRY_PATH =
            Comparator.comparing(FileEntry::path, PATH_NORMAL);

    private final List<? extends Column> columns;
    private final Icons icons;
    private final JTable table;
    private final Component component;

    private FileTable(final Retrievable<Path> cwd,
                      final List<Column> columns,
                      final Icons icons) {
        this.columns = Column.using(cwd, columns);
        this.icons = icons;
        this.table = JTables.builder()
                            .setModel(new Model(cwd))
                            .setDefaultRenderer(MyProperty.class, new MyCellRenderer())
                            .setup(jTable -> jTable.getTableHeader()
                                                   .setDefaultRenderer(new MyHeadRenderer()))
                            .setShowGrid(false)
                            .setRowSelectionAllowed(true)
                            .setColumnSelectionAllowed(false)
                            .setAutoCreateRowSorter(true)
                            .setAutoResizeMode(AUTO_RESIZE_OFF)
//                            .on(Event.MOUSE_CLICKED, new InfoTable.MOUSE_LISTENER(context)::mouseClicked)
//                            .setup(table -> table.getSelectionModel()
//                                                 .addListSelectionListener(new InfoTable.SelectionListener(table)))
//                            .setup(table -> FS.getRegister().add(new InfoTable.LSTNR_UPDINFO(table, context)))
                            .build();
        this.component = JPanels.builder()
                                .setLayout(new BorderLayout())
                                .add(new Controls().panel, BorderLayout.PAGE_START)
                                .add(new JScrollPane(table), BorderLayout.CENTER)
                                .build();
        Channel.MOUSE_CLICKED.subscribe(table.getTableHeader(), this::onMouseClicked);
    }

    public static FileTable by(final Context context) {
        return new FileTable(context.cwd(), context.columns(), context.icons());
    }

    private static <P> Comparator<P> neutralOrder() {
        return (left, right) -> 0;
    }

    private void onMouseClicked(final MouseEvent event) {
        if ((event.getComponent() instanceof final JTableHeader header) && (table == header.getTable())) {
            if (SwingUtilities.isLeftMouseButton(event)) {
                final int viewColIndex = header.columnAtPoint(event.getPoint());
                if (0 <= viewColIndex) {
                    final int colIndex = table.convertColumnIndexToModel(viewColIndex);
                    resizeColumn(colIndex);
                }
            }
        }
    }

    private void resizeColumn(final int colIndex) {
        final TableColumn column = table.getColumnModel().getColumn(colIndex);
        final TableCellRenderer headRenderer = Optional.ofNullable(column.getHeaderRenderer())
                                                       .orElseGet(() -> table.getTableHeader()
                                                                             .getDefaultRenderer());
        final Component head = headRenderer.getTableCellRendererComponent(
                table, column.getHeaderValue(), false, false, 0, colIndex);
        final int maxWidth = IntStream.range(0, table.getRowCount())
                                      .map(rowIndex -> preferredWidth(colIndex, rowIndex))
                                      .reduce(head.getPreferredSize().width, Math::max);
        column.setPreferredWidth(maxWidth + MARGIN);
    }

    private int preferredWidth(final int colIndex, final int rowIndex) {
        final TableCellRenderer cellRenderer = table.getCellRenderer(rowIndex, colIndex);
        final Component cell = table.prepareRenderer(cellRenderer, rowIndex, colIndex);
        return cell.getPreferredSize().width;
    }

    public final Component component() {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL);
        return component;
    }

    public interface Context {

        Icons icons();

        List<Column> columns();

        Retrievable<Path> cwd();
    }

    public interface Icons {

        Icon stdFolder();

        Icon stdFile();

        Icon optWidth();

        Icon parentFolder();
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    public interface Column extends RowModel.Column<FileEntry>,
                                    CellRenderer.Column {

        Column NAME = new ColumnB<>("Name", Name.class, SwingConstants.LEADING, Name::new);
        Column PATH = new ColumnA<>("Path", RelPath.class, SwingConstants.LEADING, RelPath::new);
        Column PARENT = new ColumnA<>("Location", RelLocation.class, SwingConstants.LEADING, RelLocation::new);
        Column UPDATE = new ColumnB<>("Last Modified", DateTime.class, SwingConstants.CENTER, DateTime::new);
        Column UPDATE_DATE = new ColumnB<>("Last Mod. Date", Date.class, SwingConstants.CENTER, Date::new);
        Column UPDATE_TIME = new ColumnB<>("Last Mod. Time", Time.class, SwingConstants.CENTER, Time::new);
        Column SIZE = new ColumnB<>("Size", Size.class, SwingConstants.TRAILING, Size::new);

        @SuppressWarnings({"StaticCollection", "StaticMethodOnlyUsedInOneClass"}) // List is immutable!
        List<Column> VALUES = List.of(NAME, PATH, PARENT, UPDATE, UPDATE_DATE, UPDATE_TIME, SIZE);

        @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
        static List<? extends Column> using(final Gettable<Path> cwd, final List<? extends Column> origin) {
            return origin.stream()
                         .map(column -> column.using(cwd))
                         .toList();
        }

        Column using(Gettable<Path> cwd);
    }

    private record ColumnA<P>(String title, Class<P> type, int horizontalAlignment,
                              BiFunction<Gettable<Path>, FileEntry, P> biMapping) implements Column {
        @Override
        public Column using(final Gettable<Path> cwd) {
            return new ColumnB<>(title, type, horizontalAlignment, fileEntry -> biMapping.apply(cwd, fileEntry));
        }

        @Override
        public P map(final FileEntry element) {
            throw new UnsupportedOperationException("Context <cwd> is missing - use using(cwd)");
        }
    }

    private record ColumnB<P>(String title, Class<P> type, int horizontalAlignment,
                              Function<FileEntry, P> mapping) implements Column {
        @Override
        public Column using(final Gettable<Path> cwd) {
            return this;
        }

        @Override
        public P map(final FileEntry element) {
            return mapping.apply(element);
        }
    }

    private static final class Name extends MyProperty<Name> {

        private static final Comparator<Name> ORDER = Comparator.comparing(MyProperty::entry, ENTRY_NAME);

        private Name(final FileEntry entry) {
            super(entry, Name.class, ORDER);
        }

        @Override
        public final String toString() {
            return entry().name();
        }
    }

    private static final class RelPath extends MyProperty<RelPath> {

        private static final Comparator<RelPath> ORDER = neutralOrder();

        private final Path relative;

        private RelPath(final Supplier<Path> cwd, final FileEntry entry) {
            super(entry, RelPath.class, ORDER);
            this.relative = cwd.get().relativize(entry.path());
        }

        @Override
        public final String toString() {
            return relative.toString();
        }
    }

    private static final class RelLocation extends MyProperty<RelLocation> {

        private static final Comparator<RelLocation> ORDER = neutralOrder();

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private final Optional<Path> parent;

        private RelLocation(final Supplier<Path> cwd, final FileEntry entry) {
            super(entry, RelLocation.class, ORDER);
            this.parent = Optional.ofNullable(entry.path().getParent())
                                  .map(p -> cwd.get()
                                               .relativize(p));
        }

        @Override
        public final String toString() {
            return parent.map(Path::toString)
                         .filter(not(String::isBlank))
                         .orElse(".");
        }
    }

    private static final class DateTime extends MyProperty<DateTime> {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                                                            .withLocale(LOCALE);
        private static final Comparator<DateTime> ORDER = Comparator.comparing(MyProperty::entry,
                                                                               ENTRY_LAST_MODIFIED);

        private final LocalDateTime dateTime;

        private DateTime(final FileEntry entry) {
            super(entry, DateTime.class, ORDER);
            this.dateTime = LocalDateTime.ofInstant(entry().lastModified(), ZONE_ID);
        }

        @Override
        public final String toString() {
            return dateTime.format(FORMATTER);
        }
    }

    private static final class Date extends MyProperty<Date> {

        private static final DateTimeFormatter FORMATTER =
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                 .withLocale(LOCALE);
        private static final Comparator<Date> ORDER =
                Comparator.comparing(MyProperty::entry, ENTRY_LAST_MODIFIED);

        private final LocalDate date;

        private Date(final FileEntry entry) {
            super(entry, Date.class, ORDER);
            this.date = LocalDate.ofInstant(entry().lastModified(), ZONE_ID);
        }

        @Override
        public final String toString() {
            return date.format(FORMATTER);
        }
    }

    private static final class Time extends MyProperty<Time> {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
                                                                            .withLocale(LOCALE);
        private static final Comparator<Time> ORDER = Comparator.comparing((Time ft) -> ft.time,
                                                                           LocalTime::compareTo)
                                                                .thenComparing(MyProperty::entry,
                                                                               ENTRY_LAST_MODIFIED);
        private final LocalTime time;

        private Time(final FileEntry entry) {
            super(entry, Time.class, ORDER);
            this.time = LocalTime.ofInstant(entry().lastModified(), ZONE_ID);
        }

        @Override
        public final String toString() {
            return time.format(FORMATTER);
        }
    }

    private static final class Size extends MyProperty<Size> {

        private static final Comparator<Size> ORDER = Comparator.comparing(MyProperty::entry, ENTRY_SIZE);

        private Size(final FileEntry entry) {
            super(entry, Size.class, ORDER);
        }

        @Override
        public final String toString() {
            return "%,d".formatted(entry().size());
        }
    }

    private abstract static class MyProperty<P extends MyProperty<P>> extends Property<P> {

        private final FileEntry entry;

        private MyProperty(final FileEntry entry, final Class<P> pClass, final Comparator<P> primaryOrder) {
            super(pClass, primaryOrder.thenComparing(MyProperty::entry, ENTRY_PATH));
            this.entry = entry;
        }

        final FileEntry entry() {
            return entry;
        }

        @Override
        public final boolean equals(final Object other) {
            // consistently with <compareTo()> ...
            return Property.equals(this, other);
        }

        @Override
        public final int hashCode() {
            // consistently with <equals()> and <compareTo()>:
            // final order depends on file entry path (see constructor) ...
            return entry.path().hashCode();
        }
    }

    private final class Model extends RowModel<FileEntry> {

        private volatile List<FileEntry> entries = List.of();

        private Model(final Retrievable<? extends Path> cwd) {
            cwd.subscribe(INIT, this::onSetPath);
        }

        private void onSetPath(final Path path) {
            this.entries = FileEntry.of(path)
                                    .resolved()
                                    .entries()
                                    .toList();
            fireTableDataChanged();
        }

        @Override
        protected final List<FileEntry> rows() {
            // Already IS immutable ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return entries;
        }

        @Override
        protected final List<? extends FileTable.Column> columns() {
            // Already IS immutable ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return columns;
        }
    }

    private final class MyHeadRenderer extends HeadRenderer<Column> {

        @Override
        protected List<? extends FileTable.Column> columns() {
            // Already IS immutable ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return columns;
        }
    }

    @SuppressWarnings("rawtypes")
    private final class MyCellRenderer extends CellRenderer<MyProperty, Column> {

        private MyCellRenderer() {
            super(MyProperty.class);
        }

        @Override
        protected List<? extends FileTable.Column> columns() {
            // Already IS immutable ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return columns;
        }

        @Override
        protected void setup(final JLabel result, final MyProperty value, final FileTable.Column column) {
            result.setIcon(column == columns().get(0) ? icon(value) : null);
        }

        private Icon icon(final MyProperty value) {
            return value.entry().isDirectory() ? icons.stdFolder() : icons.stdFile();
        }
    }

    private final class Controls {

        private final JPanel panel;

        private Controls() {
            panel = JPanels.builder()
                           .setLayout(new GridBagLayout())
                           .add(JButtons.builder()
                                        .setIcon(icons.parentFolder())
                                        .setToolTipText("Switch to parent directory")
                                        .build())
                           .add(JButtons.builder()
                                        .setIcon(icons.optWidth())
                                        .setToolTipText("Optimize column width")
                                        .build())
//                           .add(JComboBoxes.builder(ComboListModel.of(Column.class))
//                                           //.setIcon(icons.stdFolder()) // TODO!
//                                           .setToolTipText("Set file order")
//                                           .build())
                           .build();
        }
    }
}
