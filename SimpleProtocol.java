public class SimpleProtocol {
    private static final char DATA_SEPARATOR = ';';
    public static final String CMD_START = "S1";
    public static final String CMD_STOP = "O1";
    public static final String CMD_PAUSE = "U1";
    public static final char KEY_TIME_HIGH = 'H';
    public static final char KEY_TIME_LOW = 'L';
    public static final char KEY_TEMP = 'T';
    public static final char KEY_MODE = 'M';
    public static final short MODE_POWER = 0;
    public static final short MODE_TEMP = 1;
    private String dataBuffer = "";

    public SimpleProtocol() {
    }

    public void setTimeSignalHigh(int timeHigh) {
        this.buildParam('H', timeHigh);
    }

    public void setTimeSignalLow(int timeLow) {
        this.buildParam('L', timeLow);
    }

    public void setTemperature(float temp) {
        this.buildParam('T', temp);
    }

    private void setMode(short mode) {
        this.buildParam('M', mode);
    }

    public void start() {
        this.dataBuffer = this.dataBuffer + "S1;";
    }

    public void stop() {
        this.dataBuffer = this.dataBuffer + "O1;";
    }

    public void pause() {
        this.dataBuffer = this.dataBuffer + "U1;";
    }

    public void buildParam(char key, int value) {
        String temp = Integer.toString(value);
        this.dataBuffer = this.dataBuffer + key + temp + ';';
    }

    public void buildParam(char key, float value) {
        String temp = Float.toString(value);
        this.dataBuffer = this.dataBuffer + key + temp + ';';
    }

    public String build() {
        this.dataBuffer = this.dataBuffer + '\n';
        return this.dataBuffer;
    }

    public void clearBuffer() {
        this.dataBuffer = "";
    }
}
