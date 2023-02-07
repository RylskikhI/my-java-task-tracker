package Tasks;

public class TaskValidationException extends RuntimeException {

    public TaskValidationException() {
    }

    public TaskValidationException(final String message) {
        super(message);
    }
}
