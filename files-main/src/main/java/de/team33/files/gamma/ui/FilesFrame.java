package de.team33.files.gamma.ui;

import de.team33.sphinx.metis.JFrames;
import de.team33.sphinx.metis.JSplitPanes;

import javax.swing.*;
import java.awt.*;

public final class FilesFrame {

    private final JFrame main;

    private FilesFrame(final Context context) {
        this.main = JFrames.builder()
                           .setLayout(new BorderLayout())
                           .add(CWDInput.with(context).main(), BorderLayout.PAGE_START)
                           .add(JSplitPanes.builder()
                                           .setLeftComponent(FileTree.with(context).main())
                                           .setRightComponent(FileTable.with(context).main())
                                           .build(), BorderLayout.CENTER)
                           .add(FilesStatus.with(context).main(), BorderLayout.PAGE_END)
                           .build();
    }

    public static FilesFrame with(final Context context) {
        return new FilesFrame(context);
    }

    public final JFrame main() {
        return main;
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "InterfaceWithOnlyOneDirectInheritor"})
    public interface Context extends CWDInput.Context, FileTree.Context, FileTable.Context, FilesStatus.Context {

    }
}
