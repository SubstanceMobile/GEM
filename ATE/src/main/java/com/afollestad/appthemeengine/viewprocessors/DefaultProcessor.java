/*
 * Copyright 2016 Substance Mobile
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

package com.afollestad.appthemeengine.viewprocessors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.tagprocessors.TagProcessor;

/**
 * @author Aidan Follestad (afollestad)
 */
public class DefaultProcessor implements ViewProcessor<View, Void> {

    @Override
    public void process(@NonNull Context context, @Nullable String key, @Nullable View view, @Nullable Void extra) {
        if (view == null || view.getTag() == null || !(view.getTag() instanceof String))
            return;
        final String tag = (String) view.getTag();
        if (tag.contains(",")) {
            final String[] splitTag = tag.split(",");
            for (String part : splitTag)
                processTagPart(context, key, view, part);
        } else {
            processTagPart(context, key, view, tag);
        }
    }

    private void processTagPart(@NonNull Context context, @Nullable String key, @NonNull View view, @NonNull String part) {
        final int pipe = part.indexOf('|');
        if (pipe == -1) return;
        final String prefix = part.substring(0, pipe);
        final String suffix = part.substring(pipe + 1);
        final TagProcessor processor = ATE.getTagProcessor(prefix);
        if (processor != null) {
            if (!processor.isTypeSupported(view)) {
                throw new IllegalStateException(String.format("A view of type %s cannot use %s tags.",
                        view.getClass().getName(), prefix));
            }
            try {
                processor.process(context, key, view, suffix);
            } catch (Throwable t) {
                throw new RuntimeException(String.format("Failed to run %s: %s", processor.getClass().getName(), t.getMessage()), t);
            }
        } else {
            throw new IllegalStateException("No ATE tag processors found by prefix " + prefix);
        }
    }
}