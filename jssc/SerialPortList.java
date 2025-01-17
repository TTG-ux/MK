package jssc;

import java.io.File;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class SerialPortList {
    private static SerialNativeInterface serialInterface = new SerialNativeInterface();

    private static final Pattern PORTNAMES_REGEXP;

    private static final String PORTNAMES_PATH;

    static {
        switch (SerialNativeInterface.getOsType()) {
            case 0:
                PORTNAMES_REGEXP = Pattern.compile("(ttyS|ttyUSB|ttyACM|ttyAMA|rfcomm|ttyO)[0-9]{1,3}");
                PORTNAMES_PATH = "/dev/";
                break;
            case 2:
                PORTNAMES_REGEXP = Pattern.compile("[0-9]*|[a-z]*");
                PORTNAMES_PATH = "/dev/term/";
                break;
            case 3:
                PORTNAMES_REGEXP = Pattern.compile("tty.(serial|usbserial|usbmodem).*");
                PORTNAMES_PATH = "/dev/";
                break;
            case 1:
                PORTNAMES_REGEXP = Pattern.compile("");
                PORTNAMES_PATH = "";
                break;
            default:
                PORTNAMES_REGEXP = null;
                PORTNAMES_PATH = null;
                break;
        }
    }

    private static final Comparator<String> PORTNAMES_COMPARATOR = new Comparator<String>() {
        public int compare(String valueA, String valueB) {
            if (valueA.equalsIgnoreCase(valueB))
                return valueA.compareTo(valueB);
            int minLength = Math.min(valueA.length(), valueB.length());
            int shiftA = 0;
            int shiftB = 0;
            for (int i = 0; i < minLength; i++) {
                char charA = valueA.charAt(i - shiftA);
                char charB = valueB.charAt(i - shiftB);
                if (charA != charB)
                    if (Character.isDigit(charA) && Character.isDigit(charB)) {
                        int[] resultsA = getNumberAndLastIndex(valueA, i - shiftA);
                        int[] resultsB = getNumberAndLastIndex(valueB, i - shiftB);
                        if (resultsA[0] != resultsB[0])
                            return resultsA[0] - resultsB[0];
                        if (valueA.length() < valueB.length()) {
                            i = resultsA[1];
                            shiftB = resultsA[1] - resultsB[1];
                        } else {
                            i = resultsB[1];
                            shiftA = resultsB[1] - resultsA[1];
                        }
                    } else if (Character.toLowerCase(charA) - Character.toLowerCase(charB) != 0) {
                        return Character.toLowerCase(charA) - Character.toLowerCase(charB);
                    }
            }
            return valueA.compareToIgnoreCase(valueB);
        }

        private int[] getNumberAndLastIndex(String str, int startIndex) {
            String numberValue = "";
            int[] returnValues = { -1, startIndex };
            for (int i = startIndex; i < str.length(); ) {
                returnValues[1] = i;
                char c = str.charAt(i);
                if (Character.isDigit(c)) {
                    numberValue = numberValue + c;
                    i++;
                }
            }
            try {
                returnValues[0] = Integer.valueOf(numberValue).intValue();
            } catch (Exception ex) {}
            return returnValues;
        }
    };

    public static String[] getPortNames() {
        return getPortNames(PORTNAMES_PATH, PORTNAMES_REGEXP, PORTNAMES_COMPARATOR);
    }

    public static String[] getPortNames(String searchPath) {
        return getPortNames(searchPath, PORTNAMES_REGEXP, PORTNAMES_COMPARATOR);
    }

    public static String[] getPortNames(Pattern pattern) {
        return getPortNames(PORTNAMES_PATH, pattern, PORTNAMES_COMPARATOR);
    }

    public static String[] getPortNames(Comparator<String> comparator) {
        return getPortNames(PORTNAMES_PATH, PORTNAMES_REGEXP, comparator);
    }

    public static String[] getPortNames(String searchPath, Pattern pattern) {
        return getPortNames(searchPath, pattern, PORTNAMES_COMPARATOR);
    }

    public static String[] getPortNames(String searchPath, Comparator<String> comparator) {
        return getPortNames(searchPath, PORTNAMES_REGEXP, comparator);
    }

    public static String[] getPortNames(Pattern pattern, Comparator<String> comparator) {
        return getPortNames(PORTNAMES_PATH, pattern, comparator);
    }

    public static String[] getPortNames(String searchPath, Pattern pattern, Comparator<String> comparator) {
        if (searchPath == null || pattern == null || comparator == null)
            return new String[0];
        if (SerialNativeInterface.getOsType() == 1)
            return getWindowsPortNames(pattern, comparator);
        return getUnixBasedPortNames(searchPath, pattern, comparator);
    }

    private static String[] getWindowsPortNames(Pattern pattern, Comparator<String> comparator) {
        String[] portNames = serialInterface.getSerialPortNames();
        if (portNames == null)
            return new String[0];
        TreeSet<String> ports = new TreeSet<String>(comparator);
        for (String portName : portNames) {
            if (pattern.matcher(portName).find())
                ports.add(portName);
        }
        return (String[])ports.toArray((Object[])new String[ports.size()]);
    }

    private static String[] getUnixBasedPortNames(String searchPath, Pattern pattern, Comparator<String> comparator) {
        searchPath = searchPath.equals("") ? searchPath : (searchPath.endsWith("/") ? searchPath : (searchPath + "/"));
        String[] returnArray = new String[0];
        File dir = new File(searchPath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files.length > 0) {
                TreeSet<String> portsTree = new TreeSet<String>(comparator);
                for (File file : files) {
                    String fileName = file.getName();
                    if (!file.isDirectory() && !file.isFile() && pattern.matcher(fileName).find()) {
                        String portName = searchPath + fileName;
                        long portHandle = serialInterface.openPort(portName, false);
                        if (portHandle >= 0L || portHandle == -1L) {
                            if (portHandle != -1L)
                                serialInterface.closePort(portHandle);
                            portsTree.add(portName);
                        }
                    }
                }
                returnArray = (String[])portsTree.toArray((Object[])returnArray);
            }
        }
        return returnArray;
    }
}
