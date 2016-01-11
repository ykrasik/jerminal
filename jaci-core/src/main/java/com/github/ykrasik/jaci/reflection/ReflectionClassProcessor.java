/******************************************************************************
 * Copyright (C) 2014 Yevgeny Krasik                                          *
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

package com.github.ykrasik.jaci.reflection;

import com.github.ykrasik.jaci.api.CommandOutput;
import com.github.ykrasik.jaci.api.CommandPath;
import com.github.ykrasik.jaci.command.CommandDef;
import com.github.ykrasik.jaci.command.CommandOutputPromise;
import com.github.ykrasik.jaci.path.ParsedPath;
import com.github.ykrasik.jaci.reflection.method.ReflectionMethodProcessor;
import com.github.ykrasik.jaci.util.opt.Opt;

import java.util.*;

/**
 * Processes a class and creates {@link CommandDef}s from qualifying methods.
 *
 * @author Yevgeny Krasik
 */
public class ReflectionClassProcessor {
    /**
     * Will be injected into all processed instances.
     */
    private final CommandOutputPromise outputPromise;

    private final ReflectionMethodProcessor methodProcessor;

    public ReflectionClassProcessor() {
        this(new CommandOutputPromise());
    }

    /**
     * Package-protected for testing.
     */
    ReflectionClassProcessor(CommandOutputPromise outputPromise) {
        this(outputPromise, new ReflectionMethodProcessor(outputPromise));
    }

    /**
     * Package-protected for testing.
     */
    ReflectionClassProcessor(CommandOutputPromise outputPromise, ReflectionMethodProcessor methodProcessor) {
        this.outputPromise = Objects.requireNonNull(outputPromise, "outputPromise");
        this.methodProcessor = Objects.requireNonNull(methodProcessor, "methodProcessor");
    }

    /**
     * Process the object and return a {@link Map} from a {@link ParsedPath} to a {@link List} of {@link CommandDef}s
     * that were defined for that path.
     *
     * @param instance Object to process.
     * @return The {@link CommandDef}s that were extracted out of the object.
     * @throws RuntimeException If any error occurs.
     */
    public Map<ParsedPath, List<CommandDef>> processObject(Object instance) {
        final Class<?> clazz = instance.getClass();

        // Inject our outputPromise into the processed instance.
        // Any commands declared in the instance will reference this outputPromise, which will eventually
        // contain a concrete implementation of a CommandOutput.
        injectOutputPromise(instance, clazz);

        // All method paths will be appended to the class's top level path.
        final ParsedPath topLevelPath = getTopLevelPath(clazz);

        final ClassContext context = new ClassContext(topLevelPath);

        // Create commands from all qualifying methods..
        final ReflectionMethod[] methods = ReflectionUtils.getMethods(clazz);
        for (ReflectionMethod method : methods) {
            processMethod(context, instance, method);
        }

        return context.commandPaths;
    }

    private void injectOutputPromise(Object instance, Class<?> clazz) {
        try {
            final ReflectionField[] fields = ReflectionUtils.getDeclaredFields(clazz);
            for (ReflectionField field : fields) {
                if (field.getType() == CommandOutput.class) {
                    field.setAccessible(true);
                    field.set(instance, outputPromise);

                    // Only inject the first CommandOutput - class shouldn't have more then 1 anyway.
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void processMethod(ClassContext context, Object instance, ReflectionMethod method) {
        // Commands can only be created from qualifying methods.
        final Opt<CommandDef> commandDef = methodProcessor.process(instance, method);
        if (!commandDef.isPresent()) {
            return;
        }

        final ParsedPath commandPath = getCommandPath(method);
        context.addCommandDef(commandPath, commandDef.get());
    }

    private ParsedPath getTopLevelPath(Class<?> clazz) {
        return getPathFromAnnotation(ReflectionUtils.getAnnotation(clazz, CommandPath.class));
    }

    private ParsedPath getCommandPath(ReflectionMethod method) {
        return getPathFromAnnotation(method.getAnnotation(CommandPath.class));
    }

    private ParsedPath getPathFromAnnotation(CommandPath annotation) {
        if (annotation != null) {
            return ParsedPath.toDirectory(annotation.value());
        } else {
            // Annotation isn't present, set the default path to 'root'.
            // Composing any path with 'root' has no effect.
            return ParsedPath.root();
        }
    }

    /**
     * Auxiliary class for collecting {@link CommandDef}s.
     */
    private static class ClassContext {
        private final ParsedPath topLevelPath;

        private final Map<ParsedPath, List<CommandDef>> commandPaths = new HashMap<>();

        private ClassContext(ParsedPath topLevelPath) {
            this.topLevelPath = topLevelPath;
        }

        /**
         * Add a {@link CommandDef} to the given {@link ParsedPath}.
         *
         * @param path Path to add the command to.
         * @param commandDef CommandDef to add.
         */
        public void addCommandDef(ParsedPath path, CommandDef commandDef) {
            // Compose the top level path of the declaring class with the command path.
            final ParsedPath composedPath = topLevelPath.append(path);
            List<CommandDef> commands = commandPaths.get(composedPath);
            if (commands == null) {
                commands = new ArrayList<>();
                commandPaths.put(composedPath, commands);
            }
            commands.add(commandDef);
        }
    }
}
