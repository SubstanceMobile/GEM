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
