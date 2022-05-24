package com.github.cao.awa.modmdo.commands;

public abstract class ConfigurableCommand<T extends ConfigurableCommand<?>> extends SimpleCommand {
    abstract T init();
    protected abstract T register();
}
