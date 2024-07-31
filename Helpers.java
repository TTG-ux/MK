public class Helpers {
    public Helpers() {
    }

    public static boolean checkRange(int check, int from, int to) {
        return from < check && check < to;
    }

    public static boolean checkRange(float check, int from, int to) {
        return (float)from < check && check < (float)to;
    }

    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public static int convertToInt(String str) {
        return Integer.parseInt(str);
    }
}
