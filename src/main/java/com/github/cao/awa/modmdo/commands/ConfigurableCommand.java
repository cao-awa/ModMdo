package com.github.cao.awa.modmdo.commands;

public abstract class ConfigurableCommand<T extends ConfigurableCommand<?>> extends SimpleCommand {
    public abstract T init();
    public abstract T register();
}
