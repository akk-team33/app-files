package de.team33.files.phobos.ui;

import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.metis.JFrames;
import de.team33.sphinx.metis.JPanels;
import de.team33.sphinx.metis.JSplitPanes;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

public final class Frame {

    private final JFrame jFrame;

    private Frame(final Context context) {
        final CWDPanel cwdPanel = CWDPanel.by(context);
        final JTree treeView = new JTree();
        final JScrollPane treePane = new JScrollPane(treeView);
        final JTable tableView = new JTable();
        final JScrollPane tableMain = new JScrollPane(tableView);
        final JPanel tablePane = JPanels.builder(new BorderLayout())
                                        .add(new JLabel("Table Menu"), BorderLayout.NORTH)
                                        .add(tableMain, BorderLayout.CENTER)
                                        .build();
        final JSplitPane centerPane = JSplitPanes.builder()
                                                 .setLeftComponent(treePane)
                                                 .setRightComponent(tablePane)
                                                 .build();
        final JPanel southPane = JPanels.builder(new BorderLayout())
                                        .add(new JLabel("Left"), BorderLayout.WEST)
                                        .add(new JLabel("Center"), BorderLayout.CENTER)
                                        .add(new JLabel("Right"), BorderLayout.EAST)
                                        .build();
        jFrame = JFrames.builder("Files")
                        .setLayout(new BorderLayout())
                        .add(cwdPanel.ui(), BorderLayout.NORTH)
                        .add(centerPane, BorderLayout.CENTER)
                        .add(southPane, BorderLayout.SOUTH)
                        .build();
        context.cwd().subscribe(Retrievable.Mode.INIT, this::onSetCWD);
    }

    private void onSetCWD(final Path path) {
        jFrame.setTitle("%s - Files".formatted(path));
    }

    public static Frame by(final Context context) {
        return new Frame(context);
    }

    public final JFrame ui() {
        return jFrame;
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "InterfaceWithOnlyOneDirectInheritor"})
    @FunctionalInterface
    public interface Context extends CWDPanel.Context {

    }
}
