package eu.kanade.tachiyomi.source.online;

import eu.kanade.tachiyomi.network.NetworkHelper;
import eu.kanade.tachiyomi.source.model.FilterList;
import eu.kanade.tachiyomi.source.model.MangasPage;
import eu.kanade.tachiyomi.source.model.Page;
import eu.kanade.tachiyomi.source.model.SChapter;
import eu.kanade.tachiyomi.source.model.SManga;

import java.util.List;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class HttpSource {
    private final NetworkHelper network = new NetworkHelper();

    protected final NetworkHelper getNetwork() { return network; }
    public final Headers getHeaders() { return headersBuilder().build(); }
    protected Headers.Builder headersBuilder() { return new Headers.Builder(); }

    public abstract String getBaseUrl();
    public abstract long getId();
    public abstract String getName();
    public abstract String getLang();
    public abstract boolean getSupportsLatest();
    public abstract OkHttpClient getClient();
    protected abstract Request popularMangaRequest(int page);
    protected abstract MangasPage popularMangaParse(Response response);
    protected abstract Request latestUpdatesRequest(int page);
    protected abstract MangasPage latestUpdatesParse(Response response);
    protected abstract Request searchMangaRequest(int page, String query, FilterList filters);
    protected abstract MangasPage searchMangaParse(Response response);
    public abstract Request mangaDetailsRequest(SManga manga);
    protected abstract SManga mangaDetailsParse(Response response);
    protected abstract Request chapterListRequest(SManga manga);
    protected abstract List<SChapter> chapterListParse(Response response);
    protected abstract Request pageListRequest(SChapter chapter);
    protected abstract List<Page> pageListParse(Response response);
    protected abstract Request imageRequest(Page page);
    protected abstract String imageUrlParse(Response response);
}
