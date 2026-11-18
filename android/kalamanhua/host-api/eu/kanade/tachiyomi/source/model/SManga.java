package eu.kanade.tachiyomi.source.model;

public interface SManga {
    Companion Companion = new Companion();

    String getUrl();
    String getTitle();
    String getThumbnail_url();
    boolean getInitialized();
    void setUrl(String url);
    void setTitle(String title);
    void setThumbnail_url(String url);
    void setDescription(String description);
    void setInitialized(boolean initialized);

    final class Companion {
        public SManga create() {
            return new SManga() {
                private String url;
                private String title;
                private String thumbnail;
                private boolean initialized;

                public String getUrl() { return url; }
                public String getTitle() { return title; }
                public String getThumbnail_url() { return thumbnail; }
                public boolean getInitialized() { return initialized; }
                public void setUrl(String value) { url = value; }
                public void setTitle(String value) { title = value; }
                public void setThumbnail_url(String value) { thumbnail = value; }
                public void setDescription(String value) {}
                public void setInitialized(boolean value) { initialized = value; }
            };
        }
    }
}
