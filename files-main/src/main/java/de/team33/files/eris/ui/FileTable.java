package de.team33.files.eris.ui;

import de.team33.files.luna.ui.FilesIcons;
import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.io.adrastea.LinkHandling;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.epsilon.table.CellProperty;
import de.team33.sphinx.epsilon.table.RowColumnModel;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
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
import java.util.stream.Stream;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static java.util.Comparator.comparing;
import static java.util.function.Predicate.not;

@SuppressWarnings("unused")
public final class FileTable {

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
        this.table = JTables.builder(new Model())
                            .build();
        this.scrollPane = new JScrollPane(table);
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

    private static final FileEntry.Lister LISTER = FileEntry.lister(LinkHandling.RESOLVE);
    private static final FileEntry.Streamer STREAMER = FileEntry.streamer(LISTER);

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
        LAST_MODIFIED(new Backing<>("Last Modified", Property.LastModified.class, Entry::lasModified)),
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
    }

    private record Entry(Variable<Path> cwd, FileEntry entry) {

        private Property.Name name() {
            return new Property.Name(this);
        }

        private Property.Location location() {
            return new Property.Location(this);
        }

        private Property.LastModified lasModified() {
            return new Property.LastModified(this);
        }

        private Property.Size size() {
            return new Property.Size(this);
        }
    }

    private abstract static class Property<P extends Property<P>> extends CellProperty<P> {

        private static final Locale LOCALE = Locale.getDefault();
        private static final ZoneId ZONE_ID = ZoneId.systemDefault();
        private static final Comparator<String> IGNORE_CASE = String::compareToIgnoreCase;
        private static final Comparator<String> RESPECT_CASE = String::compareTo;
        private static final Comparator<String> STRING_ORDER = IGNORE_CASE.thenComparing(RESPECT_CASE);
        private static final Comparator<Path> PATH_ORDER = comparing(Path::toString, STRING_ORDER);
        private static final Comparator<FileEntry> FINAL_ORDER = comparing(FileEntry::path, PATH_ORDER);

        private final Entry entry;

        Property(final Entry entry, final Class<P> finalClass, final Comparator<FileEntry> primeOrder) {
            super(finalClass, comparing(Property::fileEntry, primeOrder.thenComparing(FINAL_ORDER)));
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
            return CellProperty.equals(THIS(), other);
        }

        @Override
        public final int hashCode() {
            return fileEntry().path().hashCode();
        }

        private static class Name extends Property<Name> {

            private static final Comparator<FileEntry> PRIME_ORDER = comparing(FileEntry::name, STRING_ORDER);

            Name(final Entry entry) {
                super(entry, Name.class, PRIME_ORDER);
            }

            @Override
            public final String toString() {
                return fileEntry().name();
            }
        }

        private static class Location extends Property<Location> {

            private static final Comparator<FileEntry> PRIME_ORDER = (left, right) -> 0;

            Location(final Entry entry) {
                super(entry, Location.class, PRIME_ORDER);
            }

            @Override
            public final String toString() {
                return Optional.ofNullable(fileEntry().path().getParent())
                               .map(parent -> cwd().relativize(parent).toString())
                               .filter(not(String::isBlank))
                               .orElse(".");
            }
        }

        private static class LastModified extends Property<LastModified> {

            private static final DateTimeFormatter DATE_TIME_FORMATTER =
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                     .withLocale(LOCALE);
            private static final Comparator<FileEntry> ORDER = comparing(FileEntry::lastModified);

            private final LocalDateTime dateTime;

            LastModified(final Entry entry) {
                super(entry, LastModified.class, ORDER);
                this.dateTime = localDateTime(fileEntry().lastModified());
            }

            @Override
            public final String toString() {
                return dateTime.format(DATE_TIME_FORMATTER);
            }
        }

        private static class Size extends Property<Size> {

            private static final Comparator<FileEntry> ORDER = comparing(FileEntry::size);

            Size(final Entry entry) {
                super(entry, Size.class, ORDER);
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