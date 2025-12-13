import functions.FunctionPoint;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import functions.InappropriateFunctionPointException;
import functions.Function;
import functions.Functions;
import functions.TabulatedFunctions;
import functions.basic.*;
import functions.meta.*;
import java.io.*;


public class Main {
    private static void nonThread() {
        //создаем объект задания
        threads.Task task = new threads.Task();
        task.setTasksCount(100);

        //цикл по всем заданиям
        for (int i = 0; i < task.getTasksCount(); i++) {
            try {
                //создаем логарифмическую функцию со случайным основанием от 1 до 10
                double base = 1 + Math.random() * 9; //случайное число от 1 до 10
                functions.basic.Log logFunc = new functions.basic.Log(base);
                task.setFunction(logFunc);

                //левая граница от 0 до 100
                double left = Math.random() * 100;
                task.setLeftBorder(left);

                //правая граница от 100 до 200
                double right = 100 + Math.random() * 100;
                task.setRightBorder(right);

                //шаг дискретизации от 0 до 1
                double step = Math.random();
                task.setDiscretizationStep(step);

                System.out.printf("Source %.3f %.3f %.3f%n", left, right, step);

                double result = Functions.integrate(logFunc, left, right, step);

                System.out.printf("Result %.3f %.3f %.3f %.6f%n", left, right, step, result);

            } catch (IllegalArgumentException e) {
                System.out.println("ошибка: " + e.getMessage());
            }
        }
    }

    private static void simpleThreads() {
        //создаем объект задания
        threads.Task task = new threads.Task();
        task.setTasksCount(100);

        //создаем потоки
        Thread generatorThread = new Thread(new threads.SimpleGenerator(task));
        Thread integratorThread = new Thread(new threads.SimpleIntegrator(task));

        //запускаем потоки
        integratorThread.start();
        generatorThread.start();



        //ждем завершения потоков
        try {
            generatorThread.join();
            integratorThread.join();
        } catch (InterruptedException e) {
            System.out.println("основной поток прерван");
        }
    }

    private static void complicatedThreads() {
        //создаем объекты
        threads.Task task = new threads.Task();
        task.setTasksCount(100);

        threads.ReadWriteSemaphore semaphore = new threads.ReadWriteSemaphore();

        //создаем потоки
        threads.Generator generator = new threads.Generator(task, semaphore);
        threads.Integrator integrator = new threads.Integrator(task, semaphore);

        //устанавливаем приоритеты
        generator.setPriority(Thread.MIN_PRIORITY);
        integrator.setPriority(Thread.MAX_PRIORITY);

        System.out.println("Запускаем потоки");
        generator.start();
        integrator.start();

        //ждём 50 мс
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            System.out.println("Основной поток прерван");
        }

        //прерываем потоки
        System.out.println("\nПрерываю потоки");
        generator.interrupt();
        integrator.interrupt();

        try {
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            System.out.println("Основной поток прерван");
        }
    }

    public static void main(String[] args) throws InappropriateFunctionPointException {
        /*
        // Твои оригинальные тесты 1-9
        System.out.println("=== ТЕСТ 1: СОЗДАНИЕ ФУНКЦИИ ===");
        double[] values = {0, 1, 4, 9, 16};
        TabulatedFunction func = new ArrayTabulatedFunction(0, 4, values);

        // Выводим все точки после создания
        System.out.println("Точки после создания функции:");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.println("  Точка " + i + ": (" + func.getPointX(i) + ", " + func.getPointY(i) + ")");
        }

        System.out.println("\n=== ТЕСТ 2: ВЫЧИСЛЕНИЕ ЗНАЧЕНИЙ ===");
        double[] testX = {-1, 0, 1.5, 4, 5};
        for (double x : testX) {
            double y = func.getFunctionValue(x);
            if (Double.isNaN(y)) {
                System.out.println("f(" + x + ") = не определено");
            } else {
                System.out.println("f(" + x + ") = " + y);
            }
        }

        System.out.println("\n=== ТЕСТ 3: ДОБАВЛЕНИЕ И УДАЛЕНИЕ ===");

        // Добавление точки
        System.out.println("Добавляем точку (2.5, 6.25)");
        func.addPoint(new FunctionPoint(2.5, 6.25));
        System.out.println("Точки после добавления:");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.println("  Точка " + i + ": (" + func.getPointX(i) + ", " + func.getPointY(i) + ")");
        }
        System.out.println("Количество точек: " + func.getPointsCount());

        // Удаление точки
        System.out.println("\nУдаляем точку с индексом 1");
        func.deletePoint(1);
        System.out.println("Точки после удаления:");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.println("  Точка " + i + ": (" + func.getPointX(i) + ", " + func.getPointY(i) + ")");
        }
        System.out.println("Количество точек: " + func.getPointsCount());

        // Изменение Y точки
        System.out.println("\nИзменяем Y точки с индексом 2 на 10.0");
        func.setPointY(2, 10.0);
        System.out.println("Точки после изменения Y:");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.println("  Точка " + i + ": (" + func.getPointX(i) + ", " + func.getPointY(i) + ")");
        }

        // Попытка добавить точку с существующим X
        System.out.println("\nПытаемся добавить точку (2.0, 100.0) - должна быть отклонена");
        try{
            func.addPoint(new FunctionPoint(2.0, 100.0));
            System.out.println("исключение не выброшено");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("ошибка: " + e.getMessage());
        }
        System.out.println("Точки после попытки добавления дубликата:");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.println("  Точка " + i + ": (" + func.getPointX(i) + ", " + func.getPointY(i) + ")");
        }

        // Изменение X точки
        System.out.println("\n=== ТЕСТ 4: ИЗМЕНЕНИЕ X ТОЧЕК ===");

        System.out.println("Изменяем X точки с индексом 1 с " + func.getPointX(1) + " на 1.8");
        func.setPointX(1, 1.8);
        System.out.println("Точки после изменения X точки 1:");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.println("  Точка " + i + ": (" + func.getPointX(i) + ", " + func.getPointY(i) + ")");
        }

        // Попытка изменить X на некорректное значение (должно быть отклонено)
        System.out.println("\nПытаемся изменить X точки 0 на -1.0 (должно быть отклонено)");
        try{
            func.setPointX(0, -1.0);
            System.out.println("исключение не выброшено");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("ошибка: " + e.getMessage());
        }
        System.out.println("Точки после НЕУДАЧНОЙ попытки изменения X:");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.println("  Точка " + i + ": (" + func.getPointX(i) + ", " + func.getPointY(i) + ")");
        }

        // Замена точки целиком
        System.out.println("\n=== ТЕСТ 5: ЗАМЕНА ТОЧЕК ===");

        System.out.println("Заменяем точку с индексом 2 на (2.2, 15.0)");
        func.setPoint(2, new FunctionPoint(2.2, 15.0));
        System.out.println("Точки после замены точки 2:");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.println("  Точка " + i + ": (" + func.getPointX(i) + ", " + func.getPointY(i) + ")");
        }

        // Попытка заменить точку на некорректную (должно быть отклонено)
        System.out.println("\nПытаемся заменить точку 1 на (3.5, 5.0) - X выходит за границы (должно быть отклонено)");
        try{
            func.setPoint(1, new FunctionPoint(3.5, 5.0));
            System.out.println("исключение не выброшено");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("ошибка: " + e.getMessage());
        }
        System.out.println("Точки после НЕУДАЧНОЙ попытки замены:");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.println("  Точка " + i + ": (" + func.getPointX(i) + ", " + func.getPointY(i) + ")");
        }

        System.out.println("\n=== ТЕСТ 6: ГРАНИЦЫ И ФИНАЛЬНОЕ СОСТОЯНИЕ ===");
        System.out.println("Левая граница: " + func.getLeftDomainBorder());
        System.out.println("Правая граница: " + func.getRightDomainBorder());
        System.out.println("Финальное количество точек: " + func.getPointsCount());
        System.out.println("Финальные точки функции:");
        for (int i = 0; i < func.getPointsCount(); i++) {
            System.out.println("  Точка " + i + ": (" + func.getPointX(i) + ", " + func.getPointY(i) + ")");
        }

        System.out.println("\n=== ТЕСТ 7: LinkedListTabulatedFunction ===");

        // Тестируем связный список
        TabulatedFunction linkedFunc = new LinkedListTabulatedFunction(0, 4, values);
        System.out.println("LinkedListTabulatedFunction создана успешно");
        System.out.println("Левая граница: " + linkedFunc.getLeftDomainBorder());
        System.out.println("Правая граница: " + linkedFunc.getRightDomainBorder());
        System.out.println("Количество точек: " + linkedFunc.getPointsCount());

        // Тестируем добавление точки
        try {
            linkedFunc.addPoint(new FunctionPoint(2.5, 6.25));
            System.out.println("Точка (2.5, 6.25) добавлена в LinkedList");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("Ошибка при добавлении: " + e.getMessage());
        }

        // Тестируем удаление точки
        try {
            linkedFunc.deletePoint(1);
            System.out.println("Точка с индексом 1 удалена из LinkedList");
        } catch (Exception e) {
            System.out.println("Ошибка при удалении: " + e.getMessage());
        }

        // Выводим финальное состояние LinkedList
        System.out.println("Финальные точки LinkedList функции:");
        for (int i = 0; i < linkedFunc.getPointsCount(); i++) {
            System.out.println("  Точка " + i + ": (" + linkedFunc.getPointX(i) + ", " + linkedFunc.getPointY(i) + ")");
        }

        System.out.println("\n=== ТЕСТ 8: ПОЛИМОРФИЗМ ===");

        // Демонстрация полиморфизма - работа с разными реализациями через один интерфейс
        TabulatedFunction[] functions = {
                new ArrayTabulatedFunction(0, 2, new double[]{1, 2, 3}),
                new LinkedListTabulatedFunction(0, 2, new double[]{1, 2, 3})
        };

        for (int i = 0; i < functions.length; i++) {
            System.out.println("Функция " + (i + 1) + " (" + functions[i].getClass().getSimpleName() + "):");
            System.out.println("  f(1.0) = " + functions[i].getFunctionValue(1.0));
            System.out.println("  Количество точек: " + functions[i].getPointsCount());
        }

        System.out.println("\n=== ТЕСТ 9: ИСКЛЮЧЕНИЯ ===");

        // Тестируем исключения для ArrayTabulatedFunction
        try {
            TabulatedFunction badFunc = new ArrayTabulatedFunction(5, 0, 3); // неправильные границы
        } catch (IllegalArgumentException e) {
            System.out.println("Поймано ожидаемое исключение: " + e.getMessage());
        }

        // Тестируем исключения для LinkedListTabulatedFunction
        try {
            TabulatedFunction badFunc = new LinkedListTabulatedFunction(0, 1, 1); // мало точек
        } catch (IllegalArgumentException e) {
            System.out.println("Поймано ожидаемое исключение: " + e.getMessage());
        }

        // НОВЫЕ ТЕСТЫ ДЛЯ ЗАДАНИЙ 3-8
        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== ТЕСТЫ ДЛЯ ЛАБОРАТОРНОЙ №4 ===");
        System.out.println("=".repeat(50));

        try {
            // Тест аналитических функций (задание 3)
            System.out.println("\n=== ТЕСТ 10: АНАЛИТИЧЕСКИЕ ФУНКЦИИ ===");

            Sin sin = new Sin();
            Cos cos = new Cos();
            Exp exp = new Exp();
            Log ln = new Log(Math.E);

            System.out.println("sin(π/2) = " + sin.getFunctionValue(Math.PI/2));
            System.out.println("cos(0) = " + cos.getFunctionValue(0));
            System.out.println("exp(1) = " + exp.getFunctionValue(1));
            System.out.println("ln(e) = " + ln.getFunctionValue(Math.E));

            System.out.println("область определения sin: [" + sin.getLeftDomainBorder() + ", " + sin.getRightDomainBorder() + "]");
            System.out.println("область определения ln: [" + ln.getLeftDomainBorder() + ", " + ln.getRightDomainBorder() + "]");

            // Тест мета-функций (задание 4)
            System.out.println("\n=== ТЕСТ 11: МЕТА-ФУНКЦИИ ===");

            Function sumFunc = new Sum(sin, cos);
            Function multFunc = new Mult(sin, cos);
            Function powerFunc = new Power(sin, 2);
            Function shiftFunc = new Shift(sin, 1, 0.5);
            Function scaleFunc = new Scale(sin, 2, 3);
            Function compFunc = new Composition(sin, cos);

            System.out.println("sin(1) + cos(1) = " + sumFunc.getFunctionValue(1));
            System.out.println("sin(1) * cos(1) = " + multFunc.getFunctionValue(1));
            System.out.println("sin²(1) = " + powerFunc.getFunctionValue(1));
            System.out.println("sin(1-1) + 0.5 = " + shiftFunc.getFunctionValue(1));
            System.out.println("3 * sin(1/2) = " + scaleFunc.getFunctionValue(1));
            System.out.println("sin(cos(1)) = " + compFunc.getFunctionValue(1));

            // Тест класса Functions (задание 5)
            System.out.println("\n=== ТЕСТ 12: КЛАСС Functions ===");

            Function f1 = Functions.sum(sin, cos);
            Function f2 = Functions.power(sin, 2);
            Function f3 = Functions.shift(sin, 0.5, 1);
            Function f4 = Functions.composition(sin, cos);

            System.out.println("Functions.sum(sin, cos)(1) = " + f1.getFunctionValue(1));
            System.out.println("Functions.power(sin, 2)(1) = " + f2.getFunctionValue(1));
            System.out.println("Functions.shift(sin, 0.5, 1)(1) = " + f3.getFunctionValue(1));
            System.out.println("Functions.composition(sin, cos)(1) = " + f4.getFunctionValue(1));

            // Тест табулирования (задание 6)
            System.out.println("\n=== ТЕСТ 13: ТАБУЛИРОВАНИЕ ===");

            TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 5);
            System.out.println("табулированный sin на [0, π] с 5 точками:");
            for (int i = 0; i < tabulatedSin.getPointsCount(); i++) {
                System.out.printf("  точка %d: (%.2f, %.4f)%n", i, tabulatedSin.getPointX(i), tabulatedSin.getPointY(i));
            }

            // Тест ввода/вывода (задание 7)
            System.out.println("\n=== ТЕСТ 14: ВВОД/ВЫВОД ===");

            // Бинарный формат
            try (FileOutputStream fos = new FileOutputStream("test_binary.dat")) {
                TabulatedFunctions.outputTabulatedFunction(tabulatedSin, fos);
                System.out.println("функция записана в бинарный файл");
            }

            TabulatedFunction readFromBinary;
            try (FileInputStream fis = new FileInputStream("test_binary.dat")) {
                readFromBinary = TabulatedFunctions.inputTabulatedFunction(fis);
                System.out.println("функция прочитана из бинарного файла");
            }

            // Текстовый формат
            try (FileWriter fw = new FileWriter("test_text.txt")) {
                TabulatedFunctions.writeTabulatedFunction(tabulatedSin, fw);
                System.out.println("функция записана в текстовый файл");
            }

            TabulatedFunction readFromText;
            try (FileReader fr = new FileReader("test_text.txt")) {
                readFromText = TabulatedFunctions.readTabulatedFunction(fr);
                System.out.println("функция прочитана из текстового файла");
            }

            // Сравнение
            System.out.println("сравнение исходной и считанных функций:");
            for (int i = 0; i < tabulatedSin.getPointsCount(); i++) {
                double original = tabulatedSin.getPointY(i);
                double fromBinary = readFromBinary.getPointY(i);
                double fromText = readFromText.getPointY(i);
                System.out.printf("  точка %d: исходная=%.4f, бинарная=%.4f, текстовая=%.4f%n",
                        i, original, fromBinary, fromText);
            }

            // Тест сложной композиции (задание 8)
            System.out.println("\n=== ТЕСТ 15: СЛОЖНАЯ КОМПОЗИЦИЯ ===");

            // ln(exp(x)) должна быть близка к x
            Function lnOfExp = Functions.composition(exp, ln);
            TabulatedFunction tabulatedLnExp = TabulatedFunctions.tabulate(lnOfExp, 0.1, 5, 10);

            System.out.println("ln(exp(x)) на отрезке [0.1, 5]:");
            for (int i = 0; i < tabulatedLnExp.getPointsCount(); i++) {
                double x = tabulatedLnExp.getPointX(i);
                double y = tabulatedLnExp.getPointY(i);
                double error = Math.abs(x - y);
                System.out.printf("  x=%.1f: ln(exp(x))=%.4f, ошибка=%.6f%n", x, y, error);
            }


            System.out.println("\n=== ТЕСТ 16: СЕРИАЛИЗАЦИЯ ===");

            //создаем композицию ln(exp(x)) - должна быть близка к x
            Exp expFunc = new Exp();
            Log lnFunc = new Log(Math.E);
            Function composition = Functions.composition(expFunc, lnFunc);

            //табулируем композицию от 0 до 10 с шагом 1
            TabulatedFunction tabulatedComposition = TabulatedFunctions.tabulate(composition, 0, 10, 11);

            System.out.println("Исходная функция ln(exp(x)) на [0, 10]:");
            for (int i = 0; i < tabulatedComposition.getPointsCount(); i++) {
                double x = tabulatedComposition.getPointX(i);
                double y = tabulatedComposition.getPointY(i);
                System.out.printf("  x=%.1f: ln(exp(x))=%.4f (должно быть %.1f)%n", x, y, x);
            }


            System.out.println("\n=== Serializable ===");
            //сериализуем с использованием Serializable (LinkedListTabulatedFunction)
            TabulatedFunction serializableFunc = new LinkedListTabulatedFunction(0, 10, 11);
            //заполняем значениями
            for (int i = 0; i < serializableFunc.getPointsCount(); i++) {
                double x = serializableFunc.getPointX(i);
                double y = composition.getFunctionValue(x);
                serializableFunc.setPointY(i, y);
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("serializable.dat"))) {
                oos.writeObject(serializableFunc);
                System.out.println("функция сериализована в serializable.dat (Serializable)");
            }

            //десериализуем
            TabulatedFunction deserializedSerializable;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("serializable.dat"))) {
                deserializedSerializable = (TabulatedFunction) ois.readObject();
                System.out.println("функция десериализована из serializable.dat");
            }


            System.out.println("\n=== Externalizable ===");
            //сериализуем с использованием Externalizable (ArrayTabulatedFunction)
            TabulatedFunction externalizableFunc = new ArrayTabulatedFunction(0, 10, 11);
            //заполняем значениями
            for (int i = 0; i < externalizableFunc.getPointsCount(); i++) {
                double x = externalizableFunc.getPointX(i);
                double y = composition.getFunctionValue(x);
                externalizableFunc.setPointY(i, y);
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("externalizable.dat"))) {
                oos.writeObject(externalizableFunc);
                System.out.println("функция сериализована в externalizable.dat (Externalizable)");
            }

            //десериализуем
            TabulatedFunction deserializedExternalizable;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("externalizable.dat"))) {
                deserializedExternalizable = (TabulatedFunction) ois.readObject();
                System.out.println("функция десериализована из externalizable.dat");
            }

            //сравниваем все функции
            System.out.println("\nСравнение всех функций:");
            boolean allMatch = true;
            for (int i = 0; i < tabulatedComposition.getPointsCount(); i++) {
                double original = tabulatedComposition.getPointY(i);
                double ser = deserializedSerializable.getPointY(i);
                double ext = deserializedExternalizable.getPointY(i);
                double errorSer = Math.abs(original - ser);
                double errorExt = Math.abs(original - ext);

                System.out.printf("  точка %d: исходная=%.4f, ser=%.4f(err=%.6f), ext=%.4f(err=%.6f)%n",
                        i, original, ser, errorSer, ext, errorExt);

                if (errorSer > 1e-10 || errorExt > 1e-10) {
                    allMatch = false;
                }
            }

            if (allMatch) {
                System.out.println("Обе сериализации работают правильно - все значения совпадают!");
            } else {
                System.out.println("Ошибка сериализации - значения не совпадают!");
            }

            //анализ файлов
            File serializableFile = new File("serializable.dat");
            File externalizableFile = new File("externalizable.dat");
            File binaryFile = new File("test_binary.dat");
            File textFile = new File("test_text.txt");

            System.out.println("\nСравнение размеров файлов:");
            System.out.printf("  Serializable (serializable.dat): %d байт%n", serializableFile.length());
            System.out.printf("  Externalizable (externalizable.dat): %d байт%n", externalizableFile.length());
            if (binaryFile.exists()) {
                System.out.printf("  Бинарный формат (test_binary.dat): %d байт%n", binaryFile.length());
            }
            if (textFile.exists()) {
                System.out.printf("  Текстовый формат (test_text.txt): %d байт%n", textFile.length());
            }

            } catch (Exception e) {
            System.out.println("ошибка в новых тестах: " + e.getMessage());
            e.printStackTrace();
        }
    */
            System.out.println("\n" + "=".repeat(50));
            System.out.println("=== ТЕСТЫ ДЛЯ ЛАБОРАТОРНОЙ №5 ===");
            System.out.println("=".repeat(50));

            //(Задание 1)
            System.out.println("\n=== ТЕСТ 17: FunctionPoint ===");
            {
                System.out.println("тестирование FunctionPoint:");

                FunctionPoint p1 = new FunctionPoint(1.5, 2.5);
                FunctionPoint p2 = new FunctionPoint(1.5, 2.5);
                FunctionPoint p3 = new FunctionPoint(1.5000000001, 2.5000000001);
                FunctionPoint p4 = new FunctionPoint(1.5, 3.0);

                System.out.println("   toString():");
                System.out.println("     p1: " + p1);
                System.out.println("     p2: " + p2.toString());

                System.out.println("   equals():");
                System.out.println("     p1.equals(p2): " + p1.equals(p2) + " (ожидается true)");
                System.out.println("     p1.equals(p3): " + p1.equals(p3) + " (ожидается true)");
                System.out.println("     p1.equals(p4): " + p1.equals(p4) + " (ожидается false)");

                System.out.println("   hashCode():");
                System.out.println("     p1.hashCode(): " + p1.hashCode());
                System.out.println("     p2.hashCode(): " + p2.hashCode());
                System.out.println("     p1.hashCode() == p2.hashCode(): " + (p1.hashCode() == p2.hashCode()));

                System.out.println("   clone():");
                FunctionPoint p1Clone = (FunctionPoint) p1.clone();
                System.out.println("     p1.equals(p1Clone): " + p1.equals(p1Clone));
                System.out.println("     p1 == p1Clone: " + (p1 == p1Clone) + " (ожидается false)");
            }

            //(Задание 2)
            System.out.println("\n=== ТЕСТ 18: ArrayTabulatedFunction ===");
            {
                System.out.println("тестирование ArrayTabulatedFunction:");

                FunctionPoint[] arrayPoints1 = {
                        new FunctionPoint(0.0, 0.0),
                        new FunctionPoint(1.0, 1.0),
                        new FunctionPoint(2.0, 4.0)
                };

                FunctionPoint[] arrayPoints2 = {
                        new FunctionPoint(0.0, 0.0),
                        new FunctionPoint(1.0, 1.0),
                        new FunctionPoint(2.0, 4.0)
                };

                FunctionPoint[] arrayPoints3 = {
                        new FunctionPoint(0.0, 0.0),
                        new FunctionPoint(1.0, 2.0),
                        new FunctionPoint(2.0, 4.0)
                };

                ArrayTabulatedFunction arrayFunc1 = new ArrayTabulatedFunction(arrayPoints1);
                ArrayTabulatedFunction arrayFunc2 = new ArrayTabulatedFunction(arrayPoints2);
                ArrayTabulatedFunction arrayFunc3 = new ArrayTabulatedFunction(arrayPoints3);

                System.out.println("   toString():");
                System.out.println("     arrayFunc1: " + arrayFunc1.toString());
                System.out.println("     arrayFunc2: " + arrayFunc2.toString());

                System.out.println("   equals():");
                System.out.println("     arrayFunc1.equals(arrayFunc2): " + arrayFunc1.equals(arrayFunc2) + " (ожидается true)");
                System.out.println("     arrayFunc1.equals(arrayFunc3): " + arrayFunc1.equals(arrayFunc3) + " (ожидается false)");
                System.out.println("     arrayFunc1.equals(null): " + arrayFunc1.equals(null) + " (ожидается false)");

                System.out.println("   hashCode():");
                System.out.println("     arrayFunc1.hashCode(): " + arrayFunc1.hashCode());
                System.out.println("     arrayFunc2.hashCode(): " + arrayFunc2.hashCode());
                System.out.println("     arrayFunc3.hashCode(): " + arrayFunc3.hashCode());
                System.out.println("     arrayFunc1.hashCode() == arrayFunc2.hashCode(): " + (arrayFunc1.hashCode() == arrayFunc2.hashCode()));

                System.out.println("   тест изменения хэш-кода (незначительное изменение):");
                int originalHash = arrayFunc1.hashCode();
                System.out.println("     исходный хэш: " + originalHash);
                try {
                    arrayFunc1.setPointY(1, 1.001); //незначительное изменение
                    int newHash = arrayFunc1.hashCode();
                    System.out.println("     после изменения arrayFunc1.setPointY(1, 1.001):");
                    System.out.println("       новый хэш: " + newHash);
                    System.out.println("       хэш изменился: " + (originalHash != newHash) + " (ожидается true)");
                } catch (Exception e) {
                    System.out.println("     ошибка при изменении: " + e.getMessage());
                }

                System.out.println("   clone():");
                ArrayTabulatedFunction arrayFunc1Clone = (ArrayTabulatedFunction) arrayFunc1.clone();
                System.out.println("     arrayFunc1: " + arrayFunc1);
                System.out.println("     arrayFunc1Clone: " + arrayFunc1Clone);
                System.out.println("     arrayFunc1.equals(arrayFunc1Clone): " + arrayFunc1.equals(arrayFunc1Clone) + " (ожидается true)");
                System.out.println("     arrayFunc1 == arrayFunc1Clone: " + (arrayFunc1 == arrayFunc1Clone) + " (ожидается false)");

                System.out.println("   проверка глубокого клонирования:");
                try {
                    arrayFunc1Clone.setPointY(1, 999.0); //изменяем клон
                    System.out.println("     после изменения arrayFunc1Clone.setPointY(1, 999.0):");
                    System.out.println("       arrayFunc1.getPointY(1): " + arrayFunc1.getPointY(1));
                    System.out.println("       arrayFunc1Clone.getPointY(1): " + arrayFunc1Clone.getPointY(1));
                    System.out.println("       разные значения: " + (arrayFunc1.getPointY(1) != arrayFunc1Clone.getPointY(1)) + " (ожидается true)");
                    System.out.println("       объекты-клоны не изменились: " + (!arrayFunc1.equals(arrayFunc1Clone)) + " (ожидается true)");
                } catch (Exception e) {
                    System.out.println("     ошибка: " + e.getMessage());
                }
            }

            //(Задание 3)
            System.out.println("\n=== ТЕСТ 19: LinkedListTabulatedFunction ===");
            {
                System.out.println("тестирование LinkedListTabulatedFunction:");

                FunctionPoint[] listPoints1 = {
                        new FunctionPoint(0.0, 0.0),
                        new FunctionPoint(1.0, 1.0),
                        new FunctionPoint(2.0, 4.0)
                };
                FunctionPoint[] listPoints2 = {
                        new FunctionPoint(0.0, 0.0),
                        new FunctionPoint(1.0, 1.0),
                        new FunctionPoint(2.0, 4.0)
                };
                LinkedListTabulatedFunction listFunc1 = new LinkedListTabulatedFunction(listPoints1);
                LinkedListTabulatedFunction listFunc2 = new LinkedListTabulatedFunction(listPoints2);
                System.out.println("   toString():");
                System.out.println("     listFunc1: " + listFunc1.toString());
                System.out.println("     listFunc2: " + listFunc2.toString());
                System.out.println("   equals():");
                System.out.println("     listFunc1.equals(listFunc2): " + listFunc1.equals(listFunc2) + " (ожидается true)");
                System.out.println("     listFunc1.equals(null): " + listFunc1.equals(null) + " (ожидается false)");

                System.out.println("   hashCode():");
                System.out.println("     listFunc1.hashCode(): " + listFunc1.hashCode());
                System.out.println("     listFunc2.hashCode(): " + listFunc2.hashCode());
                System.out.println("     listFunc1.hashCode() == listFunc2.hashCode(): " + (listFunc1.hashCode() == listFunc2.hashCode()));
                System.out.println("   clone():");
                LinkedListTabulatedFunction listFunc1Clone = (LinkedListTabulatedFunction) listFunc1.clone();
                System.out.println("     listFunc1: " + listFunc1);
                System.out.println("     listFunc1Clone: " + listFunc1Clone);
                System.out.println("     listFunc1.equals(listFunc1Clone): " + listFunc1.equals(listFunc1Clone) + " (ожидается true)");
                System.out.println("     listFunc1 == listFunc1Clone: " + (listFunc1 == listFunc1Clone) + " (ожидается false)");
                System.out.println("   проверка глубокого клонирования:");
                try {
                    listFunc1Clone.setPointY(1, 777.0); //изменяем клон
                    System.out.println("     после изменения listFunc1Clone.setPointY(1, 777.0):");
                    System.out.println("       listFunc1.getPointY(1): " + listFunc1.getPointY(1));
                    System.out.println("       listFunc1Clone.getPointY(1): " + listFunc1Clone.getPointY(1));
                    System.out.println("       разные значения: " + (listFunc1.getPointY(1) != listFunc1Clone.getPointY(1)) + " (ожидается true)");
                    System.out.println("       объекты-клоны не изменились: " + (!listFunc1.equals(listFunc1Clone)) + " (ожидается true)");
                } catch (Exception e) {
                    System.out.println("     ошибка: " + e.getMessage());
                }
            }

            //(Задание 4)
            System.out.println("\n=== ТЕСТ 20: Сравнение реализаций ===");
            {
                System.out.println("сравнение разных реализаций:");

                FunctionPoint[] compPoints = {
                        new FunctionPoint(0.0, 0.0),
                        new FunctionPoint(1.0, 1.0),
                        new FunctionPoint(2.0, 4.0)
                };

                ArrayTabulatedFunction compArrayFunc = new ArrayTabulatedFunction(compPoints);
                LinkedListTabulatedFunction compListFunc = new LinkedListTabulatedFunction(compPoints);

                System.out.println("   equals между реализациями:");
                System.out.println("     compArrayFunc: " + compArrayFunc.toString());
                System.out.println("     compListFunc: " + compListFunc.toString());
                System.out.println("     compArrayFunc.equals(compListFunc): " + compArrayFunc.equals(compListFunc) + " (ожидается true)");
                System.out.println("     compListFunc.equals(compArrayFunc): " + compListFunc.equals(compArrayFunc) + " (ожидается true)");

                System.out.println("   hashCode разных реализаций:");
                System.out.println("     compArrayFunc.hashCode(): " + compArrayFunc.hashCode());
                System.out.println("     compListFunc.hashCode(): " + compListFunc.hashCode());
                System.out.println("     одинаковые хэши: " + (compArrayFunc.hashCode() == compListFunc.hashCode()));

                System.out.println("   работа через интерфейс TabulatedFunction:");
                TabulatedFunction tab1 = compArrayFunc;
                TabulatedFunction tab2 = compListFunc;
                System.out.println("     tab1.equals(tab2): " + tab1.equals(tab2));
                System.out.println("     можно вызвать clone() через интерфейс: " + (tab1.clone() != null));

                TabulatedFunction tabClone = tab1.clone();
                System.out.println("     tab1.equals(tabClone): " + tab1.equals(tabClone));
                System.out.println("     tab1 == tabClone: " + (tab1 == tabClone) + " (ожидается false)");
            }

            //(Задание 5)
            System.out.println("\n=== ТЕСТ 21: Дополнительные проверки ===");
            {
                System.out.println("дополнительные проверки из задания:");

                System.out.println("1. проверка equals для одинаковых и различающихся объектов:");

                FunctionPoint[] testPoints1 = {
                        new FunctionPoint(0.0, 0.0),
                        new FunctionPoint(1.0, 1.0)
                };

                FunctionPoint[] testPoints2 = {
                        new FunctionPoint(0.0, 0.0),
                        new FunctionPoint(1.0, 1.0),
                        new FunctionPoint(2.0, 4.0)
                };

                ArrayTabulatedFunction testArray1 = new ArrayTabulatedFunction(testPoints1);
                ArrayTabulatedFunction testArray2 = new ArrayTabulatedFunction(testPoints1);
                ArrayTabulatedFunction testArray3 = new ArrayTabulatedFunction(testPoints2);

                LinkedListTabulatedFunction testList1 = new LinkedListTabulatedFunction(testPoints1);
                LinkedListTabulatedFunction testList2 = new LinkedListTabulatedFunction(testPoints1);

                System.out.println("   одинаковые ArrayTabulatedFunction: testArray1.equals(testArray2) = " + testArray1.equals(testArray2));
                System.out.println("   разные ArrayTabulatedFunction: testArray1.equals(testArray3) = " + testArray1.equals(testArray3));
                System.out.println("   одинаковые LinkedListTabulatedFunction: testList1.equals(testList2) = " + testList1.equals(testList2));
                System.out.println("   разные классы, одинаковые данные: testArray1.equals(testList1) = " + testArray1.equals(testList1));

                System.out.println("\n2. проверка hashCode() для всех объектов:");
                System.out.println("   testArray1.hashCode(): " + testArray1.hashCode());
                System.out.println("   testArray2.hashCode(): " + testArray2.hashCode());
                System.out.println("   testArray3.hashCode(): " + testArray3.hashCode());
                System.out.println("   testList1.hashCode(): " + testList1.hashCode());
                System.out.println("   testList2.hashCode(): " + testList2.hashCode());

                System.out.println("\n3. проверка согласованности equals и hashCode:");
                boolean consistent1 = (testArray1.equals(testArray2) == (testArray1.hashCode() == testArray2.hashCode()));
                boolean consistent2 = (testArray1.equals(testArray3) == (testArray1.hashCode() == testArray3.hashCode()));
                System.out.println("   testArray1 и testArray2: " + consistent1 + " (ожидается true)");
                System.out.println("   testArray1 и testArray3: " + consistent2 + " (ожидается true)");

                System.out.println("\n4. разные хэши для разного количества точек (из задания):");
                FunctionPoint[] zeroPoints = {
                        new FunctionPoint(-1.0, 1.0),
                        new FunctionPoint(0.0, 0.0),  //нулевая точка
                        new FunctionPoint(1.0, 1.0)
                };

                FunctionPoint[] noZeroPoints = {
                        new FunctionPoint(-1.0, 1.0),
                        new FunctionPoint(1.0, 1.0)   //нет точки x=0
                };

                ArrayTabulatedFunction zeroFunc = new ArrayTabulatedFunction(zeroPoints);
                ArrayTabulatedFunction noZeroFunc = new ArrayTabulatedFunction(noZeroPoints);

                System.out.println("   zeroFunc (3 точки): " + zeroFunc.toString() + ", hashCode: " + zeroFunc.hashCode());
                System.out.println("   noZeroFunc (2 точки): " + noZeroFunc.toString() + ", hashCode: " + noZeroFunc.hashCode());
                System.out.println("   хэши разные: " + (zeroFunc.hashCode() != noZeroFunc.hashCode()) + " (ожидается true)");

                System.out.println("\n5. незначительное изменение объекта (из задания):");
                FunctionPoint[] changePoints = {
                        new FunctionPoint(0.0, 0.0),
                        new FunctionPoint(1.0, 1.0),
                        new FunctionPoint(2.0, 4.0)
                };

                ArrayTabulatedFunction changeFunc = new ArrayTabulatedFunction(changePoints);
                int hashBeforeChange = changeFunc.hashCode();
                System.out.println("   исходная функция: " + changeFunc);
                System.out.println("   исходный hashCode: " + hashBeforeChange);

                try {
                    changeFunc.setPointY(1, 1.002); //на несколько тысячных
                    System.out.println("   изменена вторая точка: y = 1.002 (на несколько тысячных)");
                    System.out.println("   измененная функция: " + changeFunc);
                    int hashAfterChange = changeFunc.hashCode();
                    System.out.println("   новый hashCode: " + hashAfterChange);
                    System.out.println("   hashCode изменился: " + (hashBeforeChange != hashAfterChange) + " (ожидается true)");
                } catch (Exception e) {
                    System.out.println("   ошибка: " + e.getMessage());
                }

                System.out.println("\n6. проверка глубокого клонирования:");
                FunctionPoint[] clonePoints = {
                        new FunctionPoint(0.0, 0.0),
                        new FunctionPoint(1.0, 2.0),
                        new FunctionPoint(2.0, 8.0)
                };

                ArrayTabulatedFunction originalFunc = new ArrayTabulatedFunction(clonePoints);
                ArrayTabulatedFunction clonedFunc = (ArrayTabulatedFunction) originalFunc.clone();

                System.out.println("   исходный объект: " + originalFunc);
                System.out.println("   клон: " + clonedFunc);
                System.out.println("   equals перед изменением: " + originalFunc.equals(clonedFunc) + " (ожидается true)");

                try {
                    originalFunc.setPointY(1, 999.0); //меняем исходный объект
                    System.out.println("   изменили исходный объект: y[1] = 999.0");
                    System.out.println("   исходный после изменения: " + originalFunc);
                    System.out.println("   клон (не должен измениться): " + clonedFunc);
                    System.out.println("   equals после изменения: " + originalFunc.equals(clonedFunc) + " (ожидается false)");
                    System.out.println("   клон не изменился: " + (clonedFunc.getPointY(1) == 2.0) + " (ожидается true)");
                } catch (Exception e) {
                    System.out.println("   ошибка: " + e.getMessage());
                }
            }

            System.out.println("\n" + "=".repeat(50));
            System.out.println("=== ТЕСТЫ ДЛЯ ЛАБОРАТОРНОЙ №6 ===");
            System.out.println("=".repeat(50));

        //(Задание 1)
        {
            System.out.println("\n=== ЗАДАНИЕ 1 ===");
            functions.basic.Exp expFunc = new functions.basic.Exp();
            double theoretical = Math.E - 1;
            System.out.printf("теоретическое значение ∫e^x dx от 0 до 1: %.10f%n", theoretical);

            //проверка работы метода
            double result = Functions.integrate(expFunc, 0, 1, 0.1);
            System.out.printf("результат при шаге 0.1: %.10f%n", result);

            //поиск шага для 7 знака
            System.out.println("\nпоиск шага для точности до 7 знака:");
            double step = 0.1;
            boolean found = false;

            for (int i = 0; i < 10 && found == false; i++) {
                result = Functions.integrate(expFunc, 0, 1, step);
                double error = Math.abs(result - theoretical);

                if (error < 1e-7) {
                    System.out.printf("шаг %.6f: ошибка %.10f (достигнут 7 знак)%n", step, error);
                    found = true;
                } else {
                    System.out.printf("шаг %.6f: ошибка %.10f%n", step, error);
                }

                step /= 2.0; //уменьшаем шаг в 2 раза
            }

            if (!found) {
                System.out.println("7 знак не достигнут (шаг меньше 0.0001)");
            }
        }

        //Задание 2
        {
            System.out.println("\n=== ЗАДАНИЕ 2 ===");
            nonThread();
        }

        //Задание 3
        {
            System.out.println("\n=== ЗАДАНИЕ 3 ===");
            simpleThreads();
        }

        //Задание 4
        {
            System.out.println("\n=== ЗАДАНИЕ 4 ===");
            complicatedThreads();
        }


            System.out.println("\n=== ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ! ===");
    }


}