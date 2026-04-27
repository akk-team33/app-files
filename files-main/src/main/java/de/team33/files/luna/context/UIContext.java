package de.team33.files.luna.context;

import de.team33.files.luna.ui.FilesIcons;
import de.team33.patterns.serving.alpha.Variable;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public interface UIContext {

    Executor executor();

    FilesIcons icons();

    Variable<Path> cwd();

    TableViewConfig tableViewConfig();
}
