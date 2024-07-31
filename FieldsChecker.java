public class FieldsChecker {
    public FieldsChecker() {
    }

    public static String checkPowerModeFields(String period, String workTime) {
        String error = "";
        if (!period.isEmpty() && !workTime.isEmpty()) {
            if (!Helpers.isInt(period)) {
                error = error + "Период должен быть целым числом!\n";
            } else if (!Helpers.checkRange(Integer.parseInt(period), 0, 1000)) {
                error = error + "Период должен входить в интервал от 0 до 1000!\n";
            }

            error = error + checkWorkTime(workTime);
        } else {
            error = error + "Все поля должны быть заполнены!\n";
        }

        return error;
    }

    public static String checkTempModeFields(String temp, String workTime) {
        String error = "";
        if (!temp.isEmpty() && !workTime.isEmpty()) {
            if (!Helpers.isFloat(temp)) {
                error = error + "Температура должна быть числом!\n";
            } else if (!Helpers.checkRange(Float.parseFloat(temp), 0, 1000)) {
                error = error + "Температура должна входить в интервал от 0 до 1000!\n";
            }

            error = error + checkWorkTime(workTime);
        } else {
            error = error + "Все поля должны быть заполнены!\n";
        }

        return error;
    }

    private static String checkWorkTime(String workTime) {
        String error = "";
        if (!Helpers.isInt(workTime)) {
            error = error + "Время работы должно быть целым числом!\n";
        } else if (!Helpers.checkRange(Integer.parseInt(workTime), 0, 1000)) {
            error = error + "Время работы должно входить в интервал от 0 до 1000!\n";
        }

        return error;
    }

    public static String compareWorkTimeWithPeriod(int workTime, int period) {
        return workTime < period ? "Время работы не может быть меньше чем период!" : "";
    }
}
