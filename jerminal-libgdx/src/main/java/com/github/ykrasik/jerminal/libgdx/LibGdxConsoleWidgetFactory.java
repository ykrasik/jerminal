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

package com.github.ykrasik.jerminal.libgdx;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Creates all the libGdx widgets that will be display on screen.<br>
 * This is how the console's appearance can be customized.
 *
 * @author Yevgeny Krasik
 */
public interface LibGdxConsoleWidgetFactory {
    /**
     * Create a terminal text line.
     */
    Label createBufferEntryLabel(String text);

    /**
     * Create the terminal background.
     */
    Drawable createTerminalBufferBackground();

    /**
     * Create the "bottom row" background.<br>
     * The bottom row contains the current path, command line and close button.
     */
    Drawable createConsoleBottomRowBackground();

    /**
     * Create the "current path" label background.
     */
    Drawable createCurrentPathLabelBackground();

    /**
     * Create a "current path" label from the given text.
     */
    Label createCurrentPathLabel(String currentPath);

    /**
     * Create the input text field.
     */
    TextField createInputTextField(String initialText);

    /**
     * Create the close button.
     */
    Button createCloseButton();
}