package threads;
import functions.Functions;

public class Integrator extends Thread {
    private final Task task;
    private final ReadWriteSemaphore semaphore;

    public Integrator(Task task, ReadWriteSemaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        int tasksCount = task.getTasksCount();
        for (int i = 0; i < tasksCount; i++) {
            try {
                //захватываем семафор для чтения
                semaphore.startRead();

                //проверяем прерывание
                if (Thread.interrupted()) {
                    System.out.println("Integrator: получен сигнал прерывания");
                    return;
                }
                //читаем и обрабатываем данные
                double left = task.getLeftBorder();
                double right = task.getRightBorder();
                double step = task.getDiscretizationStep();

                double result = Functions.integrate(task.getFunction(), left, right, step);
                System.out.printf("Integrator[%d]: Result %.3f %.3f %.3f %.6f%n", i, left, right, step, result);

                semaphore.endRead(); //освобождаем семафор

            } catch (InterruptedException e) {
                System.out.println("Integrator: прерван во время ожидания");
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("Integrator: ошибка вычисления - " + e.getMessage());
                semaphore.endRead();
            }
        }
    }
}