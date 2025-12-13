package functions;
import java.io.*;
//import java.io.Serializable;



//public class ArrayTabulatedFunction implements TabulatedFunction, Serializable {
public class ArrayTabulatedFunction implements TabulatedFunction, Externalizable,Cloneable   {
    //private static final long serialVersionUID = 1L;

    private FunctionPoint[] points; //массив типа FunctionPoint
    private int pointsCount;

    // Конструктор без параметров для Externalizable
    public ArrayTabulatedFunction() {}

    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("количество точек менее 2");
        }

        //проверка упорядоченности точек по X
        for (int i = 1; i < points.length; i++) {
            if (points[i].get_x() <= points[i - 1].get_x() + 1e-10) {
                throw new IllegalArgumentException("точки не упорядочены по х");
            }
        }

        this.pointsCount = points.length;
        this.points = new FunctionPoint[points.length];

        //делаем копии точек для инкапсуляции
        for (int i = 0; i < points.length; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }


    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("левая граница больше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("количество точек менее 2");
        }

        this.pointsCount = pointsCount; //сохраняем колво точек
        this.points = new FunctionPoint[pointsCount]; //и создаём массив нужного размера

        double step = (rightX - leftX) / (pointsCount - 1); //расстояние между точками
        for (int i = 0; i < pointsCount; i++) { //проходимс по всем точкам
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0); //новая точка в масив
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("левая граница больше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("количество точек менее 2");
        }

        this.pointsCount = values.length;
        this.points = new FunctionPoint[values.length];

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }

    //методы Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(points[i].get_x());
            out.writeDouble(points[i].get_y());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        pointsCount = in.readInt();
        points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }

    public double getLeftDomainBorder() {
        return points[0].get_x();  //х первой точки
    }

    public double getRightDomainBorder() {
        return points[pointsCount - 1].get_x();  //х последней точки
    }

    public double getFunctionValue(double x) {
        //если x норм - ищем интервал и интерполирум
        if (x >= getLeftDomainBorder() && x <= getRightDomainBorder()) {
            for (int i = 0; i < pointsCount - 1; i++) {
                double x1 = points[i].get_x();
                double x2 = points[i + 1].get_x();

                if (x >= x1 && x <= x2) {
                    double y1 = points[i].get_y();
                    double y2 = points[i + 1].get_y();
                    return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
                }
            }
        }
        return Double.NaN;
    }

    //возвращает количество точек
    public int getPointsCount() {
        return pointsCount;
    }

    // возвращает копию точки по индексу
    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
        }

        return new FunctionPoint(points[index]);
    }

    //замена точку по индексу
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
        }

        // проверка что x между соседними точками
        if (index > 0 && point.get_x() <= points[index - 1].get_x() + 1e-10) {
            throw new InappropriateFunctionPointException("х точки меньше предыдущей точки");
            //return;  //x меньше предыдущей точки
        }
        if (index < pointsCount - 1 && point.get_x() >= points[index + 1].get_x() - 1e-10) {
            //return;  //x больше следующей точки
            throw new InappropriateFunctionPointException("х точки больше следующей точки");
        }

        points[index] = new FunctionPoint(point);
    }

    //возвращаем x точки по индексу
    public double getPointX(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
        }
        return points[index].get_x();
    }

    //изменяет x точки по индексу
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
            //return;  // некорректный индекс
        }

        // левая и правая границы текущей функции
        double leftBorder = getLeftDomainBorder();
        double rightBorder = getRightDomainBorder();

        // Проверка для первой точки
        if (index == 0  && (x < leftBorder || (pointsCount > 1 && x >= points[1].get_x() - 1e-10))) {
            throw new InappropriateFunctionPointException("х первой точки должен быть в пределах левой границы");
            //return; //х выходит за лев границу и больше след точки
        }
        // Проверка для последней точки
        else if (index == pointsCount - 1 && (x > rightBorder || x <= points[pointsCount - 2].get_x() + 1e-10)) {
            throw new InappropriateFunctionPointException("х последней точки должен быть в пределах правой границы");
            //return;
        }
        // Проверка для внутренних точек
        else {
            if (x <= points[index - 1].get_x() + 1e-10 || x >= points[index + 1].get_x() - 1e-10) {
                throw new InappropriateFunctionPointException("х точки должен быть между соседними точками");
                //return;
            }
        }

        points[index].set_x(x);
    }


    public double getPointY(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
        }
        return points[index].get_y(); //получает у точки по индексу
    }

    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
        }
        points[index].set_y(y);  //изменяем у точки по индексу
    }

    //удаление точки по индексу
    public void deletePoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
        }

        if (pointsCount < 3) {
            throw new IllegalStateException("количество точек должно быть минумум 3");
        }

        for (int i = index; i < pointsCount - 1; i++) {
            points[i] = points[i + 1]; //сдвиг точек влево
        }
        pointsCount--;  //уменьшаем количество точек
        points[pointsCount] = null;  //очищаем последнюю ссылку
    }

    //добавление точки
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        int insertIndex = 0;
        while (insertIndex < pointsCount && points[insertIndex].get_x() < point.get_x() - 1e-10) {
            insertIndex++; //позиция для вставки
        }

        //проверка что точка с таким X еще не существует
        if (insertIndex < pointsCount && Math.abs(points[insertIndex].get_x() - point.get_x()) < 1e-10) {
            throw new InappropriateFunctionPointException("точка с таким х уже существует");
            //return;  // точка с таким X уже есть
        }

        //увеличиваем массив в двое
        if (pointsCount == points.length) {
            FunctionPoint[] newPoints = new FunctionPoint[points.length * 2];
            System.arraycopy(points, 0, newPoints, 0, pointsCount);
            points = newPoints;
        }

        //сдвг точки
        for (int i = pointsCount; i > insertIndex; i--) {
            points[i] = points[i - 1];
        }

        //вставка новой точки
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < pointsCount; i++) {
            //добавляем строковое представление точки
            sb.append(points[i].toString());
            if (i < pointsCount - 1) {
                sb.append(", "); //добавляем разделитель
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        //проверяем ссылочное равенство
        if (this == o) return true;
        //проверяемчто объект реализует TabulatedFunction
        if (!(o instanceof TabulatedFunction)) return false;

        TabulatedFunction that = (TabulatedFunction) o;

        //проверка количества точек
        if (this.getPointsCount() != that.getPointsCount()) {
            return false;
        }

        //если передана ArrayTabulatedFunction то используем прямой доступ к массиву
        if (o instanceof ArrayTabulatedFunction) {
            ArrayTabulatedFunction other = (ArrayTabulatedFunction) o;
            for (int i = 0; i < pointsCount; i++) {
                if (!this.points[i].equals(other.points[i])) {
                    return false;
                }
            }
        } else {
            //для любой другой TabulatedFunction используем интерфейсные методы
            for (int i = 0; i < pointsCount; i++) {
                FunctionPoint thisPoint = this.getPoint(i);
                FunctionPoint thatPoint = that.getPoint(i);
                if (!thisPoint.equals(thatPoint)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        //включаем количество точек в хэш
        int result = pointsCount;
        for (int i = 0; i < pointsCount; i++) {
            result ^= points[i].hashCode();
        }
        return result;
    }

    @Override
    public TabulatedFunction clone() {
        try {
            ArrayTabulatedFunction cloned = (ArrayTabulatedFunction) super.clone();
            //глубокое клонирование массива точек
            cloned.points = new FunctionPoint[points.length];
            for (int i = 0; i < pointsCount; i++) {
                cloned.points[i] = (FunctionPoint) points[i].clone();
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("клонирование не поддерживается", e);
        }
    }
}