package de.team33.files.gamma;

import de.team33.files.gamma.busyness.Service;
import de.team33.files.gamma.ui.FilesFrame;
import de.team33.patterns.serving.alpha.Variable;

import java.nio.file.Path;

@SuppressWarnings("ClassNameSameAsAncestorName")
class Context implements FilesFrame.Context {

    private final Icons icons = new Icons();
    private final Service service = new Service();

    @Override
    public final Icons icons() {
        return icons;
    }

    @Override
    public final Variable<Path> cwd() {
        return service.cwd();
    }
}
