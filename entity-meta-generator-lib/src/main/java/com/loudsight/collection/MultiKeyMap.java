package com.loudsight.collection;

import java.util.HashMap;
import java.util.Map;

public class MultiKeyMap<K1, K2, V> {
    private final Map<K1, Map<K2, V>> map;

    public MultiKeyMap(/*Pair<K1, Pair<K2, V>>... init*/) {
        map = new HashMap<>();
    }

    public void put(K1 key1, K2 key2, V value) {
        map.computeIfAbsent(key1, k -> new HashMap<>()).put(key2, value);
    }

    public V get(K1 key1, K2 key2) {
        Map<K2, V> innerMap = map.get(key1);
        if (innerMap != null) {
            return innerMap.get(key2);
        }
        return null;
    }

    public boolean containsKey(K1 key1, K2 key2) {
        Map<K2, V> innerMap = map.get(key1);
        return innerMap != null && innerMap.containsKey(key2);
    }

    public void remove(K1 key1, K2 key2) {
        Map<K2, V> innerMap = map.get(key1);
        if (innerMap != null) {
            innerMap.remove(key2);
            if (innerMap.isEmpty()) {
                map.remove(key1);
            }
        }
    }
}
