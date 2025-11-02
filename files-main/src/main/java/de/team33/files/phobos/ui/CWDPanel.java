package de.team33.files.phobos.ui;

import de.team33.files.phobos.model.History;
import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JButtons;
import de.team33.sphinx.metis.JPanels;
import de.team33.sphinx.metis.JTextFields;
import net.team33.fscalc.ui.rsrc.Ico;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.nio.file.Path;
import java.util.Objects;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

final class CWDPanel {

    private final Variable<Path> cwd;
    private final JButton bckButton;
    private final JButton fwdButton;
    private final JButton upButton;
    private final JTextField textField;
    private final JPanel jPanel;
    private final Variable<History> history;
    private volatile String lastText = "";

    private CWDPanel(final Context context) {
        final GridBag gridBag = new GridBag();
        final JButton rldButton = JButtons.builder()
                                          .setIcon(Ico.RELOAD)
                                          .build();

        cwd = context.cwd();
        history = context.history();
        bckButton = JButtons.builder()
                            .setIcon(Ico.PRED)
                            .setToolTipText("Go back in history")
                            .subscribe(Channel.ACTION_PERFORMED, this::onBckAction)
                            .build();
        fwdButton = JButtons.builder()
                            .setIcon(Ico.SUCC)
                            .setToolTipText("Go forward in history")
                            .subscribe(Channel.ACTION_PERFORMED, this::onFwdAction)
                            .build();
        upButton = JButtons.builder()
                           .setIcon(Ico.UPDIR)
                           .setToolTipText("Switch to parent directory")
                           .subscribe(Channel.ACTION_PERFORMED, this::onUpAction)
                           .build();
        textField = JTextFields.builder()
                               .subscribe(Channel.FOCUS_GAINED, this::onInputFocusGained)
                               .subscribe(Channel.FOCUS_LOST, this::onInputConfirmed)
                               .subscribe(Channel.JTF_ACTION_PERFORMED, this::onInputConfirmed)
                               .build();
        jPanel = JPanels.builder(new GridBagLayout())
                        .add(bckButton, gridBag.nextConstraints(0.0))
                        .add(fwdButton, gridBag.nextConstraints(0.0))
                        .add(upButton, gridBag.nextConstraints(0.0))
                        //.add(new JLabel(Ico.OPNDIR), gridBag.nextConstraints(0.0))
                        .add(textField, gridBag.nextConstraints(1.0))
                        .add(rldButton, gridBag.nextConstraints(0.0))
                        .build();
        cwd.subscribe(INIT, this::onSetCWD);
        history.subscribe(INIT, this::onSetHistory);
    }

    static CWDPanel by(final Context context) {
        return new CWDPanel(context);
    }

    private void onSetHistory(final History history) {
        bckButton.setEnabled(history.hasPrev());
        fwdButton.setEnabled(history.hasNext());
    }

    private void onFwdAction(final ActionEvent event) {
        history.set(history.get().next());
    }

    private void onBckAction(final ActionEvent event) {
        history.set(history.get().prev());
    }

    private void onUpAction(final ActionEvent event) {
        cwd.set(cwd.get().getParent());
    }

    private void onSetCWD(final Path path) {
        upButton.setEnabled(null != path.getParent());
        textField.setText(path.toString());
    }

    private void onInputFocusGained(final FocusEvent event) {
        lastText = textField.getText();
    }

    private void onInputConfirmed(final AWTEvent event) {
        if (!Objects.equals(lastText, textField.getText())) {
            cwd.set(Path.of(textField.getText()));
            if (textField.isFocusOwner()) {
                textField.transferFocus();
                textField.requestFocusInWindow();
            }
        }
    }

    final JPanel ui() {
        return jPanel;
    }

    @SuppressWarnings("InterfaceWithOnlyOneDirectInheritor")
    interface Context {

        Variable<Path> cwd();

        Variable<History> history();
    }

    private static class GridBag {

        private static final Insets INSETS = new Insets(0, 0, 0, 0);
        private static final int PAD_XY = 0;
        private static final int ANCHOR = GridBagConstraints.CENTER;
        private static final int FILL = GridBagConstraints.BOTH;

        private int x = 0;

        private GridBagConstraints nextConstraints(final double weight) {
            //noinspection ValueOfIncrementOrDecrementUsed
            return new GridBagConstraints(x++, 0, 1, 1, weight, 0.0, ANCHOR, FILL, INSETS, PAD_XY, PAD_XY);
        }
    }
}
