package de.team33.files.ui;

import net.team33.fscalc.ui.rsrc.Ico;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

final class Basics {

    private Basics() {
    }

    static TreeCellRenderer treeCellRenderer() {
        return new DefaultTreeCellRenderer() {{
            this.closedIcon = Ico.CLSDIR;
            this.openIcon = Ico.OPNDIR;
            this.leafIcon = Ico.FILE;
        }};
    }
}
