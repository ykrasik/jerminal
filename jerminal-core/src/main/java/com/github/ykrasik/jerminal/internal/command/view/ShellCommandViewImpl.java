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

package com.github.ykrasik.jerminal.internal.command.view;

import com.github.ykrasik.jerminal.api.command.parameter.view.ShellCommandParamView;
import com.github.ykrasik.jerminal.api.command.view.ShellCommandView;
import com.github.ykrasik.jerminal.internal.AbstractDescribable;

import java.util.Collections;
import java.util.List;

/**
 * An implementation for a {@link ShellCommandView}.
 *
 * @author Yevgeny Krasik
 */
public class ShellCommandViewImpl extends AbstractDescribable implements ShellCommandView {
    private final List<ShellCommandParamView> params;

    public ShellCommandViewImpl(String name, String description, List<ShellCommandParamView> params) {
        super(name, description);
        this.params = Collections.unmodifiableList(params);
    }

    @Override
    public List<ShellCommandParamView> getParams() {
        return params;
    }
}