package de.team33.files.alpha.ui;

import de.team33.patterns.serving.alpha.Variable;
import de.team33.sphinx.lambda.SwingApp;
import de.team33.sphinx.luna.Channel;
import de.team33.sphinx.metis.JCheckBoxes;
import de.team33.sphinx.metis.JFrames;
import de.team33.sphinx.metis.JPanels;
import de.team33.sphinx.metis.JSplitPanes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static de.team33.patterns.serving.alpha.Retrievable.Mode.INIT;

final class FileTableTrial extends SwingApp {

    private final Context context = new Context();

    public static void main(final String[] args) {
        start(new FileTableTrial());
    }

    private static Component newColumnsPane(final FileTable fileTable) {
        final JPanel jPanel = new JPanel(new GridLayout(FileTable.Column.values().length, 1));
        Stream.of(FileTable.Column.values())
              .map(column -> newCheckBox(fileTable, column))
              .forEach(jPanel::add);
        return jPanel;
    }

    private static JCheckBox newCheckBox(final FileTable fileTable, final FileTable.Column column) {
        return JCheckBoxes.builder()
                          .setText(column.title())
                          .setup(jCheckBox -> fileTable.columns()
                                                       .subscribe(INIT,
                                                                  columns -> jCheckBox.setSelected(columns.contains(column))))
                          .subscribe(Channel.ACTION_PERFORMED,
                                     event -> onCheckColumn(event, fileTable.columns(), column))
                          .build();
    }

    private static void onCheckColumn(final ActionEvent event,
                                      final Variable<List<FileTable.Column>> columns,
                                      final FileTable.Column column) {
        if (event.getSource() instanceof final JCheckBox box) {
            columns.set(toggle(columns.get(), column));
        }
    }

    private static <E> List<E> toggle(final List<E> list, final E element) {
        final List<E> result = new ArrayList<>(list);
        if (list.contains(element)) {
            result.remove(element);
        } else {
            result.add(element);
        }
        return result;
    }

    @Override
    protected JFrame newFrame() {
        return JFrames.builder(getClass().getCanonicalName())
                      .setContentPane(newContentPane())
                      .setup(jFrame -> context.cwd().subscribe(INIT, path -> jFrame.setTitle(path.toString())))
                      .build();
    }

    private Container newContentPane() {
        final FileTable fileTable = FileTable.by(context);
        final JPanel optionPane = JPanels.builder(new BorderLayout())
                                         .add(newColumnsPane(fileTable), BorderLayout.NORTH)
                                         .build();
        return JPanels.builder(new BorderLayout())
                      .add(JSplitPanes.builder()
                                      .setLeftComponent(FileTree.by(context).component())
                                      .setRightComponent(fileTable.panel())
                                      .build(), BorderLayout.CENTER)
                      .add(optionPane, BorderLayout.EAST)
                      .build();
    }
}