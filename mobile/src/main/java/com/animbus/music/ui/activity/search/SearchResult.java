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

package com.animbus.music.ui.activity.search;

import java.util.List;

/**
 * Created by Adrian on 12/21/2015.
 */
public class SearchResult {
    public String heading;
    public List<?> results;

    public SearchResult(String heading, List<?> results) {
        this.heading = heading;
        this.results = results;
    }

    public boolean isEmpty() {
        return results.isEmpty();
    }

    public void addIfNotEmpty(List<SearchResult> list) {
        if (!isEmpty()) list.add(this);
    }
}
