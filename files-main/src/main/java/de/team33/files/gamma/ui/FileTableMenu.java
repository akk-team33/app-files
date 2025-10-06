package de.team33.files.gamma.ui;

import de.team33.sphinx.metis.JMenuBars;
import de.team33.sphinx.metis.JMenuItems;
import de.team33.sphinx.metis.JMenus;

import javax.swing.*;
import java.awt.*;

public final class FileTableMenu {

    private final JMenuBar bar;

    private FileTableMenu(final Context context) {
        this.bar = JMenuBars.builder()
                            .add(JMenus.builder()
                                       .setText("Columns")
                                       .add(JMenuItems.builder().setText("Item1")
                                                      .build())
                                       .add(JMenuItems.builder().setText("Item2")
                                                      .build())
                                       .add(JMenuItems.builder().setText("Item3")
                                                      .build())
                                       .build())
                            .add(JMenus.builder()
                                       .setText("Actions")
                                       .add(JMenuItems.builder().setText("Item1")
                                                      .build())
                                       .add(JMenuItems.builder().setText("Item2")
                                                      .build())
                                       .add(JMenuItems.builder().setText("Item3")
                                                      .build())
                                       .build())
                            .build();
    }

    public static FileTableMenu with(final Context context) {
        return new FileTableMenu(context);
    }

    public Component ui() {
        return bar;
    }

    public interface Context {
    }
}
