import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import io.reactivex.Observable;
import okhttp3.Response;
import org.junit.Test;

/**
 * Created by Ivan Prymak on 11/13/2017.
 */
public class APIInflaterTest {
    @Test
    public void testGet() throws Exception {
        Observable<Response> apiMethod = new MethodCreator()
                .baseUrl("https://jsonplaceholder.typicode.com/posts/1")
                .requestType(RequestType.GET)
                .buildRaw();
        System.out.println(apiMethod.blockingFirst().body().string());
    }

    public static class TestResponse{
        public int userId;
        public int id;
        public String title;
        public String body;

        @Override
        public String toString() {
            return "id = " + userId + ", body = " + body;
        }
    }

    @Test
    public void testGetJson() throws Exception {
        Observable<TestResponse> apiMethod = new MethodCreator()
                .baseUrl("https://jsonplaceholder.typicode.com/posts/1")
                .requestType(RequestType.GET)
                .buildJson(TestResponse.class, new GsonResponseParser(new GsonBuilder().create()));
        System.out.println(apiMethod.blockingFirst());
    }

    @Test
    public void testPostRaw() throws Exception {
        Observable<Response> apiMethod = new MethodCreator()
                .baseUrl("https://jsonplaceholder.typicode.com/posts")
                .requestType(RequestType.POST)
                .parametersEncoding(ParametersEncoding.PLAINJSON)
                .requestParameters("title","foo")
                .requestParameters("body", "bar")
                .requestParameters("userId", "1")
                .buildRaw();
        System.out.println(apiMethod.blockingFirst().body().string());
    }

    @Test
    public void testGetParameters() throws Exception {
        Observable<Response> apiMethod = new MethodCreator()
                .baseUrl("https://jsonplaceholder.typicode.com/comments")
                .requestType(RequestType.GET)
                .requestParameters("postId", "1")
                .buildRaw();
        System.out.println(apiMethod.blockingFirst().body().string());
    }
}
