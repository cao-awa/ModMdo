package com.github.cao.awa.modmdo.backup.share;

import com.github.cao.awa.modmdo.backup.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.action.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.limit.*;
import it.unimi.dsi.fastutil.objects.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ShareBackupLibrary extends Storable {
    private final ThreeObjectMap<ShareBackup> libraries = new ThreeObjectMap<>();
    private ShareBackup center;

    public ShareBackupLibrary() {
        config.get("share_backups");
    }

    public void newly(String source) {
        ShareBackup b2 = libraries.get(2);
        ShareBackup b3 = libraries.get(3);
        libraries.put(1, b2);
        libraries.put(2, b3);
        libraries.put(3, new ShareBackup(source, 3));
        affect();
    }

    public void affect() {
        Object2ObjectOpenHashMap<String, String> diffs = center.detect();
        if (diffs.size() > 0) {
            Do.letForUp(3, 1, v -> {
                libraries.get(v).affect(diffs);
            });
        }
        center.action();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        JSONObject sharingArray = new JSONObject();
        Do.letForUp(3, 1, v -> {
            sharingArray.put(String.valueOf(v), libraries.get(v).toJSONObject());
        });
        json.put("sharing", sharingArray);
        json.put("center", center.toJSONObject());
        return json;
    }
}
