package com.github.cao.awa.shilohrien.databse;

import com.esotericsoftware.kryo.*;
import com.esotericsoftware.kryo.io.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.cao.awa.shilohrien.databse.increment.*;
import com.github.cao.awa.shilohrien.databse.increment.requirement.*;
import com.github.cao.awa.shilohrien.databse.increment.requirement.requires.*;
import com.github.cao.awa.shilohrien.databse.kv.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.pair.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import it.unimi.dsi.fastutil.objects.*;

import java.io.*;
import java.text.*;
import java.util.*;

public abstract class MemoryDatabase implements Cloneable {
    private static Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);

        // Table register
        kryo.register(IncrementDataTable.class);

        // Body register
        kryo.register(DataBody.class);

        // Database register
        kryo.register(IncrementDatabase.class);
        kryo.register(KVDatabase.class);

        // Requirements register
        kryo.register(DataRequirements.class);

        // Requirements register
        kryo.register(DataRequireNotNull.class);
        kryo.register(DataRequireInstanceOf.class);
        kryo.register(DataRequireEquals.class);

        // Pair register
        kryo.register(ImmutablePair.class);

        // List register
        kryo.register(ObjectArrayList.class);

        // Set register
        kryo.register(ObjectArraySet.class);

        // Map register
        kryo.register(Object2ObjectLinkedOpenHashMap.class);
        kryo.register(Object2ObjectArrayMap.class);
        kryo.register(Object2ObjectOpenHashMap.class);

        // Class register
        kryo.register(Class.class);

        // Main key actor register
        kryo.register(MainKeyActor.class);

        // Lambda register
        kryo.register(Action.class);
    }

    private final String path;
    private final String name;
    private MemoryDatabase nextSaveTask = null;
    private ImmutablePair<String, Long> snapTime = EntrustParser.operation(() -> {
        long time = TimeUtil.millions();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return new ImmutablePair<>(date.format(calendar.getTime()), time);
    });
    public MemoryDatabase(String name, String path) {
        this.path = path;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ImmutablePair<String, Long> getSnapTime() {
        return snapTime;
    }

    public void setSnapTime(ImmutablePair<String, Long> snapTime) {
        this.snapTime = snapTime;
    }

    public boolean save(String dbType) {
        setNextSaveTask(EntrustParser.trying(this::clone));
        if (getNextSaveTask() == null) {
            return false;
        }
        MemoryDatabase database = getNextSaveTask();
        EntrustExecution.tryTemporary(() -> {
            File file = new File(getPath());
            boolean hasFile = (file.isFile() || file.getParentFile().isDirectory()) || file.getParentFile().mkdirs() || file.createNewFile();
            if (hasFile) {
                Kryo kryo = getKryo();
                Output output = new Output(new BufferedOutputStream(new FileOutputStream(getPath())));
                kryo.writeObject(output, "SHILOHRIEN DB>" + dbType + " -  ");
                kryo.writeObject(output, database);
                output.close();
            }
        }, ex -> ex.printStackTrace());
        return true;
    }

    public static Kryo getKryo() {
        return kryo;
    }

    public static void setKryo(Kryo kryo) {
        MemoryDatabase.kryo = kryo;
    }

    public MemoryDatabase getNextSaveTask() {
        return nextSaveTask;
    }

    public void setNextSaveTask(MemoryDatabase nextSaveTask) {
        this.nextSaveTask = nextSaveTask;
    }

    public String getPath() {
        return path;
    }

    public MemoryDatabase clone() throws CloneNotSupportedException {
        MemoryDatabase database = (MemoryDatabase) super.clone();
        database.setSnapTime(EntrustParser.operation(() -> {
            long time = TimeUtil.millions();
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Calendar calendar = Calendar.getInstance();
            return new ImmutablePair<>(date.format(calendar.getTime()), time);
        }));
        return database;
    }
}
