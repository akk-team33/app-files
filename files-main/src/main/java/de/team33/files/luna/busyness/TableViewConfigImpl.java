package de.team33.files.luna.busyness;

import de.team33.files.luna.context.TableViewConfig;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;

import java.time.Instant;

public class TableViewConfigImpl implements TableViewConfig {

    private final Component<Instant> optColumnWidth = new Component<>(Instant.now());

    @Override
    public final Variable<Instant> optColumnWidth() {
        return optColumnWidth;
    }
}
