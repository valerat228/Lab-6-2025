package functions.meta;

import functions.Function;

//сдвигаем функцию влево-вправо и вверх-вниз
public class Shift implements Function {
    private Function f; //исходная функция
    private double shiftX; //на сколько сдвинуть по х
    private double shiftY; //на сколько сдвинуть по у

    public Shift(Function f, double shiftX, double shiftY) {
        this.f = f;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    //левая граница сдвигается по х
    public double getLeftDomainBorder() {
        return f.getLeftDomainBorder() + shiftX;
    }

    //правая граница сдвигается по х
    public double getRightDomainBorder() {
        return f.getRightDomainBorder() + shiftX;
    }

    //сдвигаем координаты и значение функции
    public double getFunctionValue(double x) {
        double shiftedX = x - shiftX; //обратно сдвигаем х
        if (shiftedX < f.getLeftDomainBorder() || shiftedX > f.getRightDomainBorder()) {
            return Double.NaN;
        }
        return f.getFunctionValue(shiftedX) + shiftY; //сдвигаем у
    }
}