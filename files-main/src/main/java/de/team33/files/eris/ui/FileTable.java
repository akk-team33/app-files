package de.team33.files.eris.ui;

import de.team33.files.eris.ui.table.Model;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.metis.JTables;

import javax.swing.*;
import java.nio.file.Path;
import java.util.concurrent.Executor;

public final class FileTable {

    private final JTable table;
    private final JScrollPane scrollPane;

    private FileTable(final Context context) {
        this.table = JTables.builder(new Model(context))
                            .build();
        this.scrollPane = new JScrollPane(table);
    }

    public static FileTable by(final Context context) {
        return new FileTable(context);
    }

    public JComponent ui() {
        return scrollPane;
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
}