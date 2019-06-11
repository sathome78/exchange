package me.exrates.model.dto.news;

public enum ResourceEnum {

    AMB_CRYPTO(1, "https://ambcrypto.com/category/news/feed/"),
    COIN_CODEX(2, "https://coincodex.com/rss"),
    PORTAL_DOBITCOIN(3, "https://portaldobitcoin.com/feed"),
    FEEDBURNER(4, "https://feeds.feedburner.com/coinspeaker"),
    BITCOINERX(5, "https://bitcoinerx.com/feed/");
    
    private int id;
    private String ur;

    ResourceEnum(int id, String ur) {
        this.id = id;
        this.ur = ur;
    }

    public int getId() {
        return id;
    }

    public String getUr() {
        return ur;
    }
}
