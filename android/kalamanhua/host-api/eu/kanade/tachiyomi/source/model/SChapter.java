package eu.kanade.tachiyomi.source.model;

public interface SChapter {
    Companion Companion = new Companion();

    String getUrl();
    String getName();
    float getChapter_number();
    void setUrl(String url);
    void setName(String name);
    void setChapter_number(float number);

    final class Companion {
        public SChapter create() {
            return new SChapter() {
                private String url;
                private String name;
                private float number;

                public String getUrl() { return url; }
                public String getName() { return name; }
                public float getChapter_number() { return number; }
                public void setUrl(String value) { url = value; }
                public void setName(String value) { name = value; }
                public void setChapter_number(float value) { number = value; }
            };
        }
    }
}
