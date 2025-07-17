package de.team33.files.ui;

import de.team33.sphinx.alpha.activity.Event;
import de.team33.sphinx.alpha.visual.JButtons;
import de.team33.sphinx.alpha.visual.JPanels;
import net.team33.fscalc.ui.InfoTable;
import net.team33.fscalc.work.Context;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

final class ControlPane extends JPanel {

    private ControlPane() {
        super(new GridLayout(1, 0, 2, 2));
    }

    static ControlPane by(final InfoTable table, final Context context) {
        return JPanels.builder(ControlPane::new)
                      .add(delButton(table, context))
                      .build();
    }

    private static JButton delButton(final InfoTable table, final Context context) {
        return JButtons.builder()
                       .setText("Delete")
                       .on(Event.ACTION_PERFORMED, message -> onDelButtonPressed(table, context))
                       .setup(button -> table.getRegister()
                                             .add(new UpdateSelectionListener(button)))
                       .build();
    }

    private static void onDelButtonPressed(final InfoTable table, final Context context) {
        final int[] rows = table.getSelectedRows();
        final File[] paths = new File[rows.length];
        for (int i = 0; i < rows.length; ++i) {
            paths[i] = table.getModel().getValueAt(rows[i], 0).getPath();
        }
        context.startDeletion(paths);
    }

    private record UpdateSelectionListener(JButton button) implements Consumer<InfoTable.UpdateSelection> {

        @Override
        public final void accept(final InfoTable.UpdateSelection message) {
            button.setEnabled(0 < message.getSender().getSelectedRowCount());
        }
    }
}
