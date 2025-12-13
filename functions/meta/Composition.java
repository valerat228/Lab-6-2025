package functions.meta;

import functions.Function;

//вкладываем одну функцию в другую f2(f1(x))
public class Composition implements Function {
    private Function f1, f2; //f1 - внутренняя, f2 - внешняя

    public Composition(Function f1, Function f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    //левая граница от внутренней функции f1
    public double getLeftDomainBorder() {
        return f1.getLeftDomainBorder();
    }

    //правая граница от внутренней функции f1
    public double getRightDomainBorder() {
        return f1.getRightDomainBorder();
    }

    //сначала вычисляем внутреннюю функцию, потом внешнюю
    public double getFunctionValue(double x) {
        double innerValue = f1.getFunctionValue(x); //считаем f1(x)
        if (Double.isNaN(innerValue)) return Double.NaN;
        return f2.getFunctionValue(innerValue); //считаем f2(результат)
    }
}