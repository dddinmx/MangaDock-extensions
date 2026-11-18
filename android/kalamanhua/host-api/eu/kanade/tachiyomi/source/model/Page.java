package eu.kanade.tachiyomi.source.model;

import android.net.Uri;

public final class Page {
    private final int index;
    private final String imageUrl;

    public Page(int index, String url, String imageUrl, Uri uri) {
        this.index = index;
        this.imageUrl = imageUrl;
    }

    public int getIndex() { return index; }
    public String getImageUrl() { return imageUrl; }
}
