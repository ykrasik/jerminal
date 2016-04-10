/******************************************************************************
 * Copyright (c) 2016 Yevgeny Krasik.                                         *
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

package com.github.ykrasik.jaci.cli.javafx.gui;

import com.github.ykrasik.jaci.cli.directory.CliDirectory;
import com.github.ykrasik.jaci.cli.gui.CliGui;
import javafx.scene.control.Label;

import java.util.Objects;

/**
 * A JavaFx implementation of a {@link CliGui}.
 *
 * @author Yevgeny Krasik
 */
public class JavaFxCliGui implements CliGui {
    private final Label workingDirectory;

    public JavaFxCliGui(Label workingDirectory) {
        this.workingDirectory = Objects.requireNonNull(workingDirectory);
    }

    @Override
    public void setWorkingDirectory(CliDirectory workingDirectory) {
        this.workingDirectory.setText(workingDirectory.toPath());
    }
}