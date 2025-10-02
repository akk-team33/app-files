package de.team33.files.gamma;

import de.team33.files.gamma.busyness.Service;
import de.team33.files.gamma.ui.FilesFrame;
import de.team33.patterns.serving.alpha.Variable;
import net.team33.fscalc.ui.rsrc.Ico;

import javax.swing.*;
import java.nio.file.Path;

@SuppressWarnings("ClassNameSameAsAncestorName")
class Context implements FilesFrame.Context {

    private final FilesFrame.Icons icons = new Icons();
    private final Service service = new Service();

    @Override
    public final FilesFrame.Icons icons() {
        return icons;
    }

    @Override
    public final Variable<Path> cwd() {
        return service.cwd();
    }

    private static class Icons implements FilesFrame.Icons {

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
    }
}
