package de.ovgu.softwareproductlines.inflater;

import de.ovgu.softwareproductlines.annotation.API;
import de.ovgu.softwareproductlines.annotation.Action;
import de.ovgu.softwareproductlines.annotation.Url;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class APIAspect {

    private OkHttpClient client = new OkHttpClient();

    @Pointcut("@annotation(url)")
    public void urlAnnotated(Url url) {
    }

    @Pointcut("execution(io.reactivex.Observable<?> *(..))")
    public void observableMethod() {
    }

    @Pointcut("observableMethod() && !xml() && !raw()")
    public void typed() {
    }

    @Pointcut("execution(io.reactivex.Observable<org.w3c.dom.Document> *(..))")
    public void xml() {
    }

    @Pointcut("execution(io.reactivex.Observable<okhttp3.Response> *(..))")
    public void raw() {
    }

    @Pointcut("@annotation(de.ovgu.softwareproductlines.annotation.type.GET)")
    public void getAnnotated() {
    }

    @Pointcut("@annotation(de.ovgu.softwareproductlines.annotation.type.PATCH)")
    public void patchAnnotated() {
    }

    @Pointcut("@annotation(de.ovgu.softwareproductlines.annotation.type.POST)")
    public void postAnnotated() {
    }

    @Pointcut("@annotation(de.ovgu.softwareproductlines.annotation.type.PUT)")
    public void putAnnotated() {
    }

    @Pointcut("@annotation(de.ovgu.softwareproductlines.annotation.JSON)")
    public void JSONAnnotated() {
    }

    @Pointcut("@annotation(de.ovgu.softwareproductlines.annotation.Form)")
    public void formAnnotated() {
    }

    @Pointcut("@annotation(de.ovgu.softwareproductlines.annotation.response.Gson)")
    public void gsonAnnotated() {
    }

    @Pointcut("@annotation(de.ovgu.softwareproductlines.annotation.response.Jackson)")
    public void jacksonAnnotated() {
    }

    @Pointcut("@annotation(action)")
    public void actionAnnotated(Action action) {
    }



    @Around("getAnnotated() && observableMethod()")
    public Object aroundGet(ProceedingJoinPoint jp) throws Throwable {
        APIMethod delegate = (APIMethod) jp.proceed();
        return new GetBuilder(delegate);
    }

    @Around("patchAnnotated() && observableMethod()")
    public Object aroundPatch(ProceedingJoinPoint jp) throws Throwable {
        APIMethod delegate = (APIMethod) jp.proceed();
        Request.Builder builder = new Request.Builder();
        builder.url(delegate.buildUrl(null));
        builder.patch(delegate.buildBody());
        return new PatchBuilder(delegate);
    }

    @Around("postAnnotated() && observableMethod()")
    public Object aroundPost(ProceedingJoinPoint jp) throws Throwable {
        APIMethod delegate = (APIMethod) jp.proceed();

        return new PostBuilder(delegate);
    }

    @Around("putAnnotated() && observableMethod()")
    public Object aroundPut(ProceedingJoinPoint jp) throws Throwable {
        APIMethod delegate = (APIMethod) jp.proceed();
        return new PutBuilder(delegate);
    }

    @Around("urlAnnotated(url) && observableMethod()")
    public Object aroundUrl(ProceedingJoinPoint jp, Url url) throws Throwable {
        APIMethod delegate = (APIMethod) jp.proceed();
        UrlAPIMethod method = new UrlAPIMethod(delegate, url.value());
        return method;
    }

    @Around("actionAnnotated(action) && observableMethod()")
    public ActionAPIMethod aroundAction(ProceedingJoinPoint jp, Action action) throws Throwable {
        return new ActionAPIMethod(((MethodSignature) jp.getSignature()).getMethod(), jp.getArgs(), action.value());
    }

    @Around("jacksonAnnotated() && observableMethod()")
    public JacksonAPIMethod jacksonAPIMethodAdvice(ProceedingJoinPoint jp) throws Throwable {
        APIMethod delegate = (APIMethod) jp.proceed();
        JacksonAPIMethod method = new JacksonAPIMethod(delegate);
        return method;
    }

    @Around("gsonAnnotated() && observableMethod()")
    public GsonAPIMethod gsonAPIMethodAdvice(ProceedingJoinPoint jp) throws Throwable {
        APIMethod delegate = (APIMethod) jp.proceed();
        return new GsonAPIMethod(delegate);
    }

    @Around("JSONAnnotated() && observableMethod()")
    public JsonAPIMethod jsonAPIMethodAdvice(ProceedingJoinPoint jp) throws Throwable {
        JsonAPIMethod method = new JsonAPIMethod(buildMethod(jp));
        return method;
    }

    @Around("formAnnotated() && observableMethod()")
    public FormAPIMethod formAPIMethodAdvice(ProceedingJoinPoint jp) throws Throwable {
        FormAPIMethod method = new FormAPIMethod(((MethodSignature) jp.getSignature()).getMethod(), jp.getArgs());
        return method;
    }

    private BaseAPIMethod buildMethod(ProceedingJoinPoint jp) {
        BaseAPIMethod method = new BaseAPIMethod(((MethodSignature) jp.getSignature()).getMethod(), jp.getArgs());
        return method;
    }
}
