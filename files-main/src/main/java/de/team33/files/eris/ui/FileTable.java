package de.team33.files.eris.ui;

import de.team33.files.luna.common.EntryOrder;
import de.team33.files.luna.context.TableViewConfig;
import de.team33.files.luna.ui.FilesIcons;
import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.io.adrastea.LinkHandling;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.epsilon.table.CellProperty;
import de.team33.sphinx.epsilon.table.RowColumnModel;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static java.util.Comparator.comparing;
import static java.util.function.Predicate.not;
import static javax.swing.JTable.AUTO_RESIZE_OFF;

@SuppressWarnings("unused")
public final class FileTable {

    private static final FileEntry.Lister LISTER = FileEntry.lister(LinkHandling.RESOLVE);
    private static final FileEntry.Streamer STREAMER = FileEntry.streamer(LISTER);

    private final Variable<Mode> mode;
    private final Variable<List<Column>> columns;
    private final Variable<Path> cwd;
    @SuppressWarnings("FieldCanBeLocal")
    private final JTable table;
    private final JScrollPane scrollPane;

    private FileTable(final Context context) {
        this.mode = new Component<>(context.executor(), Mode.FLAT);
        this.columns = new Component<>(context.executor(), List.of(Column.values()));
        this.cwd = context.cwd();
        final Model model = new Model();
        this.table = JTables.builder(model)
                            .setRowSelectionAllowed(true)
                            .setColumnSelectionAllowed(false)
                            .setAutoCreateRowSorter(true)
                            .setAutoResizeMode(AUTO_RESIZE_OFF)
                            .build();
        this.scrollPane = new JScrollPane(table);
        context.tableViewConfig().optColumnWidth().subscribe(INIT, this::resizeColumns);
    }

    private void resizeColumns(final Instant ignored) {
        IntStream.range(0, table.getColumnCount())
                 .map(table::convertColumnIndexToModel)
                 .forEach(this::resizeColumn);
    }

    private void resizeColumn(final int colIndex) {
        final TableColumn column = table.getColumnModel().getColumn(colIndex);
        final TableCellRenderer headRenderer = Optional.ofNullable(column.getHeaderRenderer())
                                                       .orElseGet(() -> table.getTableHeader()
                                                                             .getDefaultRenderer());
        final java.awt.Component head = headRenderer.getTableCellRendererComponent(
                table, column.getHeaderValue(), false, false, 0, colIndex);
        final int maxWidth = IntStream.range(0, table.getRowCount())
                                      .map(rowIndex -> preferredWidth(colIndex, rowIndex))
                                      .reduce(head.getPreferredSize().width, Math::max);
        column.setPreferredWidth(maxWidth + 12);
    }

    private int preferredWidth(final int colIndex, final int rowIndex) {
        final TableCellRenderer cellRenderer = table.getCellRenderer(rowIndex, colIndex);
        final java.awt.Component cell = table.prepareRenderer(cellRenderer, rowIndex, colIndex);
        return cell.getPreferredSize().width;
    }

    public static FileTable by(final Context context) {
        return new FileTable(context);
    }

    public Variable<Mode> mode() {
        return mode;
    }

    public Variable<List<Column>> columns() {
        return columns;
    }

    public JComponent ui() {
        return scrollPane;
    }

    public enum Mode {

        FLAT(entry -> LISTER.list(entry).stream()),
        DEEP(STREAMER::stream);

        private final Function<? super FileEntry, ? extends Stream<FileEntry>> streaming;

        Mode(final Function<? super FileEntry, ? extends Stream<FileEntry>> streaming) {
            this.streaming = streaming;
        }

        private Stream<FileEntry> stream(final Path path) {
            return streaming.apply(FileEntry.resolved(path));
        }
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    public enum Column implements RowColumnModel.Column<Entry> {

        NAME(new Backing<>("Name", Property.Name.class, Entry::name)),
        LOCATION(new Backing<>("Location", Property.Location.class, Entry::location)),
        LAST_MODIFIED(new Backing<>("Last Modified", Property.LastModified.class, Entry::lastModified)),
        SIZE(new Backing<>("Size", Property.Size.class, Entry::size));

        private final Backing<?> backing;

        <P> Column(final Backing<P> backing) {
            this.backing = backing;
        }

        @Override
        public String title() {
            return backing.title;
        }

        @Override
        public Class<?> type() {
            return backing.type;
        }

        @SuppressWarnings("ClassEscapesDefinedScope")
        @Override
        public Object map(final Entry element) {
            return backing.mapping.apply(element);
        }

        private record Backing<P>(String title, Class<P> type, Function<Entry, P> mapping) {
        }
    }

    @SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
    public interface Context {

        Executor executor();

        FilesIcons icons();

        Variable<Path> cwd();

        TableViewConfig tableViewConfig();
    }

    private record Entry(Variable<Path> cwd, FileEntry entry) {

        private Property.Name name() {
            return new Property.Name(this);
        }

        private Property.Location location() {
            return new Property.Location(this);
        }

        private Property.LastModified lastModified() {
            return new Property.LastModified(this);
        }

        private Property.Size size() {
            return new Property.Size(this);
        }
    }

    private abstract static class Property<P extends Property<P>> extends CellProperty<P> {

        private static final Locale LOCALE = Locale.getDefault();
        private static final ZoneId ZONE_ID = ZoneId.systemDefault();

        private final Entry entry;

        Property(final Entry entry, final Class<P> finalClass, final Comparator<FileEntry> order) {
            super(finalClass, comparing(Property::fileEntry, order));
            this.entry = entry;
        }

        static LocalDateTime localDateTime(final Instant instant) {
            return LocalDateTime.ofInstant(instant, ZONE_ID);
        }

        final FileEntry fileEntry() {
            return entry.entry;
        }

        final Path cwd() {
            return entry.cwd.get();
        }

        @SuppressWarnings("EqualsDoesntCheckParameterClass")
        @Override
        public final boolean equals(final Object other) {
            return equals(THIS(), other);
        }

        @Override
        public final int hashCode() {
            return fileEntry().path().hashCode();
        }

        private static class Name extends Property<Name> {

            Name(final Entry entry) {
                super(entry, Name.class, EntryOrder.BY_TYPE_NAME);
            }

            @Override
            public final String toString() {
                return fileEntry().name();
            }
        }

        private static class Location extends Property<Location> {

            private final Path location;

            Location(final Entry entry) {
                super(entry, Location.class, EntryOrder.BY_PATH);
                this.location = cwd().relativize(fileEntry().path().getParent());
            }

            @Override
            public final String toString() {
                return Optional.of(location.toString())
                               .filter(not(String::isBlank))
                               .orElse(".");
            }
        }

        private static class LastModified extends Property<LastModified> {

            private static final DateTimeFormatter DATE_TIME_FORMATTER =
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                     .withLocale(LOCALE);

            private final LocalDateTime dateTime;

            LastModified(final Entry entry) {
                super(entry, LastModified.class, EntryOrder.BY_DATE);
                this.dateTime = localDateTime(fileEntry().lastModified());
            }

            @Override
            public final String toString() {
                return dateTime.format(DATE_TIME_FORMATTER);
            }
        }

        private static class Size extends Property<Size> {

            Size(final Entry entry) {
                super(entry, Size.class, EntryOrder.BY_SIZE);
            }

            @Override
            public final String toString() {
                return "%,d".formatted(fileEntry().size());
            }
        }
    }

    private class Model extends RowColumnModel<Entry> {

        private volatile List<Entry> entries = List.of();

        Model() {
            cwd.subscribe(INIT, newPath -> onSetPath(newPath, mode.get()));
            mode.subscribe(newMode -> onSetPath(cwd.get(), newMode));
            columns.subscribe(ignored -> fireTableStructureChanged());
        }

        private void onSetPath(final Path newPath, final Mode newMode) {
            this.entries = newMode.stream(newPath)
                                  .map(entry -> entry.isMissing() ? entry.original() : entry)
                                  .map(entry -> new Entry(cwd, entry))
                                  .toList();
            fireTableDataChanged();
        }

        @Override
        protected final List<Entry> rows() {
            // Already IS immutable ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return entries;
        }

        @Override
        protected final List<FileTable.Column> columns() {
            return columns.get();
        }
    }
}