package threads;

import functions.Function;

//класс для хранения задания на интегрирование
public class Task {
    private Function function;
    private double leftBorder;
    private double rightBorder;
    private double discretizationStep;
    private int tasksCount;

    //геттеры и сеттеры
    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public double getLeftBorder() {
        return leftBorder;
    }

    public void setLeftBorder(double leftBorder) {
        this.leftBorder = leftBorder;
    }

    public double getRightBorder() {
        return rightBorder;
    }

    public void setRightBorder(double rightBorder) {
        this.rightBorder = rightBorder;
    }

    public double getDiscretizationStep() {
        return discretizationStep;
    }

    public void setDiscretizationStep(double discretizationStep) {
        this.discretizationStep = discretizationStep;
    }

    public int getTasksCount() {
        return tasksCount;
    }

    public void setTasksCount(int tasksCount) {
        this.tasksCount = tasksCount;
    }



}