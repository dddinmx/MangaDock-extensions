package eu.kanade.tachiyomi.network;

import okhttp3.OkHttpClient;

/** Host ABI shim used by javac and the standalone integration harness. */
public final class NetworkHelper {
    private final OkHttpClient cloudflareClient = new OkHttpClient();

    public OkHttpClient getCloudflareClient() {
        return cloudflareClient;
    }
}
