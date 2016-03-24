/*
 * Copyright (C) 2016 Substance Mobile
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses/.
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
