package functions.meta;

import functions.Function;

//возводим функцию в степень
public class Power implements Function {
    private Function f; //исходная функция которую возводим в степень
    private double power; //в какую степень возводим

    public Power(Function f, double power) {
        this.f = f;
        this.power = power;
    }

    //левая граница такая же как у исходной функции
    public double getLeftDomainBorder() {
        return f.getLeftDomainBorder();
    }

    //правая граница такая же как у исходной функции
    public double getRightDomainBorder() {
        return f.getRightDomainBorder();
    }

    //берем значение исходной функции и возводим в степень
    public double getFunctionValue(double x) {
        double value = f.getFunctionValue(x);
        if (Double.isNaN(value)) return Double.NaN;
        return Math.pow(value, power);
    }
}