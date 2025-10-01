package de.team33.files.gamma;

import de.team33.files.gamma.busyness.Service;
import de.team33.files.gamma.ui.FilesFrame;

@SuppressWarnings("ClassNameSameAsAncestorName")
class Context implements FilesFrame.Context {

    private final Service service = new Service();
}
