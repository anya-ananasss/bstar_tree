import java.util.*;

public class Bstar_Tree {
    int t;
    Node root;

    public Bstar_Tree(int t) {
        this.root = null;
        this.t = t;
    }


    public void insertValue(int newKey) {
        int MAX_KEYS = t - 1;
        Node auxNode;
        if (root == null) {
            List<Integer> newList = new ArrayList<>();
            newList.add(newKey);
            auxNode = new Node(newList);
            root = auxNode;
        } else {
            auxNode = (Node) nodesInspection(newKey, true);

            if (auxNode.getKeys().size() < MAX_KEYS) {
                List<Integer> tempKeys = new ArrayList<>(auxNode.getKeys());
                tempKeys.add(newKey);
                Collections.sort(tempKeys);
                auxNode.setKeys(tempKeys);

            } else if (auxNode == root) {
                auxNode.getKeys().add(newKey);
                Collections.sort(auxNode.getKeys());
                splitRootNode(auxNode);

            } else {
                workWithFullLeafNode(auxNode, MAX_KEYS, newKey);
            }
        }
    }

    public boolean deleteValue(int value) {
        int MIN_KEYS = (t - 1) * 2 / 3;
        if ((boolean) nodesInspection(value, false)) {
            Node auxNode = (Node) nodesInspection(value, true);
            if (auxNode.isLeaf()) {//если значение в листе
                List<Integer> tempAuxNode = new ArrayList<>(auxNode.getKeys());
                tempAuxNode.remove((Object) value);
                auxNode.setKeys(tempAuxNode);
                if (auxNode.getKeys().size() < MIN_KEYS) {
                    workingWithEmptiedNode(auxNode, MIN_KEYS);
                }
            } else {
                deleteFromNonLeafNode(auxNode, value, MIN_KEYS);
            }
            return true;
        }
        return false;
    }

    public void printTree() {
        Node auxNode = root;
        System.out.println("[" + auxNode.getKeys() + "]  ");
        Queue<Node> nodes = new ArrayDeque<>(auxNode.getChildrenNodes());
        int childrenAmount = nodes.size();
        while (!nodes.isEmpty()) {
            for (int i = 0; i < childrenAmount; i++) {
                assert nodes.peek() != null;
                System.out.print("[" + nodes.peek().getKeys() + "]   ");
                assert nodes.peek() != null;
                nodes.addAll(nodes.peek().getChildrenNodes());
                nodes.poll();
            }
            childrenAmount = nodes.size();
            System.out.println("  ");
        }
    }

    public boolean findValue(int value) {
        Node auxNode = root;
        if (lookForValueInNode(value, auxNode) != -1) {
            return true;
        }
        Queue<Node> nodes = new ArrayDeque<>(auxNode.getChildrenNodes());
        int childrenAmount = nodes.size();
        while (!nodes.isEmpty()) {
            for (int i = 0; i < childrenAmount; i++) {
                assert nodes.peek() != null;
                if (nodes.peek().getKeys().contains(value)) {
                    return true;
                }
                assert nodes.peek() != null;
                nodes.addAll(nodes.peek().getChildrenNodes());
                nodes.poll();
            }
            childrenAmount = nodes.size();
        }
        return false;
    }

    private void splitRootNode(Node currentNode) {
        Node newRootNode = new Node(currentNode.getKeys().subList(t / 2 - 1, t / 2));
        root = newRootNode;
        Node leftChild = new Node(currentNode.getKeys().subList(0, t / 2 - 1));
        Node rightChild = new Node(currentNode.getKeys().subList(t / 2, t)); //берем от t+1, чтобы перескочить элемент-новый корень
        if (currentNode.getChildrenNodes().size() != 0) {
            int childrenAmount = currentNode.getChildrenNodes().size();
            List<Node> childrenForLeft = new ArrayList<>(currentNode.getChildrenNodes().subList(0, childrenAmount / 2));
            List<Node> childrenForRight = new ArrayList<>(currentNode.getChildrenNodes().subList(childrenAmount / 2, childrenAmount));


            leftChild.setChildrenNodes(childrenForLeft);
            leftChild.setLeaf(false);


            rightChild.setChildrenNodes(childrenForRight);
            rightChild.setLeaf(false);

            for (Node node : childrenForLeft) {
                node.setParentNode(leftChild);
            }

            for (Node node : childrenForRight) {
                node.setParentNode(rightChild);
            }
        }

        for (Node node : Arrays.asList(leftChild, rightChild)) {
            newRootNode.getChildrenNodes().add(node);
        }
        leftChild.setParentNode(newRootNode);
        rightChild.setParentNode(newRootNode);
        newRootNode.setLeaf(false);
    }

    private void shiftKeyToTheSide(Node currentFullNode, boolean isTheSideRight) {
        List<Integer> tempParentKeysList = new ArrayList<>();
        List<Integer> tempSideKeysList = new ArrayList<>();
        List<Integer> tempCurrentNodeKeysList = new ArrayList<>(currentFullNode.getKeys());
        Node parentToCurrent = currentFullNode.getParentNode();
        int currentFullNodeIndex = getNodeIndex(currentFullNode);
        int i = getNodeIndex(currentFullNode);
        if (isTheSideRight) {
            tempSideKeysList.addAll(parentToCurrent.getChildrenNodes().get(currentFullNodeIndex + 1).getKeys()); //получили ключи узла справа от заполненного
            tempParentKeysList.addAll(parentToCurrent.getKeys()); //получили ключи родителя
            tempParentKeysList.add(currentFullNode.getKeys().get(currentFullNode.getKeys().size() - 1)); //берем самый правый ключ из заполненного...
            tempCurrentNodeKeysList.remove(tempCurrentNodeKeysList.size() - 1); //убираем....
            tempSideKeysList.add(0, tempParentKeysList.get(currentFullNodeIndex - 1)); //добавляем родителя заполненого в незаполненный справа от него, в начало
            tempParentKeysList.remove(currentFullNodeIndex - 1); // убираем этого родителя
            parentToCurrent.getChildrenNodes().get(i + 1).setKeys(tempSideKeysList); //обновляем соседа заполненого
        } else {
            tempSideKeysList.addAll(parentToCurrent.getChildrenNodes().get(currentFullNodeIndex - 1).getKeys()); //получили ключи узла слева от заполненного
            tempParentKeysList.addAll(parentToCurrent.getKeys()); //получили ключи родителя
            tempParentKeysList.add(currentFullNode.getKeys().get(0)); //берем самый левый ключ из заполненного...
            tempCurrentNodeKeysList.remove(0); //убираем....
            tempSideKeysList.add(tempParentKeysList.get(currentFullNodeIndex - 1)); //добавляем родителя заполненого в незаполненный слева от него, в конец
            tempParentKeysList.remove(currentFullNodeIndex - 1); // убираем этого родителя
            parentToCurrent.getChildrenNodes().get(i - 1).setKeys(tempSideKeysList); //обновляем соседа заполненого
        }
        parentToCurrent.setKeys(tempParentKeysList); //обновляем родителя заполненного
        currentFullNode.setKeys(tempCurrentNodeKeysList); //обновляем нынешний заполненный

    }


    private void split(Node currentNode, int MAX_KEYS, boolean noRightNode) {
        Node parentToCurrent = currentNode.getParentNode();
        Node sideNode;
        int i;
        if (noRightNode) {
            i = getNodeIndex(currentNode) - 1;

        } else {
            i = getNodeIndex(currentNode) + 1;
        }
        sideNode = parentToCurrent.getChildrenNodes().get(i);
        List<Integer> auxArray = new ArrayList<>(currentNode.getKeys());
        auxArray.addAll(sideNode.getKeys());
        if (noRightNode) {
            auxArray.add(parentToCurrent.getKeys().get(i));
        } else {
            auxArray.add(parentToCurrent.getKeys().get(i - 1));
        }
        Collections.sort(auxArray);

        //в массиве - два заполненных по (t-1) и два родителя - всего 2t-2+2 = 2t

        List<Integer> newParentList = new ArrayList<>(parentToCurrent.getKeys());
        if (noRightNode) {
            newParentList.remove(i);
        } else {
            newParentList.remove(i - 1);
        }

        for (int k : new int[]{2 * t - 2, 4 * t + 1}) {
            newParentList.add((auxArray.get((k) / 3)));
        }


        Node newParent = new Node(newParentList);
        List<Node> newAndExChildren = new ArrayList<>(parentToCurrent.getChildrenNodes());
        for (Node node : Arrays.asList(currentNode, sideNode)) {
            newAndExChildren.remove(node);
        }
        Node child1 = new Node(auxArray.subList(0, ((2 * t - 2) / 3)));
        Node child2 = new Node(auxArray.subList((2 * t + 2) / 3, (4 * t + 1) / 3));
        Node child3 = new Node(auxArray.subList((4 * t + 4) / 3, 2 * t));


        newParent.setKeys(newParentList);


        newAndExChildren.addAll(Arrays.asList(child1, child2, child3));

        newParent.setChildrenNodes(newAndExChildren);
        newParent.setLeaf(false);


        for (int j = 0; j < newAndExChildren.size(); j++) {
            newParent.getChildrenNodes().get(j).setParentNode(newParent);
        }


        if (parentToCurrent.getParentNode() != null) { //currentNode - это переполненый, его родителя нужно заменить новым набором родителей, а новому родителю присвоить в качестве родителя родителя parentToCurrent
            newParent.setParentNode(parentToCurrent.getParentNode());
            parentToCurrent.getParentNode().getChildrenNodes().add(getNodeIndex(parentToCurrent), newParent);
            parentToCurrent.getParentNode().getChildrenNodes().remove(getNodeIndex(parentToCurrent));

        } else {
            root = newParent;
        }
        if (newParent.getKeys().size() > MAX_KEYS) {
            restoreProperties_insertion(newParent, MAX_KEYS);

        }
    }

    private void restoreProperties_insertion(Node currentNode, int MAX_KEYS) {
        if (currentNode == root) {
            splitRootNode(currentNode);
        } else {
            if (currentNode.getNodeNeighbour(getNodeIndex(currentNode) + 1) == null) {
                if (currentNode.getNodeNeighbour(getNodeIndex(currentNode) - 1).getKeys().size() < MAX_KEYS) {
                    shiftKeyToTheSide(currentNode, false);
                    currentNode.getNodeNeighbour(getNodeIndex(currentNode) - 1).getChildrenNodes().add(currentNode.getChildrenNodes().get(0));
                    currentNode.getChildrenNodes().remove(0);
                } else {
                    split(currentNode, MAX_KEYS, true);
                }
            } else {
                if (currentNode.getNodeNeighbour(getNodeIndex(currentNode) + 1).getKeys().size() < MAX_KEYS) {
                    shiftKeyToTheSide(currentNode, false);
                    currentNode.getNodeNeighbour(getNodeIndex(currentNode) + 1).getChildrenNodes().add(currentNode.getChildrenNodes().get(currentNode.getChildrenNodes().size() - 1));
                    currentNode.getChildrenNodes().remove(currentNode.getChildrenNodes().size() - 1);
                } else {
                    split(currentNode, MAX_KEYS, false);
                }
            }


        }
    }


    private void workWithFullLeafNode(Node currentNode, int MAX_KEYS, Object newKey) {
        Node rightToCurrentNode;
        Node leftToCurrentNode;
        if (currentNode.getParentNode().getChildrenNodes().size() <= getNodeIndex(currentNode) + 1) {
            rightToCurrentNode = null;
            leftToCurrentNode = currentNode.getNodeNeighbour(getNodeIndex(currentNode) - 1);
        } else {
            rightToCurrentNode = currentNode.getNodeNeighbour(getNodeIndex(currentNode) + 1);
            leftToCurrentNode = null;
        }
        if (rightToCurrentNode != null) {
            if (rightToCurrentNode.getKeys().size() == MAX_KEYS) {
                currentNode.getKeys().add((int) newKey);
                split(currentNode, MAX_KEYS, false);
            } else {
                shiftKeyToTheSide(currentNode, true);
                List<Integer> tempList = new ArrayList<>(currentNode.getKeys());
                tempList.add((int) newKey);
                Collections.sort(tempList);
                currentNode.setKeys(tempList);
            }
        } else if (leftToCurrentNode != null) {
            if (leftToCurrentNode.getKeys().size() == MAX_KEYS) {
                currentNode.getKeys().add((int) newKey);
                split(currentNode, MAX_KEYS, true);
            } else {
                shiftKeyToTheSide(currentNode, false);
                List<Integer> tempList = new ArrayList<>(currentNode.getKeys());
                tempList.add((int) newKey);
                Collections.sort(tempList);
                currentNode.setKeys(tempList);
            }
        }
    }


    private Object nodesInspection(int value, boolean returnNode) {
        Node auxNode;
        auxNode = root;
        if (!returnNode) {
            if (lookForValueInNode(value, auxNode) != -1) {
                return true;
            }
        } else if (lookForValueInNode(value, auxNode) != -1) {
            return auxNode;
        }
        int i = 0;
        while (!auxNode.isLeaf()) {
            int currentValue = auxNode.getKeys().get(i);
            if (i == 0 && value < currentValue) {
                auxNode = auxNode.getChildrenNodes().get(0);
                if (lookForValueInNode(value, auxNode) != -1) {
                    break;
                }
            } else if (i == auxNode.getKeys().size() - 1 && value >= currentValue) {
                auxNode = auxNode.getChildrenNodes().get(i + 1);
                if (lookForValueInNode(value, auxNode) != -1) {
                    break;
                }
                i = 0;
            } else if (currentValue <= value && value < auxNode.getKeys().get(i + 1)) {
                auxNode = auxNode.getChildrenNodes().get(i + 1);
                if (lookForValueInNode(value, auxNode) != -1) {
                    break;
                }
                i = 0;
            } else {
                i++;
            }
            if (!returnNode) {
                if (lookForValueInNode(value, auxNode) != -1) {
                    return true;
                }
            }
        }
        if (returnNode) {
            return auxNode;
        } else {
            return lookForValueInNode(value, auxNode) != -1;
        }
    }

    private int lookForValueInNode(int value, Node node) {
        for (int j = 0; j < node.getKeys().size(); j++) {
            if (node.getKeys().get(j) == value) {
                return j;
            }
        }
        return -1;
    }


    private void borrowingAKey(Node currentEmptyNode, boolean isTheSideRight) {
        List<Integer> tempParentKeysList = new ArrayList<>(currentEmptyNode.getParentNode().getKeys());
        List<Integer> tempSideKeysList = new ArrayList<>();
        List<Integer> tempCurrentNodeKeysList = new ArrayList<>(currentEmptyNode.getKeys());
        Node parentToCurrent = currentEmptyNode.getParentNode();
        int currentEmptyNodeIndex = getNodeIndex(currentEmptyNode);
        int i = getNodeIndex(currentEmptyNode);
        if (isTheSideRight) {
            tempSideKeysList.addAll(parentToCurrent.getChildrenNodes().get(currentEmptyNodeIndex + 1).getKeys()); //получили ключи узла справа от полупустого
            tempParentKeysList.add(tempSideKeysList.get(0)); //берем самый левый ключ у правого соседа и добавляем его родителю в конец...
            tempSideKeysList.remove(0);//убираем этот ключ у соседа
            tempCurrentNodeKeysList.add(tempParentKeysList.get(0));//берем самый левый ключ у родителя, добавляем его нынешнему полупустому узлу в конец
            tempParentKeysList.remove(0); //убираем этот ключ у родителя
            parentToCurrent.getChildrenNodes().get(i + 1).setKeys(tempSideKeysList); //обновляем соседа заполненого
        } else {
            tempSideKeysList.addAll(parentToCurrent.getChildrenNodes().get(currentEmptyNodeIndex - 1).getKeys()); //получили ключи узла слева от полупустого
            tempParentKeysList.add(0, tempSideKeysList.get(tempSideKeysList.size() - 1)); //берем самый правый ключ у левого соседа и добавляем его родителю в начало...
            tempSideKeysList.remove(tempSideKeysList.size() - 1);//убираем этот ключ у соседа
            tempCurrentNodeKeysList.add(0, tempParentKeysList.get(tempSideKeysList.size() - 1));//берем самый правый ключ у родителя, добавляем его нынешнему полупустому узлу в начало
            tempParentKeysList.remove(tempSideKeysList.size() - 1); //убираем этот ключ у родителя
            parentToCurrent.getChildrenNodes().get(i - 1).setKeys(tempSideKeysList);
        }
        parentToCurrent.setKeys(tempParentKeysList); //обновляем родителя заполненного
        currentEmptyNode.setKeys(tempCurrentNodeKeysList); //обновляем нынешний заполненный
    }


    private void centralizeEmptyNode(Node currentNode) {
        borrowingAKey(currentNode, currentNode.getNodeNeighbour(getNodeIndex(currentNode) + 1) != null);
    }

    private void merge(Node currentNode, int MIN_KEYS) {
        int nodeIndex = getNodeIndex(currentNode);
        Node parentToCurrent = currentNode.getParentNode();
        Node leftNeighbour = new Node(currentNode.getNodeNeighbour(nodeIndex - 1).getKeys());
        Node rightNeighbour = new Node(currentNode.getNodeNeighbour(nodeIndex + 1).getKeys());

        List<Integer> auxArray = new ArrayList<>();
        for (Node node : Arrays.asList(currentNode, leftNeighbour, rightNeighbour)) {
            auxArray.addAll(node.getKeys());
        }
        for (int MAX_KEYS : new int[]{nodeIndex - 1, nodeIndex}) {
            auxArray.add(currentNode.getParentNode().getKeys().get(MAX_KEYS));
        }
        Collections.sort(auxArray);

        //2 узла - 2/3(t-1); 1 узел -2/3(t-1)-1; +2 родителя - итого в массиве 2t-1 ключей


        List<Integer> newParentList = new ArrayList<>(parentToCurrent.getKeys());
        newParentList.add(auxArray.get((2 * t - 1) / 2));
        for (int i : new int[]{nodeIndex - 1, nodeIndex}) {
            newParentList.remove(currentNode.getParentNode().getKeys().get(i));
        }
        Collections.sort(newParentList);
        Node newParent = new Node(newParentList);


        List<Node> newAndExChildren = new ArrayList<>(parentToCurrent.getChildrenNodes());


        Node child1 = new Node(auxArray.subList(0, (2 * t - 1) / 2));
        Node child2 = new Node(auxArray.subList((2 * t - 1) / 2 + 1, 2 * t - 1));


        for (int i : new int[]{nodeIndex - 1, nodeIndex + 1, nodeIndex}) {
            newAndExChildren.remove(currentNode.getNodeNeighbour(i));
        }
        newAndExChildren.addAll(0, Arrays.asList(child1, child2));


        newParent.setChildrenNodes(newAndExChildren);


        newParent.setLeaf(false);
        for (Node node : Arrays.asList(child1, child2)) {
            node.setParentNode(newParent);
        }


        if (parentToCurrent.getParentNode() != null) {
            newParent.setParentNode(parentToCurrent.getParentNode());
            parentToCurrent.getParentNode().getChildrenNodes().add(getNodeIndex(parentToCurrent), newParent);
            parentToCurrent.getParentNode().getChildrenNodes().remove(getNodeIndex(parentToCurrent));

        } else {
            root = newParent;
        }

        restoreProperties_deletion(newParent, MIN_KEYS);

    }


    private void deleteFromNonLeafNode(Node currentNode, int valueToDelete, int MIN_KEYS) {
        Node auxNode;
        auxNode = currentNode;
        int i = 0;
        while (!auxNode.isLeaf()) {
            auxNode = auxNode.getChildrenNodes().get(getNodeIndex(auxNode) + i);
            i++;
        }
        List<Integer> auxNodeKeys = new ArrayList<>(auxNode.getKeys());
        List<Integer> currentNodeKeys = new ArrayList<>(currentNode.getKeys());


        currentNodeKeys.add(auxNodeKeys.get(auxNodeKeys.size() - 1));
        Collections.sort(currentNodeKeys);
        currentNodeKeys.remove((Object) valueToDelete);
        auxNodeKeys.remove(auxNodeKeys.size() - 1);
        auxNode.setKeys(auxNodeKeys);
        currentNode.setKeys(currentNodeKeys);

        restoreProperties_deletion(auxNode, MIN_KEYS);
    }


    private void workingWithEmptiedNode(Node currentNode, int MIN_KEYS) {
        if (currentNode.getNodeNeighbour(getNodeIndex(currentNode) + 1) != null) {
            if (currentNode.getNodeNeighbour(getNodeIndex(currentNode) + 1).getKeys().size() - 1 > MIN_KEYS || currentNode.getParentNode() == root) {
                borrowingAKey(currentNode, true);
                currentNode = currentNode.getNodeNeighbour(getNodeIndex(currentNode) + 1);
            }
        }
        if (currentNode.getKeys().size() < MIN_KEYS) {
            if (currentNode.getNodeNeighbour(getNodeIndex(currentNode) - 1) != null) {
                if (currentNode.getNodeNeighbour(getNodeIndex(currentNode) - 1).getKeys().size() - 1 > MIN_KEYS || currentNode.getParentNode() == root) {
                    borrowingAKey(currentNode, false);
                    currentNode = currentNode.getNodeNeighbour(getNodeIndex(currentNode) - 1);
                }
            }
        }

        if (currentNode.getKeys().size() < MIN_KEYS) {
            if (currentNode.getNodeNeighbour(getNodeIndex(currentNode) + 1) == null || currentNode.getNodeNeighbour(getNodeIndex(currentNode) - 1) == null) {
                centralizeEmptyNode(currentNode);
                if (currentNode.getNodeNeighbour(getNodeIndex(currentNode) + 1) == null) {
                    currentNode = currentNode.getNodeNeighbour(getNodeIndex(currentNode) - 1);
                } else {
                    currentNode = currentNode.getNodeNeighbour(getNodeIndex(currentNode) + 1);
                }
                merge(currentNode, MIN_KEYS);
            }
        }
    }


    private void restoreProperties_deletion(Node auxNode, int MIN_KEYS) {
        if (auxNode.getKeys().size() < MIN_KEYS) {
            if (auxNode.getNodeNeighbour(getNodeIndex(auxNode) - 1) != null) {
                if (auxNode.getNodeNeighbour(getNodeIndex(auxNode) - 1).getKeys().size() > MIN_KEYS) {
                    borrowingAKey(auxNode, false);
                } else {
                    if (auxNode.getNodeNeighbour(getNodeIndex(auxNode) + 1) == null || auxNode.getNodeNeighbour(getNodeIndex(auxNode) - 1) == null) {
                        centralizeEmptyNode(auxNode);
                        if (auxNode.getNodeNeighbour(getNodeIndex(auxNode) + 1) == null) {
                            auxNode = auxNode.getNodeNeighbour(getNodeIndex(auxNode) - 1);
                        } else {
                            auxNode = auxNode.getNodeNeighbour(getNodeIndex(auxNode) + 1);
                        }
                        merge(auxNode, MIN_KEYS);
                    }
                }
            } else {
                if (auxNode.getNodeNeighbour(getNodeIndex(auxNode) + 1).getKeys().size() > MIN_KEYS) {
                    borrowingAKey(auxNode, true);
                } else {
                    if (auxNode.getNodeNeighbour(getNodeIndex(auxNode) + 1) == null || auxNode.getNodeNeighbour(getNodeIndex(auxNode) - 1) == null) {
                        centralizeEmptyNode(auxNode);
                        if (auxNode.getNodeNeighbour(getNodeIndex(auxNode) + 1) == null) {
                            auxNode = auxNode.getNodeNeighbour(getNodeIndex(auxNode) - 1);
                        } else {
                            auxNode = auxNode.getNodeNeighbour(getNodeIndex(auxNode) + 1);
                        }
                        merge(auxNode, MIN_KEYS);
                    }
                }
            }

        }
    }


    private int getNodeIndex(Node childNode) {
        Node parentNode;
        if (childNode.getParentNode() == null) {
            return 0;
        }
        parentNode = childNode.getParentNode();
        Node auxNode = parentNode.getChildrenNodes().get(0);

        int i = 0;
        while (auxNode != childNode) {
            i++;
            auxNode = parentNode.getChildrenNodes().get(i);
        }
        return i;
    }


}

class Node {
    private List<Integer> keys;
    private List<Node> childrenNodes = new ArrayList<>();
    private Node parentNode;
    private boolean isLeaf;

    public Node(List<Integer> keys) {
        this.keys = keys;
        this.isLeaf = true;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public List<Node> getChildrenNodes() {
        return childrenNodes;
    }


    public List<Integer> getKeys() {
        return keys;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public void setChildrenNodes(List<Node> childrenNodes) {
        this.childrenNodes = childrenNodes;
    }

    public void setKeys(List<Integer> keys) {
        this.keys = keys;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public Node getNodeNeighbour(int neighbourIndex) {
        if (this.getParentNode() != null) {
            if (neighbourIndex < this.getParentNode().getChildrenNodes().size() && neighbourIndex >= 0) {
                return this.getParentNode().getChildrenNodes().get(neighbourIndex);
            }
        }
        return null;
    }
}

