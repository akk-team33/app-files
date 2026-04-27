package de.team33.files.luna.ui;

import de.team33.files.luna.context.TableViewConfig;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JButtons;
import de.team33.sphinx.metis.JPanels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Instant;

public final class TableMenu {

    private final TableViewConfig config;
    private final JPanel uiComponent;

    private TableMenu(final TableViewConfig config) {
        this.config = config;
        this.uiComponent = JPanels.builder(new BorderLayout())
                                  .add(JButtons.builder()
                                               .setText("|↔|")
                                               .setToolTipText("Optimize Column Width")
                                               // TODO?: .setMnemonic(KeyEvent.VK_)
                                               .subscribe(Channel.ACTION_PERFORMED, this::onOptColumnWidth)
                                               .build(), BorderLayout.LINE_START)
                                  .build();
    }

    public static TableMenu by(final TableViewConfig config) {
        return new TableMenu(config);
    }

    private void onOptColumnWidth(final ActionEvent event) {
        config.optColumnWidth().set(Instant.now());
    }

    public Component ui() {
        return uiComponent;
    }
}