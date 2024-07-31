// Decompiled with: CFR 0.152
// Class Version: 8
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class OvenManagerGUI
        extends JFrame {
    private JPanel panel;
    private JComboBox portSelect;
    private JLabel connectionState;
    private JComboBox modeSelect;
    private JButton stopButton;
    private JButton pauseButton;
    private JButton startButton;
    private JTextField periodTextField;
    private JSlider powerSlider;
    private JTextField workTimeTextField;
    private JProgressBar timerProgressBar;
    private JButton settingsButton;
    private JLabel powerLabel;
    private JPanel powerJPanel;
    private JLabel tempLabel;
    private JLabel periodLabel;
    private JLabel temperatureLabel;
    private JLabel settingsStateLabel;
    private JLabel timerLabel;
    private JPanel setFields;
    private JPanel setFieldsInner;
    private UInterface uInterface;
    private Serial serial;
    private Dialog dialog;
    private SimpleProtocol simpleProtocol;
    private static final String MODE_AM = "Амплитудная модуляция";
    private static final String MODE_CH = "Частотная модуляция";
    private static final String MODE_ZERO = "Без модуляции";
    private String serialBuffer = "";
    private SerialPort serialPort;
    private final int GET_TEMP_DURATION = 0;
    private int signalPeriod = 0;
    private int signalPower = 0;
    private float temp = 0.0f;
    private int workTime = 0;
    private CaretListener periodTextEditChangeListener;
    private ActionListener settingsButtonListener;
    private ActionListener pauseButtonListener;
    private ActionListener stopButtonListener;
    private ActionListener startButtonListener;
    private int timerCounter;
    private Timer timer;
    private ActionListener timerActionListener;
    private final WindowAdapter windowCloseListener;
    private ActionListener modeSelectListener;
    private ActionListener portSelectListener;
    private ChangeListener powerSliderListener;

    public static void main(String[] args) {
        OvenManagerGUI manager = new OvenManagerGUI();
        manager.addActionListeners();
    }

    public OvenManagerGUI() {
        this.$$$setupUI$$$();
        this.periodTextEditChangeListener = new CaretListener(){

            @Override
            public void caretUpdate(CaretEvent e) {
                int period;
                boolean errFlag = true;
                String periodStr = OvenManagerGUI.this.periodTextField.getText();
                if (!periodStr.isEmpty() && Helpers.isInt(periodStr) && (period = Integer.parseInt(periodStr)) > 0) {
                    OvenManagerGUI.this.uInterface.updatePowerSlider(period);
                    errFlag = false;
                }
                if (errFlag) {
                    OvenManagerGUI.this.uInterface.resetPowerSlider();
                }
            }
        };
        this.settingsButtonListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String mode = OvenManagerGUI.this.getSelectedMode();
                if (mode.equals(OvenManagerGUI.MODE_AM) && OvenManagerGUI.this.checkPowerModeFields()) {
                    OvenManagerGUI.this.setPowerModeParams();
                    OvenManagerGUI.this.writePowerModeParamsToDevice();
                    OvenManagerGUI.this.uInterface.setTimerProgressBar(OvenManagerGUI.this.workTime / 1000);
                    OvenManagerGUI.this.uInterface.updateTimer(OvenManagerGUI.this.workTime / 1000);
                }
            }
        };
        this.pauseButtonListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        this.stopButtonListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (OvenManagerGUI.this.serialPort != null) {
                    OvenManagerGUI.this.simpleProtocol.clearBuffer();
                    OvenManagerGUI.this.simpleProtocol.stop();
                    OvenManagerGUI.this.serial.write(OvenManagerGUI.this.simpleProtocol.build());
                    OvenManagerGUI.this.stopTimer();
                }
            }
        };
        this.startButtonListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (OvenManagerGUI.this.serialPort != null) {
                    OvenManagerGUI.this.simpleProtocol.clearBuffer();
                    OvenManagerGUI.this.simpleProtocol.start();
                    OvenManagerGUI.this.serial.write(OvenManagerGUI.this.simpleProtocol.build());
                    OvenManagerGUI.this.startTimer();
                }
            }
        };
        this.timerCounter = 0;
        this.timerActionListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                int workTimeSeconds = OvenManagerGUI.this.workTime / 1000;
                if (OvenManagerGUI.this.timerCounter >= workTimeSeconds) {
                    OvenManagerGUI.this.stopTimer();
                } else {
                    OvenManagerGUI.this.uInterface.updateTimer(workTimeSeconds - OvenManagerGUI.this.timerCounter);
                }
                OvenManagerGUI.this.timerCounter++;
            }
        };
        this.windowCloseListener = new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (OvenManagerGUI.this.serialPort != null) {
                    OvenManagerGUI.this.simpleProtocol.clearBuffer();
                    OvenManagerGUI.this.simpleProtocol.stop();
                    OvenManagerGUI.this.serial.write(OvenManagerGUI.this.simpleProtocol.build());
                }
                OvenManagerGUI.this.serial.close();
                System.exit(0);
            }
        };
        this.modeSelectListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = OvenManagerGUI.this.getSelectedMode();
                if (selected.equals(OvenManagerGUI.MODE_AM)) {
                    OvenManagerGUI.this.uInterface.showPowerModeFields();
                } else {
                    OvenManagerGUI.this.uInterface.showPowerModeFields();
                }
            }
        };
        this.portSelectListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String serialPortName = OvenManagerGUI.this.portSelect.getSelectedItem().toString();
                OvenManagerGUI.this.serial.close();
                OvenManagerGUI.this.serial.connect(serialPortName);
            }
        };
        this.powerSliderListener = new ChangeListener(){

            @Override
            public void stateChanged(ChangeEvent e) {
                int value = OvenManagerGUI.this.powerSlider.getValue();
                OvenManagerGUI.this.uInterface.setPowerLabelText(value);
            }
        };
        this.uInterface = new UInterface();
        this.serial = new Serial();
        this.dialog = new Dialog();
        this.simpleProtocol = new SimpleProtocol();
        this.getContentPane().add(this.panel);
        this.pack();
        this.setSize(new Dimension(600, 350));
        this.setResizable(false);
        this.setVisible(true);
        this.uInterface.setDefaultValues(this.serial.getPortsList());
        this.tryConnectToSerialPort();
    }

    private void addActionListeners() {
        this.addWindowListener(this.windowCloseListener);
        this.modeSelect.addActionListener(this.modeSelectListener);
        this.portSelect.addActionListener(this.portSelectListener);
        this.powerSlider.addChangeListener(this.powerSliderListener);
        this.settingsButton.addActionListener(this.settingsButtonListener);
        this.pauseButton.addActionListener(this.pauseButtonListener);
        this.stopButton.addActionListener(this.stopButtonListener);
        this.startButton.addActionListener(this.startButtonListener);
        this.periodTextField.addCaretListener(this.periodTextEditChangeListener);
    }

    private void tryConnectToSerialPort() {
        String[] serialPorts = this.serial.getPortsList();
        if (serialPorts.length == 1) {
            this.serial.connect(serialPorts[0]);
        }
    }

    private void writePowerModeParamsToDevice() {
        int timeHigh = this.calculateTimeSignalHigh(this.signalPower);
        int timeLow = this.signalPeriod - timeHigh;
        this.simpleProtocol.clearBuffer();
        this.simpleProtocol.setTimeSignalHigh(timeHigh);
        this.simpleProtocol.setTimeSignalLow(timeLow);
        this.serial.write(this.simpleProtocol.build());
    }

    private int calculateTimeSignalHigh(int power) {
        return Math.round(this.signalPeriod * power / 100);
    }

    private void writeTempModeParamsToDevice() {
        this.simpleProtocol.clearBuffer();
        this.simpleProtocol.setTemperature(this.signalPeriod);
        this.serial.write(this.simpleProtocol.build());
    }

    private boolean checkPowerModeFields() {
        String workTime;
        String period = this.periodTextField.getText();
        String error = FieldsChecker.checkPowerModeFields(period, workTime = this.workTimeTextField.getText());
        if (error.isEmpty()) {
            error = FieldsChecker.compareWorkTimeWithPeriod(Integer.parseInt(workTime), Integer.parseInt(period));
        }
        return this.showError(error);
    }

    private boolean showError(String error) {
        if (!error.isEmpty()) {
            this.dialog.showError(error);
            return false;
        }
        return true;
    }

    private void setPowerModeParams() {
        this.signalPeriod = Integer.parseInt(this.periodTextField.getText()) * 1000;
        this.signalPower = this.powerSlider.getValue();
        this.workTime = Integer.parseInt(this.workTimeTextField.getText()) * 1000;
    }

    private void setTempModeParams() {
        this.workTime = Integer.parseInt(this.workTimeTextField.getText()) * 1000;
    }

    private void startTimer() {
        this.uInterface.setTimerProgressBar(this.workTime / 1000);
        this.uInterface.updateTimer(this.workTime / 1000);
        this.timerCounter = 0;
        this.timer = new Timer(1000, this.timerActionListener);
        this.timer.start();
    }

    private void stopTimer() {
        this.timerCounter = 0;
        if (this.timer != null) {
            this.timer.stop();
        }
        this.sendStop();
        this.uInterface.resetTimerProgress();
        Toolkit.getDefaultToolkit().beep();
    }

    private void sendStop() {
        this.simpleProtocol.clearBuffer();
        this.simpleProtocol.stop();
        this.serial.write(this.simpleProtocol.build());
    }

    private String getSelectedMode() {
        return this.modeSelect.getSelectedItem().toString();
    }

    private String getValueFromData(String data) {
        return data.substring(1, data.length());
    }

    private void $$$setupUI$$$() {
        JLabel jLabel;
        JButton jButton;
        JButton jButton2;
        JButton jButton3;
        JButton jButton4;
        JLabel jLabel2;
        JProgressBar jProgressBar;
        JLabel jLabel3;
        JLabel jLabel4;
        JSlider jSlider;
        JTextField jTextField2;
        JLabel jLabel5;
        JTextField jTextField3;
        JLabel jLabel6;
        JPanel jPanel;
        JLabel jLabel7;
        JComboBox jComboBox;
        JComboBox jComboBox2;
        JPanel jPanel2;
        this.panel = jPanel2 = new JPanel();
        jPanel2.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 5, 0), -1, -1, false, false));
        JToolBar jToolBar = new JToolBar();
        jPanel2.add((Component)jToolBar, new GridConstraints(0, 0, 1, 1, 0, 1, 6, 0, null, new Dimension(-1, 20), null));
        JLabel jLabel8 = new JLabel();
        jLabel8.setPreferredSize(new Dimension(50, 16));
        jLabel8.setText("Режим:");
        Component component = jToolBar.add(jLabel8);
        this.modeSelect = jComboBox2 = new JComboBox();
        Component component2 = jToolBar.add(jComboBox2);
        JToolBar.Separator separator = new JToolBar.Separator();
        Component component3 = jToolBar.add(separator);
        JLabel jLabel9 = new JLabel();
        jLabel9.setMaximumSize(new Dimension(80, 20));
        jLabel9.setMinimumSize(new Dimension(80, 20));
        jLabel9.setPreferredSize(new Dimension(80, 20));
        jLabel9.setText("Порт:");
        Component component4 = jToolBar.add(jLabel9);
        this.portSelect = jComboBox = new JComboBox();
        DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel();
        jComboBox.setModel(defaultComboBoxModel);
        Component component5 = jToolBar.add(jComboBox);
        JToolBar.Separator separator2 = new JToolBar.Separator();
        Component component6 = jToolBar.add(separator2);
        this.connectionState = jLabel7 = new JLabel();
        ((Component)jLabel7).setForeground(new Color(-65536));
        jLabel7.setText("отключен");
        Component component7 = jToolBar.add(jLabel7);
        Spacer spacer = new Spacer();
        Component component8 = jToolBar.add(spacer);
        JPanel jPanel3 = new JPanel();
        jPanel3.setLayout(new GridLayoutManager(1, 2, new Insets(5, 10, 5, 10), -1, -1, false, false));
        jPanel2.add((Component)jPanel3, new GridConstraints(2, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        this.powerJPanel = jPanel = new JPanel();
        jPanel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1, false, false));
        ((Component)jPanel).setVisible(true);
        jPanel3.add((Component)jPanel, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        this.periodLabel = jLabel6 = new JLabel();
        jLabel6.setText("Период, с");
        jPanel.add((Component)jLabel6, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        this.periodTextField = jTextField3 = new JTextField();
        jPanel.add((Component)jTextField3, new GridConstraints(0, 1, 1, 1, 8, 1, 6, 0, null, new Dimension(150, -1), null));
        this.powerLabel = jLabel5 = new JLabel();
        jLabel5.setText("Коэффициент модуляции, 0");
        jPanel.add((Component)jLabel5, new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        JLabel jLabel10 = new JLabel();
        jLabel10.setRequestFocusEnabled(false);
        jLabel10.setText("Время работы, с");
        jPanel.add((Component)jLabel10, new GridConstraints(3, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        this.workTimeTextField = jTextField2 = new JTextField();
        jPanel.add((Component)jTextField2, new GridConstraints(3, 1, 1, 1, 8, 1, 6, 0, null, new Dimension(150, -1), null));
        this.powerSlider = jSlider = new JSlider();
        jSlider.setMaximum(0);
        jSlider.setValue(0);
        jSlider.setValueIsAdjusting(false);
        jPanel.add((Component)jSlider, new GridConstraints(2, 1, 1, 1, 8, 1, 6, 0, null, null, null));
        this.tempLabel = jLabel4 = new JLabel();
        jLabel4.setText("");
        ((Component)jLabel4).setVisible(false);
        jPanel.add((Component)jLabel4, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        JPanel jPanel4 = new JPanel();
        jPanel4.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel3.add((Component)jPanel4, new GridConstraints(0, 1, 1, 1, 0, 3, 3, 3, null, null, null));
        this.timerLabel = jLabel3 = new JLabel();
        jLabel3.setText("Оставшееся время, 0с");
        jPanel4.add((Component)jLabel3, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        this.timerProgressBar = jProgressBar = new JProgressBar();
        jProgressBar.setRequestFocusEnabled(false);
        jProgressBar.setString("0%");
        jProgressBar.setValue(0);
        jPanel4.add((Component)jProgressBar, new GridConstraints(1, 0, 1, 1, 0, 1, 6, 0, null, null, null));
        this.temperatureLabel = jLabel2 = new JLabel();
        jLabel2.setText("");
        jPanel4.add((Component)jLabel2, new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        JPanel jPanel5 = new JPanel();
        jPanel5.setLayout(new GridLayoutManager(1, 3, new Insets(5, 10, 5, 10), -1, -1, false, false));
        jPanel2.add((Component)jPanel5, new GridConstraints(4, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        this.stopButton = jButton4 = new JButton();
        jButton4.setText("Стоп");
        jPanel5.add((Component)jButton4, new GridConstraints(0, 1, 1, 1, 2, 1, 3, 0, null, null, null));
        this.pauseButton = jButton3 = new JButton();
        jButton3.setText("Пауза");
        jPanel5.add((Component)jButton3, new GridConstraints(0, 0, 1, 1, 2, 1, 3, 0, null, null, null));
        this.startButton = jButton2 = new JButton();
        jButton2.setText("Старт");
        jPanel5.add((Component)jButton2, new GridConstraints(0, 2, 1, 1, 2, 1, 3, 0, null, null, null));
        JPanel jPanel6 = new JPanel();
        jPanel6.setLayout(new GridLayoutManager(1, 3, new Insets(5, 10, 5, 10), -1, -1, false, false));
        jPanel2.add((Component)jPanel6, new GridConstraints(3, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        this.settingsButton = jButton = new JButton();
        jButton.setText("Установить");
        jPanel6.add((Component)jButton, new GridConstraints(0, 0, 1, 1, 0, 1, 3, 0, null, null, null));
        Spacer spacer2 = new Spacer();
        jPanel6.add((Component)spacer2, new GridConstraints(0, 2, 1, 1, 0, 1, 6, 1, null, null, null));
        this.settingsStateLabel = jLabel = new JLabel();
        jLabel.setText("");
        jPanel6.add((Component)jLabel, new GridConstraints(0, 1, 1, 1, 8, 0, 0, 0, null, null, null));
        JPanel jPanel7 = new JPanel();
        jPanel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 10, 0, 10), -1, -1, false, false));
        jPanel2.add((Component)jPanel7, new GridConstraints(1, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        JPanel jPanel8 = new JPanel();
        jPanel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel7.add((Component)jPanel8, new GridConstraints(0, 0, 1, 1, 0, 3, 3, 3, null, null, null));
        JLabel jLabel11 = new JLabel();
        jLabel11.setText("Установки");
        jPanel8.add((Component)jLabel11, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null, null, null));
        JPanel jPanel9 = new JPanel();
        jPanel9.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1, false, false));
        jPanel7.add((Component)jPanel9, new GridConstraints(0, 1, 1, 1, 0, 3, 3, 3, null, null, null));
        JLabel jLabel12 = new JLabel();
        jLabel12.setText("Показатели");
        jPanel9.add((Component)jLabel12, new GridConstraints(0, 1, 1, 1, 8, 0, 0, 0, null, null, null));
        Spacer spacer3 = new Spacer();
        jPanel9.add((Component)spacer3, new GridConstraints(0, 2, 1, 1, 0, 1, 6, 1, null, null, null));
        Spacer spacer4 = new Spacer();
        jPanel9.add((Component)spacer4, new GridConstraints(0, 0, 1, 1, 0, 1, 6, 1, null, null, null));
    }

    public JComponent $$$getRootComponent$$$() {
        return this.panel;
    }

    private class Serial {
        private SerialPortEventListener serialPortListener = new SerialPortEventListener(){

            @Override
            public void serialEvent(SerialPortEvent event) {
                int ev = event.getEventType();
                System.out.println("ev: " + ev);
                if (event.isRXCHAR() && event.getEventValue() > 0) {
                    try {
                        byte[] localBuffer;
                        for (byte b : localBuffer = OvenManagerGUI.this.serialPort.readBytes()) {
                            if (b == 10) continue;
                            OvenManagerGUI.this.serialBuffer = OvenManagerGUI.this.serialBuffer + (char)b;
                        }
                    }
                    catch (SerialPortException e) {
                        System.out.println(e);
                    }
                }
            }
        };

        private Serial() {
        }

        private void connect(String serialPortName) {
            OvenManagerGUI.this.serialPort = new SerialPort(serialPortName);
            try {
                OvenManagerGUI.this.serialPort.openPort();
                OvenManagerGUI.this.serialPort.setParams(9600, 8, 1, 0);
                OvenManagerGUI.this.serialPort.addEventListener(this.serialPortListener);
                OvenManagerGUI.this.uInterface.animationSerialIsEnabled();
            }
            catch (SerialPortException e) {
                System.out.println(e);
                OvenManagerGUI.this.uInterface.animationSerialIsDisabled();
            }
        }

        private void write(String data) {
            try {
                OvenManagerGUI.this.serialPort.writeString(data);
            }
            catch (SerialPortException e) {
                System.out.println(e);
            }
        }

        private String[] getPortsList() {
            return SerialPortList.getPortNames();
        }

        private void close() {
            if (OvenManagerGUI.this.serialPort != null) {
                try {
                    OvenManagerGUI.this.serialPort.closePort();
                }
                catch (SerialPortException e) {
                    System.out.println(e);
                }
            }
        }
    }

    private class UInterface {
        private UInterface() {
        }

        private void setModeSelect() {
            OvenManagerGUI.this.modeSelect.addItem(OvenManagerGUI.MODE_AM);
            OvenManagerGUI.this.modeSelect.addItem(OvenManagerGUI.MODE_CH);
            OvenManagerGUI.this.modeSelect.addItem(OvenManagerGUI.MODE_ZERO);
        }

        private void setDefaultValues(String[] serialPorts) {
            OvenManagerGUI.this.uInterface.setModeSelect();
            OvenManagerGUI.this.uInterface.setSerialPorts(serialPorts);
        }

        private void resetTimerProgress() {
            this.setTimerProgressBar(0);
            this.updateTimer(0);
        }

        private void setTimerProgressBar(int maxValue) {
            OvenManagerGUI.this.timerProgressBar.setMinimum(0);
            OvenManagerGUI.this.timerProgressBar.setMaximum(maxValue);
            OvenManagerGUI.this.timerProgressBar.setValue(maxValue);
        }

        private void updateTimer(int value) {
            OvenManagerGUI.this.timerLabel.setText("Оставшееся время, " + value + "с");
            OvenManagerGUI.this.timerProgressBar.setValue(value);
        }

        private void setSerialPorts(String[] serialPorts) {
            for (String port : serialPorts) {
                OvenManagerGUI.this.portSelect.addItem(port);
            }
        }

        public void setPowerLabelText(int value) {
            OvenManagerGUI.this.powerLabel.setText("Коэффициент модуляции, " + value + "");
        }

        public void animationSerialIsEnabled() {
            OvenManagerGUI.this.connectionState.setText("подключен");
            OvenManagerGUI.this.connectionState.setForeground(Color.BLUE);
        }

        public void animationSerialIsDisabled() {
            OvenManagerGUI.this.connectionState.setText("отключен");
            OvenManagerGUI.this.connectionState.setForeground(Color.RED);
        }

        private void showPowerModeFields() {
            OvenManagerGUI.this.periodLabel.setVisible(true);
            OvenManagerGUI.this.periodTextField.setVisible(true);
            OvenManagerGUI.this.powerLabel.setVisible(true);
            OvenManagerGUI.this.powerSlider.setVisible(true);
        }

        private void hidePowerModeFields() {
            OvenManagerGUI.this.periodLabel.setVisible(false);
            OvenManagerGUI.this.periodTextField.setVisible(false);
            OvenManagerGUI.this.powerLabel.setVisible(false);
            OvenManagerGUI.this.powerSlider.setVisible(false);
        }

        private void updatePowerSlider(int period) {
            OvenManagerGUI.this.powerSlider.setMaximum(this.calcSliderMaxValue(period));
            OvenManagerGUI.this.powerSlider.setValue(0);
            OvenManagerGUI.this.uInterface.setPowerLabelText(0);
        }

        private void resetPowerSlider() {
            OvenManagerGUI.this.powerSlider.setMaximum(0);
            OvenManagerGUI.this.powerSlider.setValue(0);
            OvenManagerGUI.this.uInterface.setPowerLabelText(0);
        }

        private int calcSliderMaxValue(int signalPeriod) {
            int resultT = signalPeriod - 0;
            return (int)Math.floor(resultT * 100 / signalPeriod);
        }
    }

    private class Dialog {
        private Dialog() {
        }

        private void showError(String errText) {
            ErrorDialog dialog = new ErrorDialog();
            dialog.pack();
            dialog.setErrorText(errText);
            dialog.setSize(new Dimension(550, 200));
            dialog.setResizable(false);
            dialog.setVisible(true);
        }
    }
}
