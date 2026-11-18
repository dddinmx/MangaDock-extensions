package eu.kanade.tachiyomi.source.model;

import java.util.List;

public final class MangasPage {
    private final List<SManga> mangas;
    private final boolean hasNextPage;

    public MangasPage(List<SManga> mangas, boolean hasNextPage) {
        this.mangas = mangas;
        this.hasNextPage = hasNextPage;
    }

    public List<SManga> getMangas() { return mangas; }
    public boolean getHasNextPage() { return hasNextPage; }
}
