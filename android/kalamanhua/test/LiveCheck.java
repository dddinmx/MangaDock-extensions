package eu.kanade.tachiyomi.extension.zh.mangadockkalamanhua;

import eu.kanade.tachiyomi.source.model.FilterList;
import eu.kanade.tachiyomi.source.model.MangasPage;
import eu.kanade.tachiyomi.source.model.Page;
import eu.kanade.tachiyomi.source.model.SChapter;
import eu.kanade.tachiyomi.source.model.SManga;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import okhttp3.Request;
import okhttp3.Response;

public final class LiveCheck {
    private static final Kalamanhua source = new Kalamanhua();

    private static Response get(Request request) throws Exception {
        return source.getClient().newCall(request).execute();
    }

    private static MangasPage search(String keyword, int page) throws Exception {
        try (Response response = get(source.searchMangaRequest(page, keyword, new FilterList()))) {
            return source.searchMangaParse(response);
        }
    }

    private static void require(boolean condition, String error) {
        if (!condition) {
            throw new AssertionError(error);
        }
    }

    public static void main(String[] args) throws Exception {
        Path target = Path.of(args[0]);
        Files.createDirectories(target);
        require(source.getId() == 8821044750857838761L, "Source ID changed");
        MangasPage page1 = search("魔力", 1);
        MangasPage page2 = search("魔力", 2);
        require(page1.getHasNextPage(), "First search page reports no next page");
        Set<String> keys = new HashSet<>();
        page1.getMangas().forEach(manga -> keys.add(manga.getUrl()));
        require(!page2.getMangas().isEmpty() && page2.getMangas().stream()
            .anyMatch(manga -> !keys.contains(manga.getUrl())), "Second search page repeats results");
        System.out.printf("Search pagination passed: %d and %d results%n", page1.getMangas().size(), page2.getMangas().size());
        try (Response response = get(source.popularMangaRequest(1))) {
            MangasPage popular = source.popularMangaParse(response);
            require(!popular.getMangas().isEmpty(), "Popular listing empty");
            System.out.printf("Popular listing: %d results%n", popular.getMangas().size());
        }
        String[] queries = {"国运之战", "真公主归来"};
        for (int i = 0; i < queries.length; i++) {
            String query = queries[i];
            SManga manga = search(query, 1).getMangas().stream().filter(x -> x.getTitle().startsWith(query))
                .findFirst().orElseThrow(() -> new AssertionError("No matching result: " + query));
            require(manga.getUrl().startsWith("https://www.kalamanhua.com/comic/"), "Legacy shelf key changed");
            SManga oldLink = search(manga.getUrl(), 1).getMangas().get(0);
            require(manga.getUrl().equals(oldLink.getUrl()), "Legacy link did not resolve to same ID");
            try (Response response = get(source.mangaDetailsRequest(manga))) {
                SManga details = source.mangaDetailsParse(response);
                require(details.getTitle().startsWith(query) && details.getThumbnail_url() != null
                    && details.getInitialized(), "Details incomplete");
            }
            List<SChapter> chapters;
            try (Response response = get(source.chapterListRequest(manga))) {
                chapters = source.chapterListParse(response);
            }
            require(chapters.size() > 40, "Too few chapters");
            SChapter chapter = chapters.get(chapters.size() / 2);
            List<Page> pages;
            try (Response response = get(source.pageListRequest(chapter))) {
                pages = source.pageListParse(response);
            }
            require(pages.size() > 3, "No reader pages");
            for (int index : new int[]{0, pages.size() / 2, pages.size() - 1}) {
                Page page = pages.get(index);
                require(page.getIndex() == index, "Page order changed");
                try (Response response = get(source.imageRequest(page))) {
                    require(response.isSuccessful() && response.body() != null, "Image HTTP " + response.code());
                    Files.write(target.resolve("book" + i + "-page" + index + ".img"), response.body().bytes());
                }
            }
            System.out.printf("%s: %d chapters, %s has %d pages, 3 sampled image bodies downloaded%n",
                manga.getTitle(), chapters.size(), chapter.getName(), pages.size());
        }
        source.getClient().connectionPool().evictAll();
        source.getClient().dispatcher().executorService().shutdown();
    }
}
