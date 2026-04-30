package de.team33.files.luna.busyness;

import de.team33.files.luna.context.TableViewConfig;
import de.team33.patterns.serving.alpha.Component;
import de.team33.patterns.serving.alpha.Variable;

import java.time.Instant;

public class TableViewConfigImpl implements TableViewConfig {

    private final Variable<Instant> optColumnWidth = new Component<>(Instant.now());
    private final Variable<Depth> depth = new Component<>(Depth.FLAT);

    @Override
    public final Variable<Instant> optColumnWidth() {
        return optColumnWidth;
    }

    @Override
    public Variable<Depth> depth() {
        return depth;
    }
}
