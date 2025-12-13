package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private double x;
    private double y;

    //создаёт объект точки с заданными координатами
    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //создаёт объект точки с теми же координатами, что у указанной точки
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }
    //создаёт точку с координатами (0; 0)
    public FunctionPoint() {
        this(0, 0);
    }

    public double get_x() {
        return x;
    }

    public double get_y() {
        return y;
    }

    public void set_x(double x) {
        this.x = x;
    }

    public void set_y(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        //форматируем координаты с двумя знаками после запятой
        return String.format("(%.2f; %.2f)", x, y);
    }

    @Override
    public boolean equals(Object o) {
        //проверяем ссылочное равенство
        if (this == o) return true;

        //объект не null и того же класса
        if (o == null || getClass() != o.getClass()) return false;

        FunctionPoint that = (FunctionPoint) o;

        return Math.abs(that.x - x) < 1e-9 && Math.abs(that.y - y) < 1e-9;
    }

    @Override
    public int hashCode() {
        //преобразуем double в long битовое представление
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);

        //разбиваем каждый long на два int и ксорим их
        int xHash = (int)(xBits ^ (xBits >>> 32));
        int yHash = (int)(yBits ^ (yBits >>> 32));

        //возвращаем xor от двух хэшей
        return xHash ^ yHash;
    }

    @Override
    public Object clone() {
        try {
            //простое клонирование
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("клонирование не поддерживается", e);
        }
    }
}
