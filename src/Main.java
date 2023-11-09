import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Bstar_Tree newTree = null;
        workWithTree(newTree);
    }

    private static void workWithTree(Bstar_Tree newTree) {
        while (true) {
            System.out.println("Выберите действие: \n1. Создать новое дерево степенью t \n2. Создать тестовое дерево \n3. Вставить новое значение  \n4. Найти значение \n5. Удалить значение \n6. Вывести дерево \n7. Очистить дерево \n8. Выйти");
            Scanner scanner = new Scanner(System.in);
            int value;
            int newCase = 0;
            try {
                newCase = scanner.nextInt();
            } catch (InputMismatchException im) {
                System.out.println("Введено неверное значение, введите целое число от 1 до 8");
            }
            switch (newCase) {
                case (1):
                    System.out.print("Введите желаемое значение степени (целое положительное число, большее на 1 любого кратное трем числа):");
                    try {
                        int t = scanner.nextInt();
                        if ((t - 1) % 3 != 0) {
                            System.out.println("Введено неверное значение: степень не является числом, большим на 1 любого кратного трем числа");
                        } else {
                            newTree = new Bstar_Tree(t);
                            System.out.println("Дерево создано");
                            break;
                        }
                    } catch (InputMismatchException im) {
                        System.out.println("Введено неверное значение, введите целое положительное число, большее на 1 любого кратного трем числа");
                    }
                    break;
                case (2):
                    newTree = new Bstar_Tree(4);
                    for (int i = 1; i < 22; i++) {
                        newTree.insertValue(i);
                    }
                    System.out.println("Тестовое дерево создано");
                    break;
                case (3):
                    System.out.print("Введите значение, которое хотите вставить: ");
                    value = scanner.nextInt();
                    if (newTree != null) {
                        newTree.insertValue(value);
                        System.out.println("Значение вставлено");
                    } else {
                        System.out.println("Необходимо создать дерево перед вставкой нового элемента");
                    }
                    break;

                case (4):
                    System.out.print("Введите значение, которое хотите найти: ");
                    value = scanner.nextInt();
                    if (newTree != null) {
                        if (newTree.findValue(value)) {
                            System.out.println("Значение присутствует в дереве");
                        } else {
                            System.out.println("Значение не найдено");
                        }
                    } else {
                        System.out.println("Необходимо создать дерево перед поиском элемента");
                    }
                    break;

                case (5):
                    System.out.print("Введите значение, которое хотите удалить: ");
                    value = scanner.nextInt();
                    if (newTree != null) {
                        if (newTree.deleteValue(value)) {
                            System.out.println("Удаление прошло успешно");
                        } else {
                            System.out.println("Значение не найдено, удаление не было выполнено");
                        }
                    } else {
                        System.out.println("Необходимо создать дерево перед удалением элемента");
                    }
                    break;
                case (6):
                    if (newTree != null) {
                        newTree.printTree();
                    } else {
                        System.out.println("Необходимо создать дерево перед его выводом");
                    }
                    break;
                case (7):
                    if (newTree != null) {
                        newTree = new Bstar_Tree(newTree.t);
                        System.out.println("Дерево очищено");
                    } else {
                        System.out.println("Необходимо создать дерево перед его очисткой");
                    }
                    break;
                case (8):
                    return;
                default:
                    System.out.println("Введено неверное значение, введите целое положительное число от 1 до 8");
                    break;
            }
        }
    }
}