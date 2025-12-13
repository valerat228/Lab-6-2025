package functions;

import java.io.*;

//класс с методами для работы с табулированными функциями
public final class TabulatedFunctions {
    //запрещаем создавать объекты этого класса
    private TabulatedFunctions() {
        throw new AssertionError("нельзя создать экземпляр класса TabulatedFunctions");
    }

    //создает табулированную функцию из аналитической функции
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        //проверяем что отрезок табулирования внутри области определения функции
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("границы табулирования выходят за область определения функции");
        }

        //проверяем что точек достаточно для табулирования
        if (pointsCount < 2) {
            throw new IllegalArgumentException("количество точек должно быть не менее 2");
        }

        //вычисляем шаг между точками
        double step = (rightX - leftX) / (pointsCount - 1);

        //создаем массив для хранения точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];

        //заполняем массив точек
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step; //координата x
            double y = function.getFunctionValue(x); //значение функции в точке x
            points[i] = new FunctionPoint(x, y);//создаем точку
        }

        //возвращаем табулированную функцию на основе массива точек
        return new ArrayTabulatedFunction(points);
    }

    //записывает табулированную функцию в байтовый поток
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out); //обертка для удобной записи примитивов

        //записываем количество точек
        dos.writeInt(function.getPointsCount());

        //записываем все точки (x и y для каждой)
        for (int i = 0; i < function.getPointsCount(); i++) {
            dos.writeDouble(function.getPointX(i));
            dos.writeDouble(function.getPointY(i));
        }

        dos.flush(); //проталкиваем данные в поток
        //не закрываем поток - это ответственность вызывающего кода
    }

    //читает табулированную функцию из байтового потока
    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in); //обертка для удобного чтения примитивов

        //читаем количество точек
        int pointsCount = dis.readInt();
        FunctionPoint[] points = new FunctionPoint[pointsCount];

        //читаем все точки
        for (int i = 0; i < pointsCount; i++) {
            double x = dis.readDouble();
            double y = dis.readDouble();
            points[i] = new FunctionPoint(x, y);
        }

        //создаем и возвращаем табулированную функцию
        return new ArrayTabulatedFunction(points);
    }

    //записывает табулированную функцию в символьный поток
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        PrintWriter writer = new PrintWriter(out);

        writer.print(function.getPointsCount());

        for (int i = 0; i < function.getPointsCount(); i++) {
            writer.print(" " + function.getPointX(i));
            writer.print(" " + function.getPointY(i));
        }

        writer.flush();
    }

    //читает табулированную функцию из символьного потока
    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);

        // Настраиваем tokenizer для правильной работы с числами с запятыми
        tokenizer.resetSyntax();
        tokenizer.wordChars('0', '9');        // цифры
        tokenizer.wordChars('.', '.');        // точка
        tokenizer.wordChars(',', ',');        // ЗАПЯТАЯ - важно!
        tokenizer.wordChars('-', '-');        // минус
        tokenizer.wordChars('E', 'E');        // экспонента
        tokenizer.wordChars('e', 'e');        // экспонента
        tokenizer.whitespaceChars(' ', ' ');  // пробел
        tokenizer.whitespaceChars('\t', '\t'); // табуляция
        tokenizer.whitespaceChars('\n', '\n'); // новая строка
        tokenizer.whitespaceChars('\r', '\r'); // возврат каретки

        // читаем количество точек
        if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
            throw new IOException("ожидалось число (количество точек)");
        }
        int pointsCount = Integer.parseInt(tokenizer.sval);

        FunctionPoint[] points = new FunctionPoint[pointsCount];

        // читаем все точки (пары x y)
        for (int i = 0; i < pointsCount; i++) {
            // читаем x
            if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                throw new IOException("ожидалось число для x точки " + i);
            }
            // Заменяем запятую на точку для корректного парсинга
            String xStr = tokenizer.sval.replace(',', '.');
            double x = Double.parseDouble(xStr);

            // читаем y
            if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                throw new IOException("ожидалось число для y точки " + i);
            }
            // Заменяем запятую на точку для корректного парсинга
            String yStr = tokenizer.sval.replace(',', '.');
            double y = Double.parseDouble(yStr);

            points[i] = new FunctionPoint(x, y);
        }

        return new ArrayTabulatedFunction(points);
    }
}