/*
 * Copyright (C) 2014 Yevgeny Krasik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrasik.jerminal.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this method returns a pre-made {@link com.github.ykrasik.jerminal.api.filesystem.command.Command}.<br>
 * This is meant to be used when a command cannot be constructed through the annotation-based API due to it's
 * limitations and has to be constructed through the programmatic API.<br>
 * <br>
 * Annotated methods must be no-args and return a {@link com.github.ykrasik.jerminal.api.filesystem.command.Command}.<br>
 * <b>There is very little reason to use this annotation and it exists "just in case".</b><br>
 *
 * @author Yevgeny Krasik
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandFactory {

}
