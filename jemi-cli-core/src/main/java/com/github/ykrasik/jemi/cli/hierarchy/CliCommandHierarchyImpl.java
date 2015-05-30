/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.jemi.cli.hierarchy;

import com.github.ykrasik.jemi.api.Constants;
import com.github.ykrasik.jemi.cli.CliConstants;
import com.github.ykrasik.jemi.cli.command.CliCommand;
import com.github.ykrasik.jemi.cli.directory.CliDirectory;
import com.github.ykrasik.jemi.cli.exception.ParseError;
import com.github.ykrasik.jemi.cli.exception.ParseException;
import com.github.ykrasik.jemi.core.directory.CommandDirectoryDef;
import com.github.ykrasik.jemi.cli.assist.AutoComplete;
import com.github.ykrasik.jemi.cli.assist.CliValueType;
import com.github.ykrasik.jemi.util.opt.Opt;
import com.github.ykrasik.jemi.util.string.StringUtils;
import com.github.ykrasik.jemi.util.trie.Trie;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Delegate;

import java.util.List;

/**
 * An immutable implementation of a {@link CliCommandHierarchy}.<br>
 * Supports 2 types of commands - local commands which must belong to some {@link CliDirectory}
 * and system commands, which don't belong to any {@link CliDirectory} and are accessible from anywhere, no matter what
 * the current working directory is.
 *
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class CliCommandHierarchyImpl implements CliCommandHierarchy {
    private final CliDirectory root;

    /**
     * Contains system commands that are not associated with any specific directory (stuff like 'cd', 'ls' etc).
     * These commands only come into play in certain situations (the commandLine must not start with a '/'), but other
     * then that they are identical to regular commands. For this purpose, it's convenient to store them in a 'virtual' directory.
     */
    private final CliDirectory systemCommands;

    // TODO: Don't use lombok generated methods.
    @Getter @Setter
    @NonNull private CliDirectory workingDirectory;

    /**
     * Package visible for testing
     */
    CliCommandHierarchyImpl(@NonNull CliDirectory root, @NonNull CliDirectory systemCommands) {
        this.root = root;
        this.systemCommands = systemCommands;
        this.workingDirectory = root;
    }

    @Override
    public CliDirectory parsePathToDirectory(String rawPath) throws ParseException {
        if (rawPath.isEmpty()) {
            throw new ParseException(ParseError.INVALID_DIRECTORY, "Empty path!");
        }

        final boolean startsFromRoot = rawPath.startsWith(Constants.PATH_DELIMITER_STRING);
        if (startsFromRoot && rawPath.length() == 1) {
            return root;
        }

        // Remove leading and trailing '/'.
        // TODO: Make sure this doesn't mask '//' or '///' as an error.
        final String pathToSplit = StringUtils.removeLeadingAndTrailingDelimiter(rawPath, Constants.PATH_DELIMITER_STRING);

        // Split the given path according to delimiter.
        final List<String> splitPath = Constants.splitByPathDelimiter(pathToSplit);

        // Parse all elements as directories.
        CliDirectory currentDirectory = startsFromRoot ? root : workingDirectory;
        for (String directoryName : splitPath) {
            currentDirectory = parseChildDirectory(currentDirectory, directoryName);
        }

        return currentDirectory;
    }

    private CliDirectory parseChildDirectory(CliDirectory currentDirectory, String name) throws ParseException {
        if (name.isEmpty()) {
            throw new ParseException(ParseError.INVALID_DIRECTORY, "Empty directory name! Under: '%s'", currentDirectory.getName());
        }

        if (CliConstants.PATH_THIS.equals(name)) {
            return currentDirectory;
        }

        if (CliConstants.PATH_PARENT.equals(name)) {
            final Opt<CliDirectory> parent = currentDirectory.getParent();
            if (!parent.isPresent()) {
                throw new ParseException(ParseError.INVALID_DIRECTORY, "Directory '%s' doesn't have a parent.", currentDirectory.getName());
            }
            return parent.get();
        }

        final Opt<CliDirectory> childDirectory = currentDirectory.getDirectory(name);
        if (!childDirectory.isPresent()) {
            throw new ParseException(
                ParseError.INVALID_DIRECTORY,
                "Directory '%s' doesn't contain directory: '%s'", currentDirectory.getName(), name
            );
        }
        return childDirectory.get();
    }

    @Override
    public CliCommand parsePathToCommand(String rawPath) throws ParseException {
        // If rawPath does not contain a single delimiter, we can try use it as the command name.
        final int delimiterIndex = rawPath.lastIndexOf(Constants.PATH_DELIMITER_STRING);
        if (delimiterIndex == -1) {
            // rawPath does not contain a delimiter.
            // It could either be a systemCommands command, or a command under the current workingDirectory.
            return getSystemOrWorkingDirectoryCommand(rawPath);
        }

        // rawPath contains a delimiter.
        // Parse the path until the pre-last entry as directories, and let the last directory parse the last entry as a command.
        // So in "path/to/command", parse "path/to" as path to directory "to", and let "to" parse "command".
        final String pathToLastDirectory = rawPath.substring(0, delimiterIndex + 1);
        final CliDirectory lastDirectory = parsePathToDirectory(pathToLastDirectory);

        // FIXME: Why is this check needed? The last directory would just try to parse an empty command and fail.
        // If rawPath ends with the delimiter, it cannot possibly point to a command.
        final String commandName = rawPath.substring(delimiterIndex + 1);
        if (commandName.isEmpty()) {
            throw new ParseException(ParseError.INVALID_COMMAND, "Path doesn't point to a command: %s", rawPath);
        }

        final Opt<CliCommand> command = lastDirectory.getCommand(commandName);
        if (!command.isPresent()) {
            throw new ParseException(
                ParseError.INVALID_COMMAND,
                "Directory '%s' doesn't contain command: '%s'", lastDirectory.getName(), commandName
            );
        }
        return command.get();
    }

    private CliCommand getSystemOrWorkingDirectoryCommand(String name) throws ParseException {
        // If 'name' is a system command, return it.
        final Opt<CliCommand> systemCommand = systemCommands.getCommand(name);
        if (systemCommand.isPresent()) {
            return systemCommand.get();
        }

        // 'name' is not a system command, check if it is a child of the current workingDirectory.
        final Opt<CliCommand> command = workingDirectory.getCommand(name);
        if (!command.isPresent()) {
            throw new ParseException(ParseError.INVALID_COMMAND, "'%s' is not a recognized command!", name);
        }
        return command.get();
    }

    @Override
    public AutoComplete autoCompletePathToDirectory(String rawPath) throws ParseException {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        final int delimiterIndex = rawPath.lastIndexOf(Constants.PATH_DELIMITER_STRING);
        if (delimiterIndex == -1) {
            // rawPath did not contain a delimiter, just autoComplete it from the workingDirectory.
            final Trie<CliValueType> possibilities = workingDirectory.autoCompleteDirectory(rawPath);
            return new AutoComplete(rawPath, possibilities);
        }

        // rawPath contains a delimiter.
        // Parse the path until the pre-last entry as directories, and let the last directory autoComplete the last entry as a directory.
        // So in "path/to/directory", parse "path/to" as path to directory "to", and let "to" autoComplete "directory".
        final String pathToLastDirectory = rawPath.substring(0, delimiterIndex + 1);
        final CliDirectory lastDirectory = parsePathToDirectory(pathToLastDirectory);

        final String directoryPrefix = rawPath.substring(delimiterIndex + 1);
        final Trie<CliValueType> possibilities = lastDirectory.autoCompleteDirectory(directoryPrefix);
        return new AutoComplete(directoryPrefix, possibilities);
    }

    @Override
    public AutoComplete autoCompletePath(String rawPath) throws ParseException {
        // Parse the path until the last delimiter, after which we autoComplete the remaining arg.
        final int delimiterIndex = rawPath.lastIndexOf(Constants.PATH_DELIMITER_STRING);
        if (delimiterIndex == -1) {
            // rawPath did not contain a delimiter.
            // It could be either a system command or an entry from the current workingDirectory.
            final Trie<CliValueType> systemCommandPossibilities = systemCommands.autoCompleteCommand(rawPath);
            final Trie<CliValueType> entryPossibilities = workingDirectory.autoCompleteEntry(rawPath);
            final Trie<CliValueType> possibilities = entryPossibilities.union(systemCommandPossibilities);
            return new AutoComplete(rawPath, possibilities);
        }

        // rawPath contains a delimiter.
        // Parse the path until the pre-last entry as directories, and let the last directory autoComplete the last entry.
        // So in "path/to/entry", parse "path/to" as path to directory "to", and let "to" autoComplete "entry".
        final String pathToLastDirectory = rawPath.substring(0, delimiterIndex + 1);
        final CliDirectory lastDirectory = parsePathToDirectory(pathToLastDirectory);

        final String entryPrefix = rawPath.substring(delimiterIndex + 1);
        final Trie<CliValueType> possibilities = lastDirectory.autoCompleteEntry(entryPrefix);
        return new AutoComplete(entryPrefix, possibilities);
    }

    public static CliCommandHierarchyImpl from(CommandDirectoryDef rootDef) {
        // Create hierarchy with the parameter as the root.
        final CliDirectory root = CliDirectory.fromDef(rootDef);

        // Create system commands 'virtual' directory.
        // System commands need to operate on an already built hierarchy, but... we are exactly in the process of building one.
        // In order to fully build a hierarchy, we must provide a set of system commands.
        // This is a cyclic dependency - resolved through the use of a 'promise' object, which will delegate all calls to the
        // concrete hierarchy, once it's built.
        final CliCommandHierarchyPromise hierarchyPromise = new CliCommandHierarchyPromise();
        final CliDirectory systemCommands = CliSystemCommandFactory.from(hierarchyPromise);

        // Update the 'promise' hierarchy with the concrete implementation.
        final CliCommandHierarchyImpl hierarchy = new CliCommandHierarchyImpl(root, systemCommands);
        hierarchyPromise.setHierarchy(hierarchy);
        return hierarchy;
    }

    /**
     * A {@link CliCommandHierarchy} that promises to <b>eventually</b> contain a concrete implementation of a {@link CliCommandHierarchy}.
     * Required in order to resolve dependency issues between system commands and the immutability of {@link CliCommandHierarchyImpl}.
     * System commands require a concrete {@link CliCommandHierarchy} to operate on, but {@link CliCommandHierarchyImpl} requires
     * all system commands to be available at construction time - cyclic dependency.
     * So this class was born as a compromise.
     */
    private static class CliCommandHierarchyPromise implements CliCommandHierarchy {
        @Delegate
        @Setter
        private CliCommandHierarchy hierarchy;
    }
}