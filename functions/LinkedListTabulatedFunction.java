package functions;
import java.io.Serializable;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable, Cloneable  {
    private static final long serialVersionUID = 1L;

    private static class FunctionNode implements Serializable {
        private static final long serialVersionUID = 1L;
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;

        //конструктор узла
        FunctionNode(FunctionPoint point) {
            this.point = point;
        }
    }

    private FunctionNode head;
    private int pointsCount;
    private FunctionNode lastAccessedNode;
    private int lastAccessedIndex;

    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("количество точек менее 2");
        }

        //проверка упорядоченность точек по X
        for (int i = 1; i < points.length; i++) {
            if (points[i].get_x() <= points[i - 1].get_x() + 1e-10) {
                throw new IllegalArgumentException("точки не упорядочены по х");
            }
        }

        initializeList();

        for (FunctionPoint point : points) {
            addNodeToTail().point = new FunctionPoint(point);
        }
    }

    //конструктор с равномерным распределением точек
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        //проверка параметров конструктора
        if (leftX >= rightX) {
            throw new IllegalArgumentException("левая граница должна больше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("количество точек должно быть не менее 2");
        }

        initializeList();
        //this.pointsCount = pointsCount;

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            addNodeToTail().point = new FunctionPoint(x, 0);
        }
    }

    //конструктор с заданными значениями y
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        //проверка параметров конструктора
        if (leftX >= rightX) {
            throw new IllegalArgumentException("левая граница больше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("количество точек должно быть не менее 2");
        }

        initializeList();
        //this.pointsCount = values.length;

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            addNodeToTail().point = new FunctionPoint(x, values[i]);
        }
    }

    //инициализация пустого списка
    private void initializeList() {
        head = new FunctionNode(null);
        head.prev = head;
        head.next = head;
        pointsCount = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;
    }

    //возвращает ссылку на объект элемента списка по номеру
    private FunctionNode getNodeByIndex(int index) {
        //проверка корректности индекса
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
        }

        FunctionNode node;
        if (lastAccessedIndex != -1 && Math.abs(index - lastAccessedIndex) < Math.min(index, pointsCount - index)) {
            node = lastAccessedNode;
            if (index > lastAccessedIndex) {
                //двигаемся вперед от последнего доступного узла
                for (int i = lastAccessedIndex; i < index; i++) {
                    node = node.next;
                }
            } else {
                //двигаемся назад от последнего доступного узла
                for (int i = lastAccessedIndex; i > index; i--) {
                    node = node.prev;
                }
            }
        } else {
            //поиск с начала списка
            node = head.next;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
        }

        //сохраняем информацию о последнем доступе
        lastAccessedNode = node;
        lastAccessedIndex = index;
        return node;
    }

    //добавляет новый элемент в конец и возвращает ссылку на него
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null);
        FunctionNode tail = head.prev; //текущий хвост списка

        //вставка нового узла между tail и head
        tail.next = newNode;
        newNode.prev = tail;
        newNode.next = head;
        head.prev = newNode;

        pointsCount++;
        return newNode;
    }

    //добавляет новый элемент и возвращает ссылку на него
    private FunctionNode addNodeByIndex(int index) {
        //проверка корректности индекса
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
        }

        FunctionNode newNode = new FunctionNode(null);
        FunctionNode current;

        if (index == pointsCount) {
            //вставка в конец - перед головой
            current = head;
        } else {
            //вставка перед существующим узлом
            current = getNodeByIndex(index);
        }

        //вставка нового узла перед current
        newNode.prev = current.prev;
        newNode.next = current;
        current.prev.next = newNode;
        current.prev = newNode;

        pointsCount++;
        //сбрасываем кэш так как структура списка изменилась
        lastAccessedIndex = -1;
        return newNode;
    }

    //удаляет элемент по номеру и возвращает ссылку на удаленный элемент
    private FunctionNode deleteNodeByIndex(int index) {
        //проверка корректности индекса
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
        }
        //проверка минимального количества точек
        if (pointsCount < 3) {
            throw new IllegalStateException("количество точек должно быть не менее 3");
        }

        FunctionNode nodeToDelete = getNodeByIndex(index);

        //исключаем узел из списка - перенаправляем ссылки соседей
        nodeToDelete.prev.next = nodeToDelete.next;
        nodeToDelete.next.prev = nodeToDelete.prev;

        pointsCount--;
        //сбрасываем кэш так как структура списка изменилась
        lastAccessedIndex = -1;
        return nodeToDelete;
    }

    public double getLeftDomainBorder() {
        if (pointsCount == 0) return Double.NaN;
        return head.next.point.get_x();
    }

    public double getRightDomainBorder() {
        if (pointsCount == 0) return Double.NaN;
        return head.prev.point.get_x();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        FunctionNode node = head.next;
        while (node != head) {
            FunctionNode nextNode = node.next;
            if (nextNode == head) break;

            double x1 = node.point.get_x();
            double x2 = nextNode.point.get_x();

            if (x >= x1 && x <= x2) {
                double y1 = node.point.get_y();
                double y2 = nextNode.point.get_y();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            node = node.next;
        }
        return Double.NaN;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public FunctionPoint getPoint(int index) {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.point);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
        }

        if (index > 0 && point.get_x() <= getNodeByIndex(index - 1).point.get_x() + 1e-10) {
            throw new InappropriateFunctionPointException("x точки должен быть больше предыдущей точки");
        }
        if (index < pointsCount - 1 && point.get_x() >= getNodeByIndex(index + 1).point.get_x() - 1e-10) {
            throw new InappropriateFunctionPointException("x точки должен быть меньше следующей точки");
        }

        FunctionNode node = getNodeByIndex(index);
        node.point = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        return getNodeByIndex(index).point.get_x();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
        }

        FunctionNode node = getNodeByIndex(index);
        FunctionPoint newPoint = new FunctionPoint(x, node.point.get_y());
        setPoint(index, newPoint);
    }

    public double getPointY(int index) {
        return getNodeByIndex(index).point.get_y();
    }

    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("индекс " + index + " выходит за границы");
        }

        FunctionNode node = getNodeByIndex(index);
        node.point.set_y(y);
    }

    public void deletePoint(int index) {
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        int insertIndex = 0;
        FunctionNode node = head.next;
        while (node != head && node.point.get_x() < point.get_x() - 1e-10) {
            insertIndex++;
            node = node.next;
        }

        if (node != head && Math.abs(node.point.get_x() - point.get_x()) < 1e-10) {
            throw new InappropriateFunctionPointException("точка с таким x уже существует");
        }

        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.point = new FunctionPoint(point);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        FunctionNode node = head.next; //начинаем с первой точки
        while (node != head) {
            sb.append(node.point.toString()); //добавляем строковое представление точки
            //если не последняя точка
            if (node.next != head) {
                sb.append(", "); //добавляем разделитель
            }
            //переходим к следующему узлу
            node = node.next;
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        //проверяем ссылочное равенство
        if (this == o) return true;
        //объект реализует TabulatedFunction
        if (!(o instanceof TabulatedFunction)) return false;

        TabulatedFunction that = (TabulatedFunction) o;
        //проверяем количество точек
        if (this.getPointsCount() != that.getPointsCount()) {
            return false;
        }

        //если передана LinkedListTabulatedFunction используем прямой доступ к узлам
        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction other = (LinkedListTabulatedFunction) o;
            FunctionNode thisNode = this.head.next; //первый узел текущего списка
            FunctionNode otherNode = other.head.next; //первый узел другого списка

            while (thisNode != this.head && otherNode != other.head) {
                if (!thisNode.point.equals(otherNode.point)) { //сравниваем точки
                    return false;
                }
                //переходим к следующему узлу
                thisNode = thisNode.next;
                otherNode = otherNode.next;
            }
        } else {
            //используем интерфейсные методы
            for (int i = 0; i < pointsCount; i++) {
                FunctionPoint thisPoint = this.getPoint(i); //получаем точку по индексу
                FunctionPoint thatPoint = that.getPoint(i); //получаем точку другого объекта
                if (!thisPoint.equals(thatPoint)) { //сравниваем точки
                    return false;
                }
            }
        }

        return true; //все точки совпали
    }

    @Override
    public int hashCode() {
        int result = pointsCount; //включаем количество точек в хэш

        FunctionNode node = head.next;
        while (node != head) {
            result ^= node.point.hashCode(); //ксорим хэш-код точки
            node = node.next;
        }
        return result; //возвращаем хэш-код
    }

    @Override
    public TabulatedFunction clone() {
        try {
            //поверхностное клонирование
            LinkedListTabulatedFunction cloned = (LinkedListTabulatedFunction) super.clone();

            //инициализируем новый список
            cloned.head = new FunctionNode(null); //создаем новую голову
            cloned.head.prev = cloned.head; //ссылка на себя
            cloned.head.next = cloned.head; //ссылка на себя
            cloned.pointsCount = 0; //обнуляем счетчик
            cloned.lastAccessedNode = cloned.head; //сбрасываем кэш
            cloned.lastAccessedIndex = -1; //сбрасываем индекс

            //копируем все точки из исходного списка в новый
            FunctionNode node = this.head.next; //начинаем с первой точки исходного списка
            while (node != this.head) {
                FunctionNode newNode = cloned.addNodeToTail(); //добавляем узел в конец нового списка
                newNode.point = (FunctionPoint) node.point.clone(); //клонируем точку
                node = node.next;
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("клонирование не поддерживается", e);
        }
    }
}