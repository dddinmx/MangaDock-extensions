#![no_std]

use aidoku::{
    Chapter, ContentRating, DeepLinkHandler, DeepLinkResult, FilterValue, ImageRequestProvider,
    Listing, ListingProvider, Manga, MangaPageResult, Page, PageContent, PageContext, Source,
    Viewer,
    alloc::{
        string::{String, ToString},
        vec,
        vec::Vec,
    },
    helpers::uri::encode_uri_component,
    imports::{
        error::Result,
        html::{Document, Element, Html},
        net::Request,
    },
    prelude::*,
};
use serde_json::{Deserializer, Value};

const SITE: &str = "https://www.kalamhw.com";
const LEGACY: &str = "https://www.kalamanhua.com";
const IMAGES: &str = "https://image.kalaimg.top";
const UA: &str = "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";

struct Kalamanhua;
register_source!(
    Kalamanhua,
    ListingProvider,
    DeepLinkHandler,
    ImageRequestProvider
);

fn assignment<'a>(text: &'a str, name: &str) -> Option<&'a str> {
    ["var ", "let ", "const "].iter().find_map(|prefix| {
        text.match_indices(&format!("{prefix}{name}"))
            .find_map(|(index, matched)| {
                text[index + matched.len()..]
                    .trim_start()
                    .strip_prefix('=')
                    .map(str::trim_start)
            })
    })
}

fn json_assignment<T>(text: &str, name: &str, parse: impl FnOnce(&str) -> Option<T>) -> Option<T> {
    parse(assignment(text, name)?)
}

fn unwrap_html(raw: &str) -> Result<Option<String>> {
    // The site's outer document writes this JSON string into its reader iframe.
    // Parse the literal without running any of the page's JavaScript.
    if !(raw.contains("var cleanHtml") && raw.contains("doc.write(")) {
        return Ok(None);
    }
    let html = json_assignment(raw, "rawHtml", |value| {
        Deserializer::from_str(value)
            .into_iter::<String>()
            .next()?
            .ok()
    });
    html.map(Some)
        .ok_or_else(|| error!("卡拉漫画网页正文格式已变更"))
}

fn request(url: &str, referer: &str) -> Result<Request> {
    Ok(Request::get(url)?
        .header("User-Agent", UA)
        .header("Referer", referer))
}

fn fetch_html(url: &str, referer: &str) -> Result<String> {
    let response = request(url, referer)?.send()?;
    let status = response.status_code();
    if status == 403 {
        bail!("卡拉漫画要求网页访问验证，请用浏览器打开源站后重试");
    }
    if !(200..300).contains(&status) {
        bail!("卡拉漫画请求失败（HTTP {status}）");
    }
    let raw = response.get_string()?;
    if raw.contains("<title>安全验证</title>") {
        bail!("卡拉漫画要求网页访问验证，请用浏览器打开源站后重试");
    }
    Ok(unwrap_html(&raw)?.unwrap_or(raw))
}

fn absolute(base: &str, url: &str) -> Option<String> {
    let url = url.trim();
    if url.starts_with("https://") {
        Some(url.into())
    } else if url.starts_with("//") {
        Some(format!("https:{url}"))
    } else if url.starts_with('/') {
        Some(format!("{base}{url}"))
    } else {
        None
    }
}

fn known_path(url: &str) -> Option<&str> {
    let url = url.trim();
    let path = if url.starts_with('/') && !url.starts_with("//") {
        url
    } else {
        let rest = url
            .split_once("://")
            .filter(|(scheme, _)| matches!(*scheme, "https" | "http"))?
            .1;
        let (host, _) = rest.split_once('/')?;
        if !matches!(
            host,
            "www.kalamhw.com" | "kalamhw.com" | "www.kalamanhua.com" | "kalamanhua.com"
        ) {
            return None;
        }
        &rest[host.len()..]
    };
    Some(path.split(['?', '#']).next().unwrap_or(path))
}

fn comic_path(url: &str) -> Option<&str> {
    let path = known_path(url)?;
    let id = path.strip_prefix("/comic/")?.strip_suffix(".html")?;
    (!id.is_empty() && id.bytes().all(|b| b.is_ascii_digit())).then_some(path)
}

fn chapter_parts(url: &str) -> Option<(&str, &str, u32, u32)> {
    let path = known_path(url)?;
    let mut pieces = path
        .strip_prefix("/chapter/")?
        .strip_suffix(".html")?
        .split('-');
    let id = pieces.next()?;
    let line = pieces.next()?.parse().ok()?;
    let number = pieces.next()?.parse().ok()?;
    (!id.is_empty() && id.bytes().all(|b| b.is_ascii_digit()) && pieces.next().is_none())
        .then_some((path, id, line, number))
}

fn description(element: &Element) -> Option<String> {
    element
        .text()
        .map(|text| text.trim().to_string())
        .filter(|text| !text.is_empty())
}

fn cover(element: &Element) -> Option<String> {
    ["data-original", "data-src", "src"]
        .iter()
        .find_map(|attr| element.attr(attr).and_then(|url| absolute(SITE, &url)))
}

fn manga_list(document: &Document, has_next_page: bool) -> MangaPageResult {
    let mut entries = Vec::new();
    let mut seen = aidoku::HashMap::new();
    if let Some(cards) = document.select("a.stui-vodlist__thumb[href]") {
        for card in cards {
            let Some(href) = card.attr("href") else {
                continue;
            };
            let Some(path) = comic_path(&href) else {
                continue;
            };
            let Some(title) = card.attr("title").filter(|value| !value.trim().is_empty()) else {
                continue;
            };
            if seen.insert(path.to_string(), ()).is_some() {
                continue;
            }
            entries.push(Manga {
                // Retain v2 shelf keys so that existing entries continue to update.
                key: format!("{LEGACY}{path}"),
                url: Some(format!("{SITE}{path}")),
                title,
                cover: cover(&card),
                content_rating: ContentRating::NSFW,
                viewer: Viewer::Webtoon,
                ..Default::default()
            });
        }
    }
    MangaPageResult {
        entries,
        has_next_page,
    }
}

fn read_chapters(document: &Document, manga_id: &str) -> Vec<Chapter> {
    let mut lines: aidoku::HashMap<u32, Vec<Chapter>> = aidoku::HashMap::new();
    if let Some(links) = document.select("[id^=playlist] a[href], .stui-content__playlist a[href]")
    {
        for link in links {
            let Some(href) = link.attr("href") else {
                continue;
            };
            let Some((path, id, line, number)) = chapter_parts(&href) else {
                continue;
            };
            if manga_id != id {
                continue;
            }
            lines.entry(line).or_default().push(Chapter {
                key: format!("{LEGACY}{path}"),
                title: description(&link),
                chapter_number: Some(number as f32),
                url: Some(format!("{SITE}{path}")),
                language: Some("zh".into()),
                ..Default::default()
            });
        }
    }
    for chapters in lines.values_mut() {
        chapters.sort_unstable_by(|a, b| {
            b.chapter_number
                .partial_cmp(&a.chapter_number)
                .unwrap_or(core::cmp::Ordering::Equal)
        });
        chapters.dedup_by(|a, b| a.key == b.key);
    }
    lines
        .into_iter()
        .max_by_key(|(line, chapters)| (chapters.len(), core::cmp::Reverse(*line)))
        .map(|(_, chapters)| chapters)
        .unwrap_or_default()
}

fn player_url(raw: &str) -> Option<String> {
    let player = json_assignment(raw, "player_aaaa", |value| {
        Deserializer::from_str(value)
            .into_iter::<Value>()
            .next()?
            .ok()
    })?;
    let url = player.get("url")?.as_str()?.trim();
    if url.len() >= 32 && url.bytes().all(|byte| byte.is_ascii_hexdigit()) {
        return Some(format!("{IMAGES}/play/min/{url}"));
    }
    let url = absolute(IMAGES, url)?;
    url.starts_with(&format!("{IMAGES}/play/")).then_some(url)
}

fn automatic_player_token(raw: &str) -> Option<&str> {
    // This loader is an automatic redirect emitted by the image player.
    if !raw.contains("window.location.replace(currentUrl.href)") {
        return None;
    }
    let value = assignment(raw, "token")?;
    let quote = value
        .chars()
        .next()
        .filter(|value| matches!(value, '"' | '\''))?;
    let token = value[1..].split_once(quote)?.0;
    (!token.is_empty()
        && token.len() < 512
        && token
            .bytes()
            .all(|b| b.is_ascii_alphanumeric() || matches!(b, b'-' | b'_' | b'.')))
    .then_some(token)
}

fn player_query(url: &str, token: &str) -> String {
    let sep = if url.contains('?') { '&' } else { '?' };
    format!("{url}{sep}cf_verify={token}")
}

impl Source for Kalamanhua {
    fn new() -> Self {
        Self
    }

    fn get_search_manga_list(
        &self,
        query: Option<String>,
        page: i32,
        _filters: Vec<FilterValue>,
    ) -> Result<MangaPageResult> {
        let Some(query) = query
            .as_deref()
            .map(str::trim)
            .filter(|query| !query.is_empty())
        else {
            return self.get_manga_list(Listing::default(), page);
        };
        if let Some(path) = comic_path(query) {
            if page > 1 {
                return Ok(MangaPageResult::default());
            }
            let manga = Manga {
                key: format!("{LEGACY}{path}"),
                ..Default::default()
            };
            return Ok(MangaPageResult {
                entries: vec![self.get_manga_update(manga, true, false)?],
                has_next_page: false,
            });
        }
        let encoded = encode_uri_component(query);
        let path = if page <= 1 {
            format!("/search/wd/{encoded}.html")
        } else {
            format!("/search/page/{page}/wd/{encoded}.html")
        };
        let url = format!("{SITE}{path}");
        let raw = fetch_html(&url, SITE)?;
        let doc = Html::parse_with_url(raw, &url)?;
        let more = doc
            .select(".stui-page__item a[href], .stui-page__all a[href], .pagination a[href]")
            .is_some_and(|links| {
                let next_path = format!("/search/page/{}/wd/", page.saturating_add(1));
                links.into_iter().any(|link| {
                    link.attr("href")
                        .and_then(|href| known_path(&href).map(|path| path.starts_with(&next_path)))
                        .unwrap_or_default()
                })
            });
        Ok(manga_list(&doc, more))
    }

    fn get_manga_update(&self, mut manga: Manga, details: bool, chapters: bool) -> Result<Manga> {
        if !details && !chapters {
            return Ok(manga);
        }
        let path = comic_path(&manga.key).ok_or_else(|| error!("卡拉漫画作品链接无效"))?;
        let url = format!("{SITE}{path}");
        let raw = fetch_html(&url, SITE)?;
        let doc = Html::parse_with_url(raw, &url)?;
        if chapters {
            let id = path.trim_start_matches("/comic/").trim_end_matches(".html");
            let rows = read_chapters(&doc, id);
            if rows.is_empty() {
                bail!("卡拉漫画未返回章节列表");
            }
            manga.chapters = Some(rows);
        }
        if details {
            manga.title = doc
                .select_first("h1.title")
                .and_then(|node| description(&node))
                .ok_or_else(|| error!("卡拉漫画未返回作品标题"))?;
            manga.cover = doc
                .select_first(".stui-content__thumb img")
                .and_then(|node| cover(&node))
                .or(manga.cover);
            manga.description = doc
                .select_first(
                    ".detail-content, .stui-content__desc .detail-sketch, .stui-content__desc",
                )
                .and_then(|node| description(&node))
                .or(manga.description);
            manga.content_rating = ContentRating::NSFW;
            manga.viewer = Viewer::Webtoon;
            manga.url = Some(url);
        }
        Ok(manga)
    }

    fn get_page_list(&self, _manga: Manga, chapter: Chapter) -> Result<Vec<Page>> {
        let (path, _, _, _) =
            chapter_parts(&chapter.key).ok_or_else(|| error!("卡拉漫画章节链接无效"))?;
        let chapter_url = format!("{SITE}{path}");
        let chapter_html = fetch_html(&chapter_url, SITE)?;
        let player = player_url(&chapter_html).ok_or_else(|| error!("卡拉漫画未返回章节播放器"))?;
        let mut reader = fetch_html(&player, &chapter_url)?;
        let token = automatic_player_token(&reader).map(str::to_string);
        if let Some(token) = &token {
            reader = fetch_html(&player_query(&player, token), &chapter_url)?;
        }
        let document = Html::parse_with_url(reader, IMAGES)?;
        let mut pages = Vec::new();
        if let Some(images) = document.select("img.comic-img") {
            for node in images {
                let url = ["data-src", "src"].iter().find_map(|key| {
                    node.attr(key)
                        .and_then(|url| absolute(IMAGES, &url))
                        .filter(|url| url.starts_with(&format!("{IMAGES}/image/")))
                });
                let Some(mut url) = url else { continue };
                if let Some(token) = &token
                    && !url.contains("cf_verify=")
                {
                    url = player_query(&url, token);
                }
                pages.push(Page {
                    content: PageContent::url(url),
                    ..Default::default()
                });
            }
        }
        if pages.is_empty() {
            bail!("卡拉漫画未返回正文图片");
        }
        Ok(pages)
    }
}

impl ListingProvider for Kalamanhua {
    fn get_manga_list(&self, _listing: Listing, page: i32) -> Result<MangaPageResult> {
        if page > 1 {
            return Ok(MangaPageResult::default());
        }
        let raw = fetch_html(SITE, SITE)?;
        Ok(manga_list(&Html::parse_with_url(raw, SITE)?, false))
    }
}

impl DeepLinkHandler for Kalamanhua {
    fn handle_deep_link(&self, url: String) -> Result<Option<DeepLinkResult>> {
        if let Some(path) = comic_path(&url) {
            return Ok(Some(DeepLinkResult::Manga {
                key: format!("{LEGACY}{path}"),
            }));
        }
        Ok(
            chapter_parts(&url).map(|(path, id, _, _)| DeepLinkResult::Chapter {
                manga_key: format!("{LEGACY}/comic/{id}.html"),
                key: format!("{LEGACY}{path}"),
            }),
        )
    }
}

impl ImageRequestProvider for Kalamanhua {
    fn get_image_request(&self, url: String, _context: Option<PageContext>) -> Result<Request> {
        if !url.starts_with("https://") {
            bail!("卡拉漫画图片地址无效");
        }
        request(&url, "https://image.kalaimg.top/")
    }
}
