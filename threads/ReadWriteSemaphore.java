package threads;

public class ReadWriteSemaphore {
    private boolean canWrite = true;
    private boolean canRead = false;

    //начинаем операцию записи (блокируем чтение)
    public synchronized void startWrite() throws InterruptedException {
        //ждём пока можно будет писать
        while (!canWrite) {
            wait();
        }
        canRead = false; //запрещаем чтение пока идёт запись
    }

    //заканчиваем операцию записи (разрешаем чтение)
    public synchronized void endWrite() {
        canWrite = false;//запрещаем дальнейшую запись
        canRead = true; //разрешаем чтение
        notifyAll();//будим потоки ожидающие чтения
    }

    //начинаем операцию чтения (блокируем запись)
    public synchronized void startRead() throws InterruptedException {
        //ждём пока можно будет читать
        while (!canRead) {
            wait();
        }
        canWrite = false;
    }

    //заканчиваем операцию чтения (разрешаем запись)
    public synchronized void endRead() {
        canRead = false;
        canWrite = true;
        notifyAll();
    }
}