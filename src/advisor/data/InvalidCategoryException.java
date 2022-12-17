package advisor.data;

public class InvalidCategoryException extends Exception{
    public InvalidCategoryException() {
        super("Unknown category name.");
    }

    public InvalidCategoryException(String message) {
        super(message);
    }
}
