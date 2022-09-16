package managment.inMemory;

import managment.HistoryManager;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private LinkedList.Node<Task> head;
    private LinkedList.Node<Task> tail;
    private Map<Integer, LinkedList.Node<Task>> nodeMap = new HashMap<>();

    public void removeNode(LinkedList.Node el) {
        if (nodeMap.containsValue(el)) {
            final LinkedList.Node next = el.next;
            final LinkedList.Node prev = el.prev;
            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                el.prev = null;
            }
            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                el.next = null;
            }
            el.data = null;
        }

    }

    public void linkLast(Task task) {
        final LinkedList.Node<Task> oldTail = tail;
        final LinkedList.Node<Task> newNode = new LinkedList.Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;

        } else {
            oldTail.next = newNode;

        }
        if (task != null) {
            nodeMap.put(task.getIdTask(), newNode);
        }
    }


    public List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        LinkedList.Node<Task> cur = this.head;
        while (cur != null) {
            history.add(cur.data);
            cur = cur.next;
        }
        return history;

    }

    @Override
    public void remove(int id) {
        removeNode(nodeMap.get(id));
        nodeMap.remove(id);

    }

    @Override
    public void add(Task task) {
        if (task != null && nodeMap.containsKey(task.getIdTask())) {
            removeNode(nodeMap.get(task.getIdTask()));
            nodeMap.remove(task.getIdTask());
            linkLast(task);
        } else {
            linkLast(task);
        }

    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
