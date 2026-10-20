import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import javax.swing.*;

public class PetClinicTracker {
    public static void main(String[] args) {
        Style.setup();

        SwingUtilities.invokeLater(() -> {
            try {
                Path file = Path.of("clinic.properties");

                ClinicPanel panel = new ClinicPanel(
                        Clinic.load(file), file
                );

                JFrame frame = new JFrame(
                        "Pet Clinic Tracker"
                );

                frame.setContentPane(panel);
                frame.setSize(1320, 900);
                frame.setMinimumSize(
                        new Dimension(1140, 760)
                );
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(
                        JFrame.DO_NOTHING_ON_CLOSE
                );

                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent event) {
                        if (panel.save()
                                || ClinicDialog.confirm(
                                frame,
                                "Unsaved changes",
                                "Saving failed. Close without saving?",
                                "Close anyway"
                        )) {
                            frame.dispose();
                        }
                    }
                });

                frame.setVisible(true);

            } catch (Exception ex) {
                ClinicDialog.message(
                        null,
                        "Could not open clinic",
                        "Could not open clinic: "
                                + ex.getMessage()
                                + "\nExisting data was not overwritten."
                );
            }
        });
    }
}