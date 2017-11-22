import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;
import okhttp3.Request;

public class OkHttpOAuthConsumer extends AbstractOAuthConsumer {
    public OkHttpOAuthConsumer(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected HttpRequest wrap(Object o) {
        if (!(o instanceof Request)) {
            throw new IllegalArgumentException(
                    "This consumer expects requests of type "
                    + HttpRequest.class.getCanonicalName());
        } else {
            return new OkHttpRequestAdapter((Request)o);
        }
    }
}