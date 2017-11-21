import io.reactivex.Observable;
import org.junit.Test;

/**
 * Created by Ivan Prymak on 11/13/2017.
 */
public class APIInflaterTest {
    //    interface GithubGetRepositories {
//        public Observable<String> repository(String user);
//    }
//    @Test public void testGet(){
//        APIInflater apiInflater = new APIInflater();
//        GithubGetRepositories api = apiInflater.inflate(GithubGetRepositories.class);
//
//    }
    class MethodCreator{
        public MethodCreator(){

        }
    }


    @Test
    public void testGet() {
        APIInflater apiInflater = new APIInflater();
        GithubGetRepositories api = apiInflater.inflate(GithubGetRepositories.class);

    }
}
