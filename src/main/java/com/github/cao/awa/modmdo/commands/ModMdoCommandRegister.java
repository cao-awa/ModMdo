package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.mixins.command.tree.*;
import com.github.cao.awa.modmdo.module.*;
import com.github.zhuaidadaya.rikaishinikui.handler.conductor.string.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.brigadier.tree.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.server.*;
import net.minecraft.server.command.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public final class ModMdoCommandRegister {
    private final MinecraftServer server;
    private final Map<String, ModMdoModule<?>> commands = new Object2ObjectOpenHashMap<>();
    private final Map<String, SimpleCommand> modMdoCommands = new Object2ObjectOpenHashMap<>();

    public ModMdoCommandRegister(MinecraftServer server) {
        this.server = server;
    }


    public MinecraftServer getServer() {
        return this.server;
    }

    public void register(SimpleCommand node, ModMdoModule<?> module) {
        if (! node.isLoaded()) {
            node.markLoad();
            TRACKER.info("Register for command: " + node.builder().getName());
            getRoot().addChild(node.builder());
            this.commands.put(node.path(), module);
            this.modMdoCommands.put(node.path(), node);
            update();
        }
    }

    public void update() {
        CommandManager commandManager = server.getCommandManager();

        EntrustExecution.notNull(server.getPlayerManager(), playerManager -> EntrustExecution.tryFor(playerManager.getPlayerList(), commandManager::sendCommandTree));
    }

    public CommandNode<ServerCommandSource> getRoot() {
        return this.server.getCommandManager().getDispatcher().getRoot();
    }

    public void register(String level, SimpleCommand command, ModMdoModule<?> module) {
        if (! command.isLoaded()) {
            command.markLoad();
            TRACKER.info("Register for command child: " + level + command.builder().getName());
            StringTokenizerConductor tokenizer = new StringTokenizerConductor(new StringTokenizer(level, "/"));
            CommandNode<ServerCommandSource> base = getRoot();
            Collection<String> least = new ObjectArrayList<>();
            for (String n : tokenizer) {
                CommandNode<ServerCommandSource> child = base.getChild(n);
                if (child == null) {
                    least.add(n);
                    continue;
                }
                base = child;
            }
            if (least.size() > 0) {
                for (String s : least) {
                    base.addChild(CommandManager.literal(s).build());
                    base = base.getChild(s);
                }
            }
            base.addChild(command.builder());
            this.commands.put(command.path(), module);
            this.modMdoCommands.put(command.path(), command);
            update();
        }
    }

    public void unregister(SimpleCommand command, ModMdoModule<?> module) {
        List<String> unload = new ArrayList<>();

        if (module instanceof ModMdoCommandModule<?> commandModule) {
            unload.addAll(commandModule.unload(command.path()));
        }
        if (unload.size() < 1) {
            unload.add(command.path());
        }
        unload.forEach(path -> {
            if (commands.get(path) == module) {
                if (module instanceof ModMdoCommandModule<?> commandModule) {
                    unload(commandModule.getCommand(path).level());
                }
                modMdoCommands.get(command.path()).markUnload();
            }
        });
        update();
    }

    public void unload(String level) {
        System.out.println(level);

        String name;

        StringTokenizerConductor tokenizer = new StringTokenizerConductor(new StringTokenizer(level, "/"));
        CommandNode<ServerCommandSource> base = getRoot();
        CommandNode<ServerCommandSource> last = getRoot();
        for (Iterator<String> iterator = tokenizer.iterator(); ; ) {
            if (!iterator.hasNext()) {
                name = base.getName();
                base = last;
                break;
            }
            last = base;
            base = base.getChild(iterator.next());
            if (base == null) {
                return;
            }
        }

        ((CommandNodeMixin<?>) base).getChildren().remove(name);
        ((CommandNodeMixin<?>) base).getLiterals().remove(name);
        ((CommandNodeMixin<?>) base).getArguments().remove(name);
    }

    public void unregister(String level, SimpleCommand command, ModMdoModule<?> module) {
        List<String> unload = new ArrayList<>();

        if (module instanceof ModMdoCommandModule<?> commandModule) {
            unload.addAll(commandModule.unload(command.level()));
        }
        if (unload.size() < 1) {
            unload.add(command.level());
        }
        unload.forEach(path -> {
            if (commands.get(path) == module) {
                if (module instanceof ModMdoCommandModule<?>) {
                    unload(command.level());
                }

                modMdoCommands.get(level).markUnload();
            }
        });
        update();
    }

    public MinecraftServer server() {
        return server;
    }

    public Map<String, ModMdoModule<?>> getCommands() {
        return commands;
    }

    public Map<String, SimpleCommand> getModMdoCommands() {
        return modMdoCommands;
    }

    public SimpleCommand getCommand(String name) {
        return modMdoCommands.get(name);
    }
}
