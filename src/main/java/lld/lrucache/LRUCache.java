package lld.lrucache;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final Node<K, V> head;
    private final Node<K, V> tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head = new Node<>(null, null);
        tail = new Node<>(null, null);
        map = new HashMap<>(capacity);
        head.setNext(tail);
        tail.setPrev(head);
    }

    public synchronized V get(K key){
        if (map.containsKey(key)){
            var node = map.get(key);
            moveToFront(node);
            return node.getValue();
        }else return null;
    }

    public synchronized void put(K key, V value){
        if (map.containsKey(key)){
            var existing = map.get(key);
            existing.setValue(value);
            moveToFront(existing);
            return;
        }
        if (map.size()>=capacity){
            var lastNode = tail.getPrev();
            remove(lastNode);
            map.remove(lastNode.getKey());
        }
        Node<K, V> node = new Node<>(key, value);
        addToFront(node);
        map.put(key, node);
    }

    public synchronized int size() {
        return map.size();
    }

    private void addToFront(Node<K, V> curr) {
        merge(curr, head, head.getNext());
    }

    private void remove(Node<K, V> node) {
        var prev = node.getPrev();
        var next = node.getNext();
        prev.setNext(next);
        next.setPrev(prev);
    }

    private void moveToFront(Node<K, V> curr) {
        remove(curr);
        addToFront(curr);
    }

    private void merge(Node<K, V> curr, Node<K, V> prev, Node<K, V> next) {
        curr.setNext(prev.getNext());
        curr.setPrev(prev);
        prev.setNext(curr);
        next.setPrev(curr);
    }

}
