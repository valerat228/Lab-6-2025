package functions;

public class FunctionPointIndexOutOfBoundsException extends IndexOutOfBoundsException {
    //по умолчанию без сообщения
    public FunctionPointIndexOutOfBoundsException() {
        super();
    }

    //с сообщением
    public FunctionPointIndexOutOfBoundsException(String message) {
        super(message);
    }
}