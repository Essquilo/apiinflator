package de.ovgu.softwareproductlines;

import de.ovgu.softwareproductlines.annotation.*;
import de.ovgu.softwareproductlines.annotation.auth.AuthToken;
import de.ovgu.softwareproductlines.annotation.auth.OAuth;
import de.ovgu.softwareproductlines.annotation.json.JacksonAdapterFactory;
import de.ovgu.softwareproductlines.annotation.json.ParseWith;
import de.ovgu.softwareproductlines.annotation.params.FormBody;
import de.ovgu.softwareproductlines.annotation.params.JSONBody;
import de.ovgu.softwareproductlines.annotation.params.Param;
import de.ovgu.softwareproductlines.annotation.params.Path;
import de.ovgu.softwareproductlines.annotation.type.GET;
import de.ovgu.softwareproductlines.annotation.type.PATCH;
import de.ovgu.softwareproductlines.annotation.type.PUT;
import io.reactivex.Observable;
import okhttp3.Response;
import org.w3c.dom.Document;

@API("www.github.com")
@ParseWith(PrettyJacksonAdapterFactory.class)
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
    @JSONBody
    @OAuth
    public Observable<SettingsResponse> patchSettings(@Path("user") String user, @AuthToken String token);

    @Url("/users/{user}/repos/add")
    @FormBody
    @PUT
    public Observable<Document> addRepo(@Path("user") String user, @Param("number") Integer number, @Param("string") String string);
}