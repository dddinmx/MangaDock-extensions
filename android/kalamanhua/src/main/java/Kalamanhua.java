package eu.kanade.tachiyomi.extension.zh.mangadockkalamanhua;

import eu.kanade.tachiyomi.source.model.FilterList;
import eu.kanade.tachiyomi.source.model.MangasPage;
import eu.kanade.tachiyomi.source.model.Page;
import eu.kanade.tachiyomi.source.model.SChapter;
import eu.kanade.tachiyomi.source.model.SManga;
import eu.kanade.tachiyomi.source.online.HttpSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class Kalamanhua extends HttpSource {
    private static final String SITE = "https://www.kalamhw.com";
    private static final String LEGACY = "https://www.kalamanhua.com";
    private static final String IMAGES = "https://image.kalaimg.top";
    private static final String UA = "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";
    private static final Pattern COMIC = Pattern.compile("/comic/(\\d+)\\.html");
    private static final Pattern CHAPTER = Pattern.compile("/chapter/(\\d+)-(\\d+)-(\\d+)\\.html");
    private static final Pattern RAW_HTML = assignment("rawHtml");
    private static final Pattern PLAYER = assignment("player_aaaa");
    private static final Pattern LOADER_TOKEN = Pattern.compile(
            "\\b(?:var|let|const)\\s+token\\s*=\\s*(['\"])([A-Za-z0-9._-]{1,512})\\1");

    @Override public String getBaseUrl() { return SITE; }
    @Override public long getId() { return 8821044750857838761L; }
    @Override public String getName() { return "卡拉漫画"; }
    @Override public String getLang() { return "zh"; }
    @Override public boolean getSupportsLatest() { return false; }
    @Override public OkHttpClient getClient() { return getNetwork().getCloudflareClient(); }

    @Override
    protected Headers.Builder headersBuilder() {
        return super.headersBuilder().set("User-Agent", UA).set("Referer", SITE + "/");
    }

    @Override
    protected Request popularMangaRequest(int page) {
        return request(SITE + "/", SITE + "/");
    }

    @Override
    protected Request latestUpdatesRequest(int page) {
        return popularMangaRequest(page);
    }

    @Override
    protected Request searchMangaRequest(int page, String query, FilterList filters) {
        String keyword = query.trim();
        if (keyword.isEmpty()) {
            return popularMangaRequest(page);
        }
        String path = sourcePath(keyword);
        if (path != null && COMIC.matcher(path).matches()) {
            return request(SITE + path, SITE + "/");
        }
        HttpUrl.Builder url = HttpUrl.get(SITE).newBuilder().addPathSegment("search");
        if (page > 1) {
            url.addPathSegment("page").addPathSegment(Integer.toString(page));
        }
        url.addPathSegment("wd").addPathSegment(keyword + ".html");
        return request(url.build().toString(), SITE + "/");
    }

    @Override
    protected MangasPage popularMangaParse(Response response) {
        return listing(document(response), 0);
    }

    @Override
    protected MangasPage latestUpdatesParse(Response response) {
        return popularMangaParse(response);
    }

    @Override
    protected MangasPage searchMangaParse(Response response) {
        HttpUrl url = response.request().url();
        Document doc = document(response);
        if (COMIC.matcher(url.encodedPath()).matches()) {
            return new MangasPage(Collections.singletonList(details(doc)), false);
        }
        List<String> segments = url.pathSegments();
        int page = segments.size() > 2 && "page".equals(segments.get(1))
                ? Integer.parseInt(segments.get(2)) : 1;
        return listing(doc, page);
    }

    @Override
    public Request mangaDetailsRequest(SManga manga) {
        return sourceRequest(manga.getUrl(), COMIC);
    }

    @Override
    protected Request chapterListRequest(SManga manga) {
        return mangaDetailsRequest(manga);
    }

    @Override
    protected Request pageListRequest(SChapter chapter) {
        return sourceRequest(chapter.getUrl(), CHAPTER);
    }

    @Override
    protected SManga mangaDetailsParse(Response response) {
        SManga manga = details(document(response));
        manga.setInitialized(true);
        return manga;
    }

    @Override
    protected List<SChapter> chapterListParse(Response response) {
        Matcher comic = COMIC.matcher(response.request().url().encodedPath());
        if (!comic.matches()) {
            throw new IllegalArgumentException("卡拉漫画作品链接无效");
        }
        Document doc = document(response);
        Map<Integer, TreeMap<Integer, SChapter>> routes = new TreeMap<>();
        for (Element link : doc.select("[id^=playlist] a[href], .stui-content__playlist a[href]")) {
            String path = sourcePath(link.attr("abs:href"));
            if (path == null) {
                continue;
            }
            Matcher match = CHAPTER.matcher(path);
            if (!match.matches() || !comic.group(1).equals(match.group(1))) {
                continue;
            }
            int route = Integer.parseInt(match.group(2));
            int number = Integer.parseInt(match.group(3));
            TreeMap<Integer, SChapter> chapters = routes.get(route);
            if (chapters == null) {
                chapters = new TreeMap<>(Collections.reverseOrder());
                routes.put(route, chapters);
            }
            if (!chapters.containsKey(number)) {
                SChapter chapter = SChapter.Companion.create();
                chapter.setUrl(LEGACY + path);
                chapter.setName(link.text().isEmpty() ? "第" + number + "话" : link.text());
                chapter.setChapter_number(number);
                chapters.put(number, chapter);
            }
        }
        List<SChapter> best = Collections.emptyList();
        for (TreeMap<Integer, SChapter> route : routes.values()) {
            if (route.size() > best.size()) {
                best = new ArrayList<>(route.values());
            }
        }
        if (best.isEmpty()) {
            throw new IllegalStateException("卡拉漫画未返回章节列表");
        }
        return best;
    }

    @Override
    protected List<Page> pageListParse(Response response) {
        String chapterUrl = response.request().url().toString();
        String player = playerUrl(body(response));
        String html;
        try {
            html = readPlayer(player, chapterUrl);
            Matcher loaderToken = LOADER_TOKEN.matcher(html);
            String token = html.contains("window.location.replace(currentUrl.href)") && loaderToken.find()
                    ? loaderToken.group(2) : null;
            if (token != null) {
                html = readPlayer(withToken(HttpUrl.get(player), token).toString(), chapterUrl);
            }
            List<Page> pages = new ArrayList<>();
            for (Element image : Jsoup.parse(html, player).select("img.comic-img")) {
                HttpUrl url = imageUrl(image);
                if (url != null) {
                    pages.add(new Page(pages.size(), player, withToken(url, token).toString(), null));
                }
            }
            if (pages.isEmpty()) {
                throw new IllegalStateException("卡拉漫画未返回正文图片");
            }
            return pages;
        } catch (IOException e) {
            throw new IllegalStateException("卡拉漫画图床连接失败", e);
        }
    }

    @Override
    protected Request imageRequest(Page page) {
        return request(page.getImageUrl(), IMAGES + "/");
    }

    @Override
    protected String imageUrlParse(Response response) {
        throw new UnsupportedOperationException("图片直链已在章节页中解析");
    }

    private String readPlayer(String url, String referer) throws IOException {
        try (Response response = getClient().newCall(request(url, referer)).execute()) {
            return body(response);
        }
    }

    private Request sourceRequest(String value, Pattern type) {
        String path = sourcePath(value);
        if (path == null || !type.matcher(path).matches()) {
            throw new IllegalArgumentException("卡拉漫画链接无效");
        }
        return request(SITE + path, SITE + "/");
    }

    private Request request(String url, String referer) {
        return new Request.Builder().url(url).headers(getHeaders()).header("Referer", referer).build();
    }

    private static String sourcePath(String value) {
        if (value == null) {
            return null;
        }
        HttpUrl url = HttpUrl.parse(value.startsWith("/") && !value.startsWith("//") ? SITE + value : value);
        if (url == null) {
            return null;
        }
        switch (url.host()) {
            case "www.kalamhw.com":
            case "kalamhw.com":
            case "www.kalamanhua.com":
            case "kalamanhua.com":
                return url.encodedPath();
            default:
                return null;
        }
    }

    private static MangasPage listing(Document doc, int page) {
        Map<String, SManga> found = new LinkedHashMap<>();
        for (Element card : doc.select("a.stui-vodlist__thumb[href]")) {
            String path = sourcePath(card.attr("abs:href"));
            String title = card.attr("title").trim();
            if (path != null && COMIC.matcher(path).matches() && !title.isEmpty() && !found.containsKey(path)) {
                found.put(path, manga(path, title, cover(card)));
            }
        }
        boolean more = false;
        if (page > 0 && page < Integer.MAX_VALUE) {
            String next = "/search/page/" + (page + 1) + "/wd/";
            for (Element link : doc.select(".stui-page__item a[href], .stui-page__all a[href]")) {
                String path = sourcePath(link.attr("abs:href"));
                if (path != null && path.startsWith(next)) {
                    more = true;
                    break;
                }
            }
        }
        return new MangasPage(new ArrayList<>(found.values()), more);
    }

    private static SManga details(Document doc) {
        String path = sourcePath(doc.location());
        Element title = doc.selectFirst("h1.title");
        if (path == null || !COMIC.matcher(path).matches() || title == null || title.text().isEmpty()) {
            throw new IllegalStateException("卡拉漫画未返回作品详情");
        }
        SManga manga = manga(path, title.text(), cover(doc.selectFirst(".stui-content__thumb img")));
        Element description = doc.selectFirst(".detail-content, .stui-content__desc .detail-sketch, .stui-content__desc");
        if (description != null) {
            manga.setDescription(description.text());
        }
        return manga;
    }

    private static SManga manga(String path, String title, String cover) {
        SManga manga = SManga.Companion.create();
        // The host stores these URLs in the library. Migrate requests, not stored keys.
        manga.setUrl(LEGACY + path);
        manga.setTitle(title);
        manga.setThumbnail_url(cover);
        return manga;
    }

    private static String cover(Element element) {
        if (element == null) {
            return null;
        }
        for (String attribute : new String[]{"data-original", "data-src", "src"}) {
            String url = element.attr("abs:" + attribute).trim();
            if (url.startsWith("https://")) {
                return url;
            }
        }
        return null;
    }

    private static HttpUrl imageUrl(Element image) {
        for (String attribute : new String[]{"data-src", "src"}) {
            HttpUrl url = HttpUrl.parse(image.attr("abs:" + attribute).trim());
            if (url != null && url.isHttps() && "image.kalaimg.top".equals(url.host())
                    && url.encodedPath().startsWith("/image/")) {
                return url.newBuilder().fragment(null).build();
            }
        }
        return null;
    }

    private static HttpUrl withToken(HttpUrl url, String token) {
        return token == null || url.queryParameter("cf_verify") != null
                ? url : url.newBuilder().addQueryParameter("cf_verify", token).build();
    }

    private static String playerUrl(String html) {
        Object value = assignedValue(html, PLAYER);
        if (value instanceof JSONObject) {
            String address = ((JSONObject) value).optString("url").trim();
            if (address.matches("[0-9a-fA-F]{32,}")) {
                return IMAGES + "/play/min/" + address;
            }
            HttpUrl url = HttpUrl.get(IMAGES + "/").resolve(address);
            if (url != null && url.isHttps() && "image.kalaimg.top".equals(url.host())
                    && url.encodedPath().startsWith("/play/")) {
                return url.toString();
            }
        }
        throw new IllegalStateException("卡拉漫画未返回章节播放器");
    }

    private static Document document(Response response) {
        return Jsoup.parse(body(response), response.request().url().toString());
    }

    private static String body(Response response) {
        try (ResponseBody body = response.body()) {
            if (response.code() == 403) {
                throw new IllegalStateException("请在 WebView 打开卡拉漫画并完成访问验证");
            }
            if (!response.isSuccessful() || body == null) {
                throw new IllegalStateException("卡拉漫画请求失败（HTTP " + response.code() + "）");
            }
            String html = body.string();
            if (html.contains("<title>安全验证</title>")) {
                throw new IllegalStateException("请在 WebView 打开卡拉漫画并完成访问验证");
            }
            if (html.contains("var cleanHtml") && html.contains("doc.write(")) {
                // The outer document writes its HTML string into an iframe.
                // Parse that literal without evaluating the page's JavaScript.
                Object value = assignedValue(html, RAW_HTML);
                if (!(value instanceof String)) {
                    throw new IllegalStateException("卡拉漫画网页正文格式已变更");
                }
                return (String) value;
            }
            return html;
        } catch (IOException e) {
            throw new IllegalStateException("卡拉漫画读取网页失败", e);
        }
    }

    private static Pattern assignment(String name) {
        return Pattern.compile("\\b(?:var|let|const)\\s+" + name + "\\s*=\\s*");
    }

    private static Object assignedValue(String html, Pattern variable) {
        Matcher match = variable.matcher(html);
        if (!match.find()) {
            return null;
        }
        try {
            return new JSONTokener(html.substring(match.end())).nextValue();
        } catch (JSONException e) {
            throw new IllegalStateException("卡拉漫画网页数据格式已变更", e);
        }
    }
}
