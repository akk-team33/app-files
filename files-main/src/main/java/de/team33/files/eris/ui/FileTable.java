package de.team33.files.eris.ui;

import de.team33.patterns.io.delta.FileEntry;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.delta.table.RowColumnModel;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Stream;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

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

    public Variable<List<Column>> columns() {
        return columns;
    }

    public JComponent ui() {
        return scrollPane;
    }

    public enum Mode {

        FLAT(FileEntry::entries),
        DEEP(Mode::deep);

        private final Function<? super FileEntry, ? extends Stream<FileEntry>> streaming;

        Mode(final Function<? super FileEntry, ? extends Stream<FileEntry>> streaming) {
            this.streaming = streaming;
        }

        private static Stream<FileEntry> deep(final FileEntry entry) {
            return entry.entries()
                        .flatMap(Mode::content);
        }

        private static Stream<FileEntry> content(final FileEntry entry) {
            final Stream<FileEntry> head = Stream.of(entry);
            if (entry.isDirectory()) {
                return Stream.concat(head, deep(entry));
            } else {
                return head;
            }
        }

        private Stream<FileEntry> stream(final Path path) {
            return streaming.apply(FileEntry.of(path));
        }
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    public enum Column implements RowColumnModel.Column<Entry> {

        NAME(new Backing<>("Name", Property.Name.class, Entry::name)),
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
    public interface Icons {

        Icon stdFolder();

        Icon stdFile();
    }

    @SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
    public interface Context {

        Executor executor();

        Icons icons();

        Variable<Path> cwd();
    }

    @SuppressWarnings({"MethodMayBeStatic", "ClassCanBeRecord"})
    private static final class Entry {

        @SuppressWarnings("FieldCanBeLocal")
        private final Variable<Path> cwd;
        @SuppressWarnings("FieldCanBeLocal")
        private final FileEntry entry;

        private Entry(final Variable<Path> cwd, final FileEntry entry) {
            this.cwd = cwd;
            this.entry = entry;
        }

        private Property.Name name() {
            return new Property.Name();
        }

        private Property.LastModified lasModified() {
            return new Property.LastModified();
        }

        private Property.Size size() {
            return new Property.Size();
        }
    }

    @SuppressWarnings("EmptyClass")
    private static class Property {

        private static class Name {
        }

        private static class Location {
        }

        private static class LastModified {
        }

        private static class Size {
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