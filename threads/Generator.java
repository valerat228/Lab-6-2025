package threads;

import functions.basic.Log;

public class Generator extends Thread {
    private final Task task;
    private final ReadWriteSemaphore semaphore;

    public Generator(Task task, ReadWriteSemaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        int tasksCount = task.getTasksCount();
        for (int i = 0; i < tasksCount; i++) {
            try {
                //захватываем семафор для записи
                semaphore.startWrite();

                //проверяем прерывание
                if (Thread.interrupted()) {
                    System.out.println("Generator: получен сигнал прерывания");
                    return;
                }

                //генерируем данные
                double base = 1 + Math.random() * 9;
                double left = Math.random() * 100;
                double right = 100 + Math.random() * 100;
                double step = Math.random();

                task.setFunction(new Log(base));
                task.setLeftBorder(left);
                task.setRightBorder(right);
                task.setDiscretizationStep(step);

                System.out.printf("Generator[%d]: Source %.3f %.3f %.3f%n", i, left, right, step);

                //освобождаем семафор
                semaphore.endWrite();

            } catch (InterruptedException e) {
                System.out.println("Generator: прерван во время ожидания");
                return;
            }
        }
    }
}