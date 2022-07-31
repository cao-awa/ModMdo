package com.github.cao.awa.modmdo.mixins.command.tree;

import com.mojang.brigadier.tree.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

import java.util.*;

@Mixin(CommandNode.class)
public interface CommandNodeMixin<S> {
    @Accessor
    Map<String, CommandNode<S>> getChildren();
    @Accessor
    Map<String, LiteralCommandNode<S>> getLiterals();
    @Accessor
    Map<String, ArgumentCommandNode<S, ?>> getArguments();
}
