package com.github.zhuaidadaya.rikaishinikui.handler.conductor.thread;

import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.*;
import java.util.function.*;

public class MapCountDownConductor<K, V> {
    private final Object2ObjectArrayMap<K, V> map = new Object2ObjectArrayMap<>();
    private final ObjectArrayList<K> leastList = new ObjectArrayList<>();
    private int participated;
    private int done;

    public void put(K key, V value) {
        map.put(key, value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public void reset() {
        participated = 0;
        done = 0;
    }

    public void await() {
        int waiting = 0;
        while (done != participated) {
            TimeUtil.coma(2);
            waiting++;

            if (waiting > 100) {
                for (K k : leastList) {
                    if (map.get(k) instanceof TaskOrder<?> task) {
                        if (! task.isRunning()) {
//                            System.out.println("");
                            done(k);
                        }
                    }
                }
            }
        }
    }

    public Collection<V> values() {
        return map.values();
    }

    public void done(K value) {
        done++;
        leastList.remove(value);
    }

    public int size() {
        return map.size();
    }

    public void participate(K key, BiConsumer<K, V> action) {
        leastList.add(key);
        action.accept(key, map.get(key));
        participated++;
    }
}
