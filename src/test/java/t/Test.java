package t;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.utils.file.*;
import com.github.zhuaidadaya.rikaishinikui.handler.conductor.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;

import java.io.*;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        System.out.println("7a8ab53891a4eac890e8a5650c875ca2d29bae6b8acb274a409b59444ee6dfb3".length());
        Diffs diffs = new Diffs(new File("C:\\normal\\Codes\\Code-Java\\ModMdo\\src"), new File("C:\\normal\\Codes\\Code-Java\\ModMdo\\test\\diffs.diff"));
        diffs.save();
    }
}

@Disposable
class Diffs {
    private final Object2ObjectOpenHashMap<String, String> factory = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<File, String> paths = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<File, String> diffs = new Object2ObjectOpenHashMap<>();
    private final ObjectArrayList<File> waiting = new ObjectArrayList<>();

    private final File base;
    private final File save;

    public Diffs(File base, File save) {
        this.base = base;
        this.save = save;
        load();
        make();
    }

    public void make() {
        foreachFile(base);
        EntrustExecution.tryFor(waiting, file -> {
            EntrustExecution.tryTemporary(() -> {
                this.paths.put(file, FileUtil.fileSha(file, MessageDigger.Sha3.SHA_512));
                waiting.remove(file);
                if (!paths.get(file).equals(factory.get(file.toString()))) {
                    System.out.println("hasDiff: " + file);
                }
            }, ex -> ex.printStackTrace());
        });
    }

    private void foreachFile(File path) {
        ObjectArrayList<File> nextDir = new ObjectArrayList<>(List.of(path));
        ObjectArrayList<File> list = new ObjectArrayList<>();
        while (nextDir.size() > 0) {
            for (File next : nextDir) {
                nextDir.remove(next);
                list.clear();
                EntrustExecution.tryFor(next.listFiles(), file -> {
                    if (file.isFile()) {
                        list.add(file);
                    } else {
                        nextDir.add(file);
                    }
                });
                waiting.addAll(list);
            }
        }
    }

    public void save() {
        StringBuilder builder = new StringBuilder();
        for (File s : paths.keySet()) {
            EntrustExecution.notNull(paths.get(s), p -> {
                builder.append(p).append("\u00a0").append(s.toString()).append("\u0020\n");
            });
        }
        FileUtil.write(save, builder);
    }

    private void load() {
        EntrustExecution.tryTemporary(() -> {
            String str = FileUtil.strictRead(new BufferedReader(new FileReader(save)));
            StringTokenizerConductor conductor = new StringTokenizerConductor(new StringTokenizer(str, "\u0020\n"));
            for (String s : conductor) {
                factory.put(s.substring(s.indexOf("\u00a0") + 1), s.substring(0, s.indexOf("\u00a0")));
            }
        });
    }
}
