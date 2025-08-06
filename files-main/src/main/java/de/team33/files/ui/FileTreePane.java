package de.team33.files.ui;

import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.alpha.activity.Event;
import de.team33.sphinx.alpha.visual.JScrollPanes;
import de.team33.sphinx.alpha.visual.JTrees;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import java.nio.file.Path;
import java.util.stream.IntStream;

public class FileTreePane extends JScrollPane {

    private static final String[] EMPTY = {};

    private FileTreePane(final JTree fileTree) {
        super(fileTree);
    }

    public static FileTreePane using(final Variable<Path> path) {
        return JScrollPanes.builder(() -> new FileTreePane(fileTree(path)))
                           .build();
    }

    private static FileTree fileTree(final Variable<Path> path) {
        return JTrees.builder(FileTree::new)
                     .setCellRenderer(Basics.treeCellRenderer())
                     .setup(tree -> path.retrieve(tree::setPath))
                     .on(Event.TREE_VALUE_CHANGED, event -> onTreeValueChanged(event, path))
                     .build();
    }

    private static void onTreeValueChanged(final TreeSelectionEvent event, final Variable<Path> path) {
        if (event.getSource() instanceof FileTree tree) {
            path.set(toPath(event.getPath()));
        }
    }

    private static Path toPath(final TreePath treePath) {
        return IntStream.range(0, treePath.getPathCount())
                        .mapToObj(treePath::getPathComponent)
                        .map(Object::toString)
                        .map(Path::of)
                        .reduce(Path::resolve)
                        .orElseThrow();
    }

    private static TreePath toTreePath(final Path path) {
        final Path parent = path.getParent();
        if (null == parent) {
            return new TreePath(path.toString());
        } else {
            return toTreePath(parent).pathByAddingChild(path.getFileName().toString());
        }
    }

    private static class FileTree extends JTree {

        private void setPath(final Path path) {
            setSelectionPath(toTreePath(path));
        }
    }
}
