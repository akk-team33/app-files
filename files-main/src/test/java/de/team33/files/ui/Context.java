package de.team33.files.ui;

import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;
import net.team33.fscalc.ui.rsrc.Ico;

import javax.swing.*;
import java.nio.file.Path;
import java.util.List;
import java.util.function.UnaryOperator;

@SuppressWarnings("ClassNameSameAsAncestorName")
public class Context implements FileTree.Context, FileTable.Context {

    private static final UnaryOperator<Path> NORMAL_PATH = path -> path.toAbsolutePath().normalize();

    private static final Icons ICONS = new Icons();

    private final Variable<Path> cwd = new Component<>(NORMAL_PATH, Path.of("."));

    @Override
    public final Icons icons() {
        return ICONS;
    }

    @Override
    public final List<FileTable.Column<?>> columns() {
        // Already is immutable ...
        // noinspection AssignmentOrReturnOfFieldWithMutableType
        return FileTable.Column.VALUES;
    }

    @Override
    public final Variable<Path> cwd() {
        return cwd;
    }

    @SuppressWarnings("ClassNameSameAsAncestorName")
    public static class Icons implements FileTree.Icons, FileTable.Icons {

        @Override
        public final Icon stdFolder() {
            return Ico.CLSDIR;
        }

        @Override
        public final Icon opnFolder() {
            return Ico.OPNDIR;
        }

        @Override
        public final Icon stdFile() {
            return Ico.FILE;
        }

        @Override
        public Icon optWidth() {
            return Ico.RELOAD; // TODO
        }

        @Override
        public Icon parentFolder() {
            return Ico.UPDIR; // TODO?
        }
    }
}
