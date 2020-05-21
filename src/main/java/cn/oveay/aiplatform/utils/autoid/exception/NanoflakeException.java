package cn.oveay.aiplatform.utils.autoid.exception;

/**
 * @author QiangweiLuo
 */
public class NanoflakeException extends Exception {

    public NanoflakeException() {
        super();
    }

    /**
     *
     * @param message
     */
    public NanoflakeException(String message) {
        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public NanoflakeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public NanoflakeException(Throwable cause) {
        super(cause);
    }
}
