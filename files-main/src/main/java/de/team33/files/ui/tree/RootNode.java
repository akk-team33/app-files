package de.team33.files.ui.tree;

import de.team33.files.ui.FileTreeModel;
import de.team33.patterns.io.phobos.FileEntry;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;

public class RootNode implements FileTreeModel.Node {

    private final List<FileTreeModel.Node> roots;

    {
        this.roots = StreamSupport.stream(FileSystems.getDefault().getRootDirectories().spliterator(), false)
                                  .map(FileEntry::of)
                                  .map(FileEntry::resolved)
                                  .map(FileNode::new).map(FileTreeModel.Node.class::cast)
                                  .toList();
    }

    @Override
    public final FileTreeModel.Node child(final int index) {
        return roots.get(index);
    }

    @Override
    public final int childCount() {
        return roots.size();
    }

    @Override
    public final boolean isLeaf() {
        return false;
    }

    @Override
    public final int indexOf(final FileTreeModel.Node child) {
        return roots.indexOf(child);
    }

    @Override
    public final Path path() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
