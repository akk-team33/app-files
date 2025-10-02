package de.team33.files.gamma;

import de.team33.files.gamma.ui.FileTree;
import net.team33.fscalc.ui.rsrc.Ico;

import javax.swing.*;

public class Icons implements FileTree.Icons {

    @Override
    public final Icon stdFolder() {
        return Ico.CLSDIR;
    }

    @Override
    public final Icon opnFolder() {
        return Ico.OPNDIR;
    }

    @Override
    public final Icon stdFile() {
        return Ico.FILE;
    }
}
