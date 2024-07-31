import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class Answer extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    public Answer() {
        this.setContentPane(this.contentPane);
        this.setModal(true);
        this.buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Answer.this.onCancel();
            }
        });
        this.setDefaultCloseOperation(0);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Answer.this.onCancel();
            }
        });
    }

    public void setButtonOKListener(ActionListener listener) {
        this.buttonOK.addActionListener(listener);
        System.out.println("Answer");
    }

    private void onCancel() {
        this.dispose();
    }
}
