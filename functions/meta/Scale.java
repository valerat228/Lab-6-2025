package functions.meta;

import functions.Function;

//растягиваем или сжимаем функцию по осям х и у
public class Scale implements Function {
    private Function f; //исходная функция
    private double scaleX; //во сколько раз растянуть по х
    private double scaleY; //во сколько раз растянуть по у

    public Scale(Function f, double scaleX, double scaleY) {
        this.f = f;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    //левая граница растягивается по х
    public double getLeftDomainBorder() {
        return f.getLeftDomainBorder() * scaleX;
    }

    //правая граница растягивается по х
    public double getRightDomainBorder() {
        return f.getRightDomainBorder() * scaleX;
    }

    //масштабируем координаты и значение функции
    public double getFunctionValue(double x) {
        double scaledX = x / scaleX; //обратно масштабируем х
        if (scaledX < f.getLeftDomainBorder() || scaledX > f.getRightDomainBorder()) {
            return Double.NaN;
        }
        return f.getFunctionValue(scaledX) * scaleY; //масштабируем у
    }
}