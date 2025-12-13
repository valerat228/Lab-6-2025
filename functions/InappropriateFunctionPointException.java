package functions;

public class InappropriateFunctionPointException extends Exception {
    //по умолчанию без сообщения
    public InappropriateFunctionPointException() {
        super(); // вызов конструктора род класса
    }

    //с описанием ошибки
    public InappropriateFunctionPointException(String message) {
        super(message);
    }
}
