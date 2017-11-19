import io.reactivex.Observable;
import org.junit.Test;

/**
 * Created by Ivan Prymak on 11/13/2017.
 */
public class APIInflaterTest {
    interface GithubGetRepositories {
        @GET
        @Url("/{user}/repos")
        public Observable<String> repository(@Param String user);
    }
    @Test public void testGet(){
        APIInflater apiInflater = new APIInflater.Builder().baseUrl("www.github.com").build();
        GithubGetRepositories api = apiInflater.inflate(GithubGetRepositories.class);

    }
}
