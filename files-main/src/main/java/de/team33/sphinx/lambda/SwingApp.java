package de.team33.sphinx.lambda;

import de.team33.sphinx.metis.JFrames;

import javax.swing.*;

public class SwingApp {

    /**
     * Starts a given <em>app</em>, typically within <em>main(args)</em>.
     */
    protected static void start(final SwingApp app) {
        SwingUtilities.invokeLater(app::run);
    }

    private void run() {
        JFrames.setup(newFrame())
               .setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
               .setLocationByPlatform(true)
               .pack()
               .setVisible(true);
    }

    /**
     * Retrieves a new {@link JFrame}.
     * <p>
     * The default implementation simply creates a new {@link JFrame} using {@link JFrame#JFrame()}
     * <p>
     * This method is designed for (optional) extension.
     */
    @SuppressWarnings("DesignForExtension")
    protected JFrame newFrame() {
        return new JFrame(getClass().getCanonicalName());
    }
}
