package com.rawcod.jerminal;

import com.rawcod.jerminal.command.factory.ControlCommandFactory;
import com.rawcod.jerminal.filesystem.ShellFileSystem;
import com.rawcod.jerminal.filesystem.ShellFileSystemBuilder;
import com.rawcod.jerminal.filesystem.ShellFileSystemPromise;
import com.rawcod.jerminal.filesystem.entry.command.ShellCommand;
import com.rawcod.jerminal.output.OutputHandler;

import java.util.Collection;
import java.util.Set;

/**
 * User: ykrasik
 * Date: 11/08/2014
 * Time: 21:20
 */
public class ShellBuilder {
    private final OutputHandler outputHandler;
    private final ShellFileSystemBuilder fileSystemBuilder;
    private final ShellFileSystemPromise fileSystemPromise;

    private int maxCommandHistory = 20;

    public ShellBuilder(OutputHandler outputHandler) {
        this.outputHandler = outputHandler;
        this.fileSystemBuilder = new ShellFileSystemBuilder();
        this.fileSystemPromise = new ShellFileSystemPromise();

        final Set<ShellCommand> controlCommands = new ControlCommandFactory(fileSystemPromise, outputHandler).createControlCommands();
        fileSystemBuilder.addGlobalCommands(controlCommands);
    }

    public Shell build() {
        final ShellFileSystem fileSystem = fileSystemBuilder.build();
        fileSystemPromise.setFileSystem(fileSystem);
        final ShellCommandHistory commandHistory = new ShellCommandHistory(maxCommandHistory);
        return new Shell(outputHandler, fileSystem, commandHistory);
    }

    public ShellBuilder setMaxCommandHistory(int maxCommandHistory) {
        this.maxCommandHistory = maxCommandHistory;
        return this;
    }

    public ShellBuilder add(ShellCommand... commands) {
        fileSystemBuilder.add(commands);
        return this;
    }

    public ShellBuilder add(String path, ShellCommand... commands) {
        fileSystemBuilder.add(path, commands);
        return this;
    }

    public ShellBuilder addGlobalCommands(ShellCommand... globalCommands) {
        fileSystemBuilder.addGlobalCommands(globalCommands);
        return this;
    }

    public ShellBuilder addGlobalCommands(Collection<ShellCommand> globalCommands) {
        fileSystemBuilder.addGlobalCommands(globalCommands);
        return this;
    }
}