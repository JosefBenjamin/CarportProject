package app.exceptions;

public class DatabaseException extends Exception {


    /**
     * @param userMsg is a custom message meant to explain the error to the end-user
     *                userMsg is saved in the Exception superclass constructor,
     *                it can be accessed by using e.getMessage();
     */

    public DatabaseException(String userMsg) {
        super(userMsg);
    }

    /**
     * @param devMsg is a custom message meant to explain the error to a fellow developer
     *               devMsg is just printed out to the console.
     */

    public DatabaseException(String userMsg, String devMsg) {
        super(userMsg);
        System.out.println(devMsg);
    }

    /**
     * @param userMsg   is a custom message meant to explain the error to the end-user
     *                  userMsg is saved in the Exception superclass constructor,
     *                  it can be accessed by using e.getMessage();
     * @param rootCause finds the specific exception, from which all inherit or extend from Throwable
     */

    public DatabaseException(String userMsg, Throwable rootCause) {
        super(userMsg, rootCause);
    }

    public DatabaseException(Throwable rootCause) {
        super(rootCause);
    }
}
