package com.github.cao.awa.shilohrien.databse.increment;

import com.github.cao.awa.shilohrien.databse.*;
import com.github.cao.awa.shilohrien.databse.increment.requirement.requires.*;
import com.github.cao.awa.shilohrien.databse.increment.requirement.selection.*;
import com.github.zhuaidadaya.rikaishinikui.handler.conductor.string.builder.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.action.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class IncrementDataTable<T> implements Cloneable {
    private final DataBody NOT_NULL_CHECKER;
    private final String name;
    private final DataBody head;
    private final Object2ObjectLinkedOpenHashMap<T, DataBody> data = new Object2ObjectLinkedOpenHashMap<>();
    private final MainKeyActor<T> mainKeyTactics;
    private boolean incremental = true;

    private IncrementDataTable() {
        this.head = DataBody.body();
        this.name = "";
        this.mainKeyTactics = new MainKeyActor<>();

        NOT_NULL_CHECKER = DataBody.body();
    }

    public IncrementDataTable(String name, DataBody head, MainKeyActor.MainKeyTactics<T, T> mainKeyTactics) {
        this.head = head;
        this.name = name;
        this.mainKeyTactics = new MainKeyActor<>(mainKeyTactics);

        if (mainKeyTactics instanceof MainKeyActor.KeepMainKeyTactics<T>) {
            setIncremental(false);
        }

        NOT_NULL_CHECKER = EntrustParser.trying(() -> EntrustParser.operation(head.clone(), body -> {
            body.setRequirements(new Object2ObjectOpenHashMap<>());
            body.allRequire(new DataRequireNotNull());
        }));

        if (NOT_NULL_CHECKER != null) {
            NOT_NULL_CHECKER.check();
        }
    }

    public IncrementDataTable(String name, DataBody head, MainKeyActor.MainKeyTactics<T, T> mainKeyTactics, T mainKey) {
        this.head = head;
        this.name = name;
        this.mainKeyTactics = new MainKeyActor<>(mainKeyTactics, mainKey);

        if (mainKeyTactics instanceof MainKeyActor.KeepMainKeyTactics<T>) {
            setIncremental(false);
        }

        NOT_NULL_CHECKER = EntrustParser.trying(() -> EntrustParser.operation(head.clone(), body -> {
            body.setRequirements(new Object2ObjectOpenHashMap<>());
            body.allRequire(new DataRequireNotNull());
        }));

        if (NOT_NULL_CHECKER != null) {
            NOT_NULL_CHECKER.check();
        }
    }

    public static IncrementDataTable<Integer> integerKey(String name, DataBody head, int mainKey) {
        return new IncrementDataTable<>(name, head, new MainKeyActor.IntegerMainKeyTactics(), mainKey);
    }

    public static IncrementDataTable<Integer> integerKey(String name, DataBody head) {
        return new IncrementDataTable<>(name, head,  new MainKeyActor.IntegerMainKeyTactics(), 0);
    }

    public void forEach(BiConsumer<T, DataBody> action) {
        data.forEach(action);
    }

    public void deleteSuccessive(ThreeConsumer<T, DataBody, DataKeyDeleteSelection<T>> action) {
        DataKeyDeleteSelection<T> keys = new DataKeyDeleteSelection<>();

        data.forEach((k, v) -> action.accept(k, v, keys));

        keys.forEach(this::delete);
    }

    public boolean delete(@NotNull T index) {
        data.remove(index);
        return true;
    }

    public DataBody getHead() {
        return head;
    }

    public String getName() {
        return name;
    }

    public boolean insert(@NotNull DataBody body) {
        if (isIncremental()) {
            data.put(mainKeyTactics.action(), body);
            return true;
        } else {
            throw new IllegalStateException("This table is disabled incremental, must insert with a key");
        }
    }

    public boolean isIncremental() {
        return incremental;
    }

    public IncrementDataTable<T> setIncremental(boolean incremental) {
        this.incremental = incremental;
        return this;
    }

    public boolean insert(@NotNull T key, @NotNull DataBody body) {
        data.put(key, body);
        return true;
    }

    /**
     * <p>Update an element</p>
     * <p>If not key present, will put an update</p>
     * <br>
     *
     * @param key
     *         the key
     * @param body
     *         update value
     * @return is operation success
     */
    public boolean update(@NotNull T key, @NotNull DataBody body) {
        if (data.containsKey(key)) {
            data.replace(key, body);
        } else {
            data.put(key, body);
        }
        return true;
    }

    /**
     * <p>Update an element</p>
     * <p>If not key present, will create a NULL_BODY and update</p>
     * <br>
     *
     * @param key
     *         the key
     * @param rowName
     *         the name of body row
     * @param obj
     *         update value
     * @return is operation success
     */
    public boolean update(@NotNull T key, @NotNull String rowName, @NotNull Object obj) {
        int index = head.getIndex(rowName);
        if (index != - 1) {
            DataBody body = data.get(key);
            if (body != null) {
                body.update(index, obj);
            } else {
                data.put(key, new DataBody().update(index, obj));
            }
            return true;
        }
        return false;
    }

    /**
     * <p>Modify an element, need key present</p>
     * <p>If key is not preset, will fail</p>
     * <br>
     *
     * @param key
     *         the key
     * @param rowName
     *         the name of body row
     * @param obj
     *         modify value
     * @return this
     */
    public boolean modify(@NotNull T key, @NotNull String rowName, @NotNull Object obj) {
        int index = head.getIndex(rowName);
        if (index != - 1) {
            DataBody body = data.get(key);
            if (body != null) {
                body.update(index, obj);
            }
            return true;
        }
        return false;
    }

    @Nullable
    public Object query(@NotNull T key, @NotNull String rowName) {
        DataBody body = query(key);
        return body != null ? body.get(head.getIndex(rowName)) : null;
    }

    @Nullable
    public DataBody query(@NotNull T key) {
        return data.get(key);
    }

    public Collection<DataBody> querySuccessive(@NotNull T key, int early) {
        ObjectArrayList<DataBody> bodies = new ObjectArrayList<>();

        List<T> keyList = data.keySet().stream().toList();
        int index = keyList.indexOf(key);
        Do.letForUp(index - early > 0 ? early : 0, index, integer -> {
            bodies.add(data.get(keyList.get(integer)));
        });

        return bodies;
    }

    public Collection<Object> querySuccessive(@NotNull T key, int early, @NotNull String rowName) {
        ObjectArrayList<Object> bodies = new ObjectArrayList<>();

        int bodyIndex = head.getIndex(rowName);
        List<T> keyList = data.keySet().stream().toList();
        int index = keyList.indexOf(key);
        Do.letForUp(index - early > 0 ? early : 0, index, integer -> {
            bodies.add(data.get(keyList.get(integer)).get(bodyIndex));
        });

        return bodies;
    }

    /**
     * <p>Delete target body</p>
     * <br>
     *
     * @param target target
     * @return is operation success
     */
    public boolean delete(@NotNull DataBody target) {
        data.remove(getIndex(target));
        return true;
    }

    /**
     * <p>Index to target</p>
     * <br>
     *
     * @param t target
     * @return key of target
     */
    public T getIndex(@NotNull DataBody t) {
        return data.keySet().stream().toList().get(data.values().stream().toList().indexOf(t));
    }

    public ObjectSortedSet<T> keySet() {
        return data.keySet();
    }

    public ObjectCollection<DataBody> values() {
        return data.values();
    }

    /**
     * <p>Move an element from sourceKey to toKey and delete sourceKey</p>
     * <br>
     *
     * @param sourceKey
     *         the element key
     * @param toKey
     *         another element key
     * @return is operation success
     */
    public boolean move(@NotNull T sourceKey, @NotNull T toKey) {
        DataBody body = data.get(sourceKey);
        if (body != null && data.containsKey(toKey)) {
            data.replace(toKey, body);
            delete(sourceKey);
            return true;
        }
        return false;
    }

    /**
     * <p>Swap two element in the table </p>
     * <p>Only Swapping data, do not change the key </p>
     * <br>
     *
     * @param sourceKey
     *         the element key
     * @param toKey
     *         another element key
     * @return is operation success
     */
    public boolean swap(@NotNull T sourceKey, @NotNull T toKey) {
        DataBody source = data.get(sourceKey);
        DataBody to = data.get(toKey);
        if (source != null && to != null) {
            data.put(toKey, source);
            data.put(sourceKey, to);
            return true;
        }
        return false;
    }

    public IncrementDataTable<T> clone() throws CloneNotSupportedException {
        return (IncrementDataTable<T>) super.clone();
    }

    public String toString() {
        Int2IntOpenHashMap maxLengths = new Int2IntOpenHashMap();
        int keyMaxLength = 5;

        BiConsumer<T, DataBody> calculateLengths = (key, body) -> {
            int i = 0;
            for (Object element : body.elements()) {
                i++;
                int length = element == null ? 4 : element.toString().length();
                if (maxLengths.getOrDefault(i, 0) < length) {
                    maxLengths.put(i, length);
                }
            }
        };

        for (T element : data.keySet()) {
            int length = element == null ? 5 : element.toString().length();
            if (keyMaxLength < length) {
                keyMaxLength = length;
            }
        }

        data.forEach(calculateLengths);
        calculateLengths.accept(null, head);

        StringBuilderConductor builder = new StringBuilderConductor();
        builder.append("DataTable {\n");

        StringBuilder information = new StringBuilder();
        information.append("name: ").append(name) // name
                .append(", \nrows: ").append(head.size()) // rows
                .append(", \nlines: ").append(data.size()) // lines
                .append(", \ntheory total: ").append(size()) // theory size
                .append(", \ncurrent total: ").append(total()) // current count
                .append("\n");
        builder.append(information);

        int headLength = 0;
        int index = 1;

        {
            String element = "<KEY>";
            if (element.length() < keyMaxLength) {
                String ident = " ".repeat(keyMaxLength - element.length());
                builder.append(element).append(ident).append(" | ");
                headLength += element.length() + ident.length() + 3;
            } else {
                builder.append(element).append(" | ");
                headLength += element.length() + 3;
            }
        }
        for (Object obj : head.elements()) {
            int shouldLength = maxLengths.get(index);
            String element = obj.toString();
            if (element.length() < shouldLength) {
                String ident = " ".repeat(shouldLength - element.length());
                builder.append(element).append(ident).append(" | ");
                headLength += element.length() + ident.length() + 3;
            } else {
                builder.append(element).append(" | ");
                headLength += element.length() + 3;
            }
            index++;
        }
        if (data.size() > 0) {
            builder.append("\n");
            builder.append("-".repeat(headLength - 1));
            builder.append("\n");

            List<T> keyList = data.keySet().stream().toList();

            int i = 0;

            for (DataBody body : data.values()) {
                {
                    T element = keyList.get(i++);
                    String key = element == null ? "NULL" : element.toString();
                    if (key.length() < keyMaxLength) {
                        builder.append(key).append(" ".repeat(keyMaxLength - key.length())).append(" | ");
                    } else {
                        builder.append(key).append(" | ");
                    }
                }
                for (index = 1; index <= head.size(); index++) {
                    Object element = body.get(index);
                    String str = element == null ? "NULL" : element.toString();
                    int shouldLength = maxLengths.get(index);
                    if (str.length() < shouldLength) {
                        builder.append(str).append(" ".repeat(shouldLength - str.length())).append(" | ");
                    } else {
                        builder.append(str).append(" | ");
                    }
                }
                builder.append("\n");
            }
        } else {
            builder.append("\n");
        }
        builder.insert(12 + information.length(), "-".repeat(headLength - 1) + "\n");
        builder.append("-".repeat(headLength - 1));
        return builder.toString();
    }

    public long total() {
        AtomicLong count = new AtomicLong();
        data.values().parallelStream().forEach(body -> count.getAndAdd(body.elements().stream().filter(Objects::nonNull).count()));
        return count.get();
    }

    public long size() {
        return (long) head.size() * data.size();
    }

    /**
     * <p>Clear all data </p>
     * <br>
     */
    public void clear() {
        data.clear();
    }
}
