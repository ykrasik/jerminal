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

package com.github.ykrasik.jerminal.internal.command;

import com.github.ykrasik.jerminal.api.command.OutputPrinter;
import com.github.ykrasik.jerminal.api.display.DisplayDriver;

/**
 * An implementation for an {@link OutputPrinter}.
 *
 * @author Yevgeny Krasik
 */
public class OutputPrinterImpl implements OutputPrinter {
    private final DisplayDriver displayDriver;

    public OutputPrinterImpl(DisplayDriver displayDriver) {
        this.displayDriver = displayDriver;
    }

    @Override
    public void println(String text) {
        displayDriver.displayText(text);
    }

    @Override
    public void println(String format, Object... args) {
        println(String.format(format, args));
    }
}
