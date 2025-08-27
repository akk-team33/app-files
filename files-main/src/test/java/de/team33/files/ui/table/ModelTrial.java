package de.team33.files.ui.table;

import de.team33.files.ui.Context;
import de.team33.files.ui.FileTree;
import de.team33.files.uix.SwingTrial;
import de.team33.sphinx.metis.JSplitPanes;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

final class ModelTrial extends SwingTrial {

    private static final List<FileColumn<?>> COLUMNS = List.of(
            new FileColumn<>("Name", String.class, File::getName),
            new FileColumn<>("Last Modified", Instant.class, file -> Instant.ofEpochMilli(file.lastModified())),
            new FileColumn<>("Size", Long.class, File::length));

    private final Context context = new Context();
    private final Model<File> model = new FileModel(context);
    private final JTable fileTable = JTables.builder(model)
                                            .setAutoCreateRowSorter(true)
                                            .build();

    public static void main(final String[] args) {
        run(new ModelTrial());
    }

    @Override
    protected Container contentPane() {
        return JSplitPanes.builder()
                          .setLeftComponent(FileTree.by(context).component())
                          .setRightComponent(new JScrollPane(fileTable))
                          .build();
    }

    @Override
    protected void setupFrame(final JFrame jFrame) {
        context.cwd().subscribe(INIT, path -> jFrame.setTitle(path.toString()));
    }

    private record FileColumn<C extends Comparable<C>>(String title, Class<C> type, Function<File, C> mapping)
            implements Model.Column<File, C> {

        @Override
        public C map(final File file) {
            return mapping.apply(file);
        }
    }

    private static class FileModel extends Model<File> {

        private volatile List<File> files = List.of();

        FileModel(final Context context) {
            super(COLUMNS);
            context.cwd().subscribe(INIT, this::onSetCWD);
        }

        private void onSetCWD(final Path path) {
            this.files = List.of(path.toFile().listFiles());
            fireTableDataChanged();
        }

        @Override
        protected final List<File> list() {
            return files;
        }
    }
}