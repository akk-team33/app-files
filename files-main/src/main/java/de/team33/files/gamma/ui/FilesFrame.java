package de.team33.files.gamma.ui;

import de.team33.sphinx.metis.JFrames;
import de.team33.sphinx.metis.JSplitPanes;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

public final class FilesFrame {

    private final JFrame main;

    private FilesFrame(final Context context) {
        this.main = JFrames.builder()
                           .setLayout(new BorderLayout())
                           .add(CWDInput.with(context).ui(), BorderLayout.PAGE_START)
                           .add(JSplitPanes.builder()
                                           .setLeftComponent(FileTree.with(context).ui())
                                           .setRightComponent(FileTablePanel.with(context).ui())
                                           .build(), BorderLayout.CENTER)
                           .add(FilesStatus.with(context).ui(), BorderLayout.PAGE_END)
                           .build();
        context.cwd().subscribe(INIT, this::onSetSWD);
    }

    private void onSetSWD(final Path path) {
        main.setTitle("%s - Files".formatted(path));
    }

    public static FilesFrame with(final Context context) {
        return new FilesFrame(context);
    }

    public final JFrame ui() {
        return main;
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "InterfaceWithOnlyOneDirectInheritor"})
    public interface Icons extends FileTree.Icons, FileTable.Icons {
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "InterfaceWithOnlyOneDirectInheritor"})
    public interface Context extends CWDInput.Context,
                                     FileTree.Context,
                                     FileTablePanel.Context,
                                     FilesStatus.Context {

        @Override
        Icons icons();
    }
}
