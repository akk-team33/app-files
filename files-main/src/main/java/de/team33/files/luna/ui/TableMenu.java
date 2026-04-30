package de.team33.files.luna.ui;

import de.team33.files.luna.context.TableViewConfig;
import de.team33.patterns.serving.alpha.Retrievable;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JButtons;
import de.team33.sphinx.metis.JPanels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Instant;

import static de.team33.files.luna.context.TableViewConfig.Depth.DEEP;
import static de.team33.files.luna.context.TableViewConfig.Depth.FLAT;

public final class TableMenu {

    private final TableViewConfig config;
    private final JPanel uiComponent;
    private final JButton depthButton;

    private TableMenu(final TableViewConfig config) {
        this.config = config;
        this.depthButton = JButtons.builder()
                                   .setText("flat")
                                   .setToolTipText("Optimize Column Width")
                                   .subscribe(Channel.ACTION_PERFORMED, this::switchDepth)
                                   .build();
        this.uiComponent = JPanels.builder(new FlowLayout()) //(new BorderLayout())
                                  .add(JButtons.builder()
                                               .setText("|↔|")
                                               .setToolTipText("Optimize Column Width")
                                               // TODO?: .setMnemonic(KeyEvent.VK_)
                                               .subscribe(Channel.ACTION_PERFORMED, this::onOptColumnWidth)
                                               .build()) //, BorderLayout.LINE_START)
                                  .add(depthButton) //, BorderLayout.LINE_START)
                                  .build();
        config.depth().subscribe(Retrievable.Mode.INIT, this::onSwitchDepth);
    }

    private void onSwitchDepth(final TableViewConfig.Depth depth) {
        switch (depth) {
            case FLAT -> JButtons.setup(depthButton)
                                 .setText("deep")
                                 .setToolTipText("Show files recursive");
            case DEEP -> JButtons.setup(depthButton)
                                 .setText("flat")
                                 .setToolTipText("Show files non-recursive");
        }
    }

    private void switchDepth(final ActionEvent event) {
        config.depth().set(FLAT == config.depth().get() ? DEEP : FLAT);
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