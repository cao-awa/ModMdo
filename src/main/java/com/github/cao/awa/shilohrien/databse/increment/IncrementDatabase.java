package com.github.cao.awa.shilohrien.databse.increment;

import com.esotericsoftware.kryo.*;
import com.esotericsoftware.kryo.io.*;
import com.github.cao.awa.shilohrien.databse.*;
import com.github.cao.awa.shilohrien.databse.increment.filter.*;
import com.github.cao.awa.shilohrien.databse.increment.requirement.selection.*;
import com.github.zhuaidadaya.rikaishinikui.handler.conductor.string.builder.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.pair.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public class IncrementDatabase<T> extends MemoryDatabase {
    private final Object2ObjectOpenHashMap<String, IncrementDataTable<T>> tables = new Object2ObjectOpenHashMap<>();
    private IncrementDataTable<T> using;

    private IncrementDatabase() {
        super(null, null);
    }

    public IncrementDatabase(String name, String path) {
        super(name, path);
    }

    public IncrementDatabase(String name, String path, IncrementDataTable<T> initTable) {
        super(name, path);
        this.using = initTable;
        this.tables.put(initTable.getName(), initTable);
    }

    public IncrementDatabase(String name, String path, String tableName, DataBody tableHead, MainKeyActor.MainKeyTactics<T, T> mainKeyTactics) {
        super(name, path);
        this.using = new IncrementDataTable<>(tableName, tableHead, mainKeyTactics);
        this.tables.put(tableName, using);
    }

    public IncrementDatabase(String name, String path, String tableName, DataBody tableHead, MainKeyActor.MainKeyTactics<T, T> mainKeyTactics, T mainKey) {
        super(name, path);
        this.using = new IncrementDataTable<>(tableName, tableHead, mainKeyTactics, mainKey);
        this.tables.put(tableName, using);
    }

    /**
     * <p>Load database from a archived file</p>
     * <br>
     *
     * @param path database path, need a file
     * @param create if failed load, will create
     * @param <T> t
     * @return database
     */
    public static <T> IncrementDatabase<T> load(String name, String path, boolean create) {
        return EntrustParser.trying(() -> {
            Input input = new Input(new BufferedInputStream(new FileInputStream(path)));
            Kryo kryo = getKryo();
            String type = kryo.readObject(input, String.class);
            if (!type.equals("SHILOHRIEN DB>IDB -  ")) {
                return create ? new IncrementDatabase<>(name, path) : null;
            }
            IncrementDatabase<T> database = kryo.readObject(input, IncrementDatabase.class);
            input.close();
            return database;
        }, ex -> create ? new IncrementDatabase<>(name, path) : null);
    }

    public ImmutablePair<String, Long> getSnapTime() {
        return super.getSnapTime();
    }

    /**
     * <p>Save database to archive file</p>
     * >br>
     *
     * @return is operation success
     */
    public boolean save() {
        return save("IDB");
    }

    public String toString() {
        StringBuilderConductor builder = new StringBuilderConductor();
        builder.append("IncrementDatabase{");
        for (String s : tables.keySet()) {
            builder.append("\n").append(s).append(": [\n");
            builder.append(tables.get(s).toString()).append("\n]");
        }
        builder.append("\n}");
        return builder.toString();
    }

    /**
     * <p>Insert data to end, using increment key</p>
     * <br>
     *
     * @param body data body
     * @return is operation success
     */
    public boolean insert(@NotNull DataBody body) {
        if (using == null) {
            return false;
        }
        using.getHead().check(body);
        return using.insert(body);
    }

    /**
     * <p>Insert data to end, using specified key</p>
     * <br>
     *
     * @param key
     * @param body
     * @return is operation success
     */
    public boolean insert(@NotNull T key, @NotNull DataBody body) {
        if (using == null) {
            return false;
        }
        using.getHead().check(body);
        return using.insert(key, body);
    }

    /**
     * <p>Create a data table</p>
     * <br>
     *
     * @param name table name
     * @param head table head
     * @param mainKeyTactics main key incremental
     * @return is operation success
     */
    public boolean createTable(String name, DataBody head, MainKeyActor.MainKeyTactics<T, T> mainKeyTactics) {
        return createTable(name, head, mainKeyTactics, false);
    }

    /**
     * <p>Create a data table</p>
     * <br>
     *
     * @param name table name
     * @param head table head
     * @param mainKeyTactics main key incremental
     * @param force force create
     * @return is operation success
     */
    public boolean createTable(String name, DataBody head, MainKeyActor.MainKeyTactics<T, T> mainKeyTactics, boolean force) {
        if (! force && tables.containsKey(name)) {
            return false;
        }
        tables.put(name, new IncrementDataTable<>(name, head, mainKeyTactics));
        return true;
    }

    /**
     * <p>Add a data table to database</p>
     * <br>
     *
     * @param table data table
     * @return is operation success
     */
    public boolean addTable(IncrementDataTable<T> table) {
        return addTable(table, false);
    }

    /**
     * <p>Add a data table to database</p>
     * <br>
     *
     * @param table data table
     * @param force force add
     * @return is operation success
     */
    public boolean addTable(IncrementDataTable<T> table, boolean force) {
        if (! force && tables.containsKey(table.getName())) {
            return false;
        }
        tables.put(table.getName(), table);
        return true;
    }

    /**
     * <p>Get table</p>
     * <br>
     *
     * @param name table name
     * @return target table
     */
    @Nullable
    public IncrementDataTable<T> getTable(String name) {
        return tables.get(name);
    }

    /**
     * <p>Get using table</p>
     * <br>
     *
     * @return using table
     */
    public IncrementDataTable<T> getUsing() {
        return using;
    }

    /**
     * <p>Get using table incremental</p>
     * <br>
     *
     * @return using table incremental
     */
    public boolean isIncremental() {
        return using.isIncremental();
    }

    /**
     * <p>Set using table incremental</p>
     * <br>
     */
    public IncrementDataTable<T> setIncremental(boolean incremental) {
        return using.setIncremental(incremental);
    }

    /**
     * <p>Switch using to a present table</p>
     * <br>
     *
     * @param name target table
     * @return is operation success
     */
    public boolean use(String name) {
        IncrementDataTable<T> table = tables.get(name);
        if (table != null) {
            using = table;
            return true;
        }
        return false;
    }

    /**
     * <p>Query a row at the key</p>
     * <br>
     *
     * @param key key
     * @param rowName target row
     * @return the row element at the key
     */
    public Object query(T key, String rowName) {
        return using.query(key, rowName);
    }

    /**
     * <p>Query a body</p>
     * <br>
     *
     * @param key key
     * @return data body at the key
     */
    public DataBody query(T key) {
        return using.query(key);
    }

    /**
     * <p>Query successive, start with the key</p>
     * <p>Successive is after elements from the key</p>
     * <br>
     * <p>Use the negative count to query previous elements</p>
     * <br>
     *
     * @param key start with the key
     * @param count early count
     * @return collection of bodies
     */
    public Collection<DataBody> querySuccessive(@NotNull T key, int count) {
        return using.querySuccessive(key, count);
    }

    /**
     * <p>Query successive, start with the key</p>
     * <p>Successive is after elements from the key</p>
     * <br>
     * <p>Use the negative count to query previous elements</p>
     * <br>
     *
     * @param key start with the key
     * @param count early count
     * @param rowName query at row
     * @return collection of bodies
     */
    public Collection<Object> querySuccessive(@NotNull T key, int count, @NotNull String rowName) {
        return using.querySuccessive(key, count, rowName);
    }

    /**
     * <p>Delete a body</p>
     * <br>
     *
     * @param key key
     * @return is operation success
     */
    public boolean delete(T key) {
        return delete(key, false);
    }

    /**
     * <p>Delete a body</p>
     * <br>
     *
     * @param key key
     * @param force force delete
     * @return is operation success
     */
    public boolean delete(T key, boolean force) {
        return using.delete(key);
    }

    /**
     * <p>Delete target body</p>
     * <br>
     *
     * @param target target
     * @return is operation success
     */
    public boolean delete(@NotNull DataBody target) {
        using.delete(target);
        return true;
    }

    /**
     * <p>Update an body</p>
     * <p>If not key present, will create a key for update</p>
     * <br>
     *
     * @param key
     *         the key
     * @param body
     *         the body
     * @return is operation success
     */
    public boolean update(T key, DataBody body) {
        return using.update(key, body);
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
    public boolean update(T key, String rowName, Object obj) {
        return using.update(key, rowName, obj);
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
    public boolean modify(T key, String rowName, Object obj) {
        return using.modify(key, rowName, obj);
    }

    /**
     * <p>Build a disposable data filter </p>
     * <br>
     *
     * @return data filter
     */
    public IncrementDataTableFilter<T> filter() {
        return new IncrementDataTableFilter<>(using);
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
    public boolean move(T sourceKey, T toKey) {
        return using.move(sourceKey, toKey);
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
    public boolean swap(T sourceKey, T toKey) {
        return using.swap(sourceKey, toKey);
    }

    /**
     * <p>Let all element to operation</p>
     * <br>
     *
     * @param action action
     */
    public void forEach(BiConsumer<T, DataBody> action) {
        using.forEach(action);
    }

    /**
     * <p>Successive delete elements</p>
     * <br>
     *
     * @param action action and confirm delete
     */
    public void deleteSuccessive(ThreeConsumer<T, DataBody, DataKeyDeleteSelection<T>> action) {
        using.deleteSuccessive(action);
    }

    /**
     * <p>Clear all data of using table</p>
     * <br>
     */
    public void clear() {
        using.clear();
    }

    /**
     * <p>Get tables</p>
     *
     * @return tables
     */
    public Object2ObjectOpenHashMap<String, IncrementDataTable<T>> getTables() {
        return tables;
    }
}
