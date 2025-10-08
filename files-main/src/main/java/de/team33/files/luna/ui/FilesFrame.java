package de.team33.files.luna.ui;

import de.team33.files.eris.ui.CWDInput;
import de.team33.files.eris.ui.FileTree;
import de.team33.sphinx.metis.JFrames;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

public final class FilesFrame {

    private final JFrame frame;

    private FilesFrame(final Context context) {
        this.frame = JFrames.builder()
                            .setLayout(new BorderLayout())
                            .add(CWDInput.by(context).ui(), BorderLayout.PAGE_START)
                            .add(CenterPane.by(context).ui(), BorderLayout.CENTER)
                            .add(FilesStatus.by(context).ui(), BorderLayout.PAGE_END)
                            .build();
        context.cwd().subscribe(INIT, this::onSetCWD);
    }

    private void onSetCWD(final Path path) {
        frame.setTitle("%s - Files".formatted(path.getFileName()));
    }

    public static FilesFrame by(final Context context) {
        return new FilesFrame(context);
    }

    public final JFrame ui() {
        return frame;
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "InterfaceWithOnlyOneDirectInheritor", "MarkerInterface"})
    public interface Icons extends FileTree.Icons {
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "InterfaceWithOnlyOneDirectInheritor"})
    public interface Context extends CWDInput.Context, CenterPane.Context, FilesStatus.Context {

        @Override
        Icons icons();
    }
}
