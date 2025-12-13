package functions.meta;

import functions.Function;

//складываем две функции
public class Sum implements Function {
    private Function f1, f2; //две функции которые будем складывать

    public Sum(Function f1, Function f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    //берем самую правую левую границу из двух функций
    public double getLeftDomainBorder() {
        return Math.max(f1.getLeftDomainBorder(), f2.getLeftDomainBorder());
    }

    //берем самую левую правую границу из двух функций
    public double getRightDomainBorder() {
        return Math.min(f1.getRightDomainBorder(), f2.getRightDomainBorder());
    }

    //складываем значения двух функций в точке x
    public double getFunctionValue(double x) {
        //если x не входит в область определения - возвращаем NaN
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }
        return f1.getFunctionValue(x) + f2.getFunctionValue(x);
    }
}