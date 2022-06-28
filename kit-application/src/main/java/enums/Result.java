package enums;

public class Result {

    private static Result clazz;

    private boolean result;

    private String message;

    public static Result ok() {
        newWhenNull();
        clazz.result = true;
        return clazz;
    }

    public static Result fail() {
        newWhenNull();
        clazz.result = false;
        return clazz;
    }

    public Result message(String msg) {
        clazz.message = msg;
        return clazz;
    }

    private static void newWhenNull() {
        if (clazz == null) {
            clazz = new Result();
        }
    }

    public boolean getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
