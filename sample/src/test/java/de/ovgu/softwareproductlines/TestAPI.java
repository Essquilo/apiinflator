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

@API("www.github.com")
public class TestAPI {
    @Url("/users/{user}/repos")
    @GET
    public Observable<Response> rawRepos(@Path("user") String user) {
        return null;
    }

    class SettingsResponse {
        public String name;
        public String email;
    }

    @PATCH
    @JSON
    @OAuth
    @Url("/users/{user}/repos/settings")
    public Observable<SettingsResponse> patchSettings(@Path("user") String user, @AuthToken String token) {
        return null;
    }

    @Url("/users/{user}/repos/add")
    @Jackson
    @Form
    @PUT
    public Observable<Document> addRepo(@Path("user") String user, @Param("number") Integer number, @Param("string") String string) {
        return null;
    }
}