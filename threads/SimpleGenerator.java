package threads;

import functions.basic.Log;

public class SimpleGenerator implements Runnable {
    private final Task task;

    public SimpleGenerator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        int tasksCount = task.getTasksCount();
        for (int i = 0; i < tasksCount; i++) {
            //создаем логарифмическую функцию
            double base = 1 + Math.random() * 9;

            //левая граница
            double left = Math.random() * 100;

            //правая граница
            double right = 100 + Math.random() * 100;

            //шаг дискретизации
            double step = Math.random();

            synchronized (task) {
                task.setFunction(new Log(base));
                task.setLeftBorder(left);
                task.setRightBorder(right);
                task.setDiscretizationStep(step);
                System.out.printf("Generator: Source %.3f %.3f %.3f%n", left, right, step);
            }
        }
    }
}