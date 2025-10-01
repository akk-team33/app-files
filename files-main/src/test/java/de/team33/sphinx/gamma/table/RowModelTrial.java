package de.team33.sphinx.gamma.table;

import de.team33.files.alpha.ui.Context;
import de.team33.files.alpha.ui.FileTree;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.lambda.SwingApp;
import de.team33.sphinx.metis.JFrames;
import de.team33.sphinx.metis.JSplitPanes;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;
import static javax.swing.JTable.AUTO_RESIZE_OFF;
import static javax.swing.SwingConstants.*;

final class RowModelTrial extends SwingApp {

    @SuppressWarnings("StaticCollection")
    private static final List<FileColumn<?>> COLUMNS = List.of(
            new FileColumn<>("Name", String.class, LEFT, File::getName),
            new FileColumn<>("Last Modified", Instant.class, CENTER, file -> Instant.ofEpochMilli(file.lastModified())),
            new FileColumn<>("Size", Long.class, RIGHT, File::length));

    private final FileTree.Context context = new Context();
    private final TableModel model = new FileModel(context.cwd());
    private final JTable fileTable = JTables.builder(model)
                                            .setAutoCreateRowSorter(true)
                                            .setAutoResizeMode(AUTO_RESIZE_OFF)
                                            .build();

    public static void main(final String[] args) {
        start(new RowModelTrial());
    }

    @Override
    protected JFrame newFrame() {
        return JFrames.builder(getClass().getCanonicalName())
                      .setContentPane(JSplitPanes.builder()
                                                 .setLeftComponent(FileTree.by(context).component())
                                                 .setRightComponent(new JScrollPane(fileTable))
                                                 .build())
                      .setup(jFrame -> context.cwd().subscribe(INIT, path -> jFrame.setTitle(path.toString())))
                      .build();
    }

    private record FileColumn<C>(String title, Class<C> type, int horizontalAlignment, Function<File, C> mapping)
            implements RowModel.Column<File> {

        @Override
        public C map(final File element) {
            return mapping.apply(element);
        }
    }

    private static class FileModel extends RowModel<File> {

        private volatile List<File> files = List.of();

        FileModel(final Retrievable<? extends Path> cwd) {
            cwd.subscribe(INIT, this::onSetCWD);
        }

        private void onSetCWD(final Path path) {
            this.files = List.of(path.toFile().listFiles());
            fireTableDataChanged();
        }

        @Override
        protected final List<? extends File> rows() {
            // Already is an immutable List ...
            // noinspection AssignmentOrReturnOfFieldWithMutableType
            return files;
        }

        @Override
        protected final List<FileColumn<?>> columns() {
            return COLUMNS;
        }
    }
}