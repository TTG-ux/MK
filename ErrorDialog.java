import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class ErrorDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel errText;
    private JButton buttonCancel;

    public ErrorDialog() {
        this.setContentPane(this.contentPane);
        this.setModal(true);
        this.getRootPane().setDefaultButton(this.buttonOK);
        this.buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ErrorDialog.this.onCancel();
            }
        });
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ErrorDialog.this.onCancel();
            }
        });
        this.contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ErrorDialog.this.onCancel();
            }
        }, KeyStroke.getKeyStroke(27, 0), 1);
    }

    public void setErrorText(String errText) {
        this.errText.setText(errText);
    }

    private void onCancel() {
        this.dispose();
    }
}
