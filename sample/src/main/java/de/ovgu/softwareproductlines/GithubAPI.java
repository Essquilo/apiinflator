package de.ovgu.softwareproductlines;

import de.ovgu.softwareproductlines.annotation.*;
import de.ovgu.softwareproductlines.annotation.auth.AuthToken;
import de.ovgu.softwareproductlines.annotation.auth.OAuth;
import de.ovgu.softwareproductlines.annotation.response.Jackson;
import de.ovgu.softwareproductlines.annotation.type.GET;
import de.ovgu.softwareproductlines.annotation.type.PATCH;
import de.ovgu.softwareproductlines.annotation.type.PUT;
import io.reactivex.Observable;
import okhttp3.Response;
import org.w3c.dom.Document;

import java.util.List;

@API("www.github.com")
public interface GithubAPI {
    @Url("/users/{user}/repos")
    @GET
    public Observable<Response> rawRepos(@Path("user") String user);

    class SettingsResponse {
        public String name;
        public String email;
    }

    @Url("/users/{user}/repos/settings")
    @PATCH
    @JSON
    @OAuth
    public Observable<SettingsResponse> patchSettings(@Path("user") String user, @AuthToken String token);

    @Url("/users/{user}/repos/add")
    @Jackson
    @Form
    @PUT
    public Observable<Document> addRepo(@Path("user") String user, @Param("number") Integer number, @Param("string") String string);
}