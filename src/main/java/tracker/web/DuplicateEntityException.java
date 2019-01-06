package tracker.web;

public class DuplicateEntityException extends Exception {
    public DuplicateEntityException() {
        super();
    }

    public DuplicateEntityException(String message) {
        super(message);
    }
}
