package threads;

import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private final Task task;

    public SimpleIntegrator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        int tasksCount = task.getTasksCount();
        int processed = 0;
        while ( processed < tasksCount) {
            double left, right, step;

            synchronized (task) {
                if (task.getFunction() == null) {
                    continue;
                }
                //читаем параметры
                left = task.getLeftBorder();
                right = task.getRightBorder();
                step = task.getDiscretizationStep();

                try {
                    //вычисляем интеграл
                    double result = Functions.integrate(task.getFunction(), left, right, step);

                    System.out.printf("Integrator: Result %.3f %.3f %.3f %.6f%n", left, right, step, result);
                    processed++;
                } catch (IllegalArgumentException e) {
                    System.out.println("Integrator: ошибка вычисления - " + e.getMessage());
                } catch (NullPointerException e) {
                    System.out.println("Integrator: NullPointerException - нет данных для обработки");
                    processed = tasksCount;
                }
            }

        }
    }
}