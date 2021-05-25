package com.ksyun.ks3.services.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.BufferedHttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ksyun.ks3.auth.AuthEvent;
import com.ksyun.ks3.auth.AuthEventCode;
import com.ksyun.ks3.auth.AuthUtils;
import com.ksyun.ks3.auth.DefaultSigner;
import com.ksyun.ks3.auth.RepeatableInputStreamRequestEntity;
import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.AsyncHttpRequsetParam;
import com.ksyun.ks3.model.HttpHeaders;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.model.acl.Authorization;
import com.ksyun.ks3.model.transfer.MD5DigestCalculatingInputStream;
import com.ksyun.ks3.model.transfer.RequestProgressListener;
import com.ksyun.ks3.services.AuthListener;
import com.ksyun.ks3.services.AuthResult;
import com.ksyun.ks3.services.Ks3AuthHandler;
import com.ksyun.ks3.services.Ks3ClientConfiguration;
import com.ksyun.ks3.services.ServerDateAuthListener;
import com.ksyun.ks3.util.ByteUtil;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.DateUtil;
import com.ksyun.ks3.util.HttpUtils;
import com.ksyun.ks3.util.RequestUtils;
import com.ksyun.ks3.util.StringUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

public abstract class Ks3HttpRequest implements Serializable {

    private static final long serialVersionUID = -5871616471337887313L;
    private String url;
    private String bucketname;
    private String objectkey;
    private String paramsToSign = "";
    private HttpEntity entity;
    private InputStream requestBody;
    private HttpMethod httpMethod;
    private Map<String, String> header = new HashMap<String, String>();
    private Map<String, String> params = new HashMap<String, String>();
    private Authorization authorization;
    private Context context;
    private AsyncHttpRequsetParam asyncHttpRequestParam;
    private AuthListener authListener;
    private String authorizationStr;
    private RequestProgressListener progressListener;
    private RequestHandle handler;
    private static final Pattern ENCODED_CHARACTERS_PATTERN;


    static {
        StringBuilder pattern = new StringBuilder();

        pattern.append(Pattern.quote("+")).append("|")
                .append(Pattern.quote("*")).append("|")
                .append(Pattern.quote("%7E")).append("|");

        ENCODED_CHARACTERS_PATTERN = Pattern.compile(pattern.toString());
    }


    /* url */
    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {

        this.url = url;
    }

    /* bucket */
    public void setBucketname(String bucketname) {

        this.bucketname = bucketname;
    }

    public String getBucketname() {

        return bucketname;
    }

    /* Entity */
    public HttpEntity getEntity() {

        return entity;
    }

    public void setEntity(HttpEntity entity) {

        this.entity = entity;
    }

    /* Endpoint */
    public String getEndpoint() {

        return this.header.get(HttpHeaders.Host.toString());
    }

    public void setEndpoint(String endpoint) {

        this.addHeader(HttpHeaders.Host.toString(), endpoint);
    }

    /* object */
    public void setObjectkey(String objectkey) {

        this.objectkey = objectkey;
    }

    public String getObjectkey() {

        return objectkey;
    }

    /* authorization */
    public void setAuthorization(Authorization authorization) {

        this.authorization = authorization;
    }

    public Authorization getAuthorization() {

        return authorization;
    }

    /* Request body */
    public InputStream getRequestBody() {

        return requestBody;
    }

    public void setRequestBody(InputStream requestBody) {

        this.requestBody = requestBody;
    }

    /* Header */
    public void addHeader(String key, String value) {

        this.header.put(key, value);
    }

    protected void addHeader(HttpHeaders key, String value) {

        this.addHeader(key.toString(), value);
    }

    public void setHeader(Map<String, String> header) {

        this.header = header;
    }

    public Map<String, String> getHeader() {

        return header;
    }

    /* paramsToSign */
    protected void setParamsToSign(String paramsToSign) {

        this.paramsToSign = paramsToSign;
    }

    public String getParamsToSign() {

        return paramsToSign;
    }

    /* params */
    protected void addParams(String key, String value) {

        this.params.put(key, value);
    }

    public void setParams(Map<String, String> params) {

        this.params = params;
    }

    public Map<String, String> getParams() {

        return params;
    }

    /* httpMethod */
    public void setHttpMethod(HttpMethod httpMethod) {

        this.httpMethod = httpMethod;
    }

    public HttpMethod getHttpMethod() {

        return httpMethod;
    }

    /* ContentMD5 */
    protected void setContentMD5(String md5) {
        if (!StringUtils.isBlank(md5))
            this.addHeader(HttpHeaders.ContentMD5.toString(), md5);
    }

    public String getContentMD5() {
        String res = this.header.get(HttpHeaders.ContentMD5.toString());
        return res == null ? "" : res;
    }

    /* ContentHandler Type */
    public void setContentType(String type) {
        this.header.put(HttpHeaders.ContentType.toString(), type);
    }

    public String getContentType() {
        String res = this.header.get(HttpHeaders.ContentType.toString());
        return res == null ? "" : res;
    }

    /* Date */
    public String getDate() {

        String s = this.header.get(HttpHeaders.Date.toString());
        if (TextUtils.isEmpty(s)) {
            return null;
        } else {
            return s;
        }
    }

    protected void setDate(String string) {

        this.addHeader(HttpHeaders.Date.toString(), string);
    }

    /* Context */
    public Context getContext() {

        return context;
    }

    public void setContext(Context context) {

        this.context = context;
    }

    /* AsyncHttpRequsetParam */
    public AsyncHttpRequsetParam getAsyncHttpRequestParam() {

        return asyncHttpRequestParam;
    }

    public void setAsyncHttpRequestParam(
            AsyncHttpRequsetParam asyncHttpRequestParam) {

        this.asyncHttpRequestParam = asyncHttpRequestParam;
    }

    /**
     * Important, Should call it when completed a request
     *
     * @param ks3AuthHandler
     */
    public void completeRequset(Ks3AuthHandler ks3AuthHandler,
                                AsyncHttpResponseHandler handler) throws Ks3ClientException {

        this.validateParams();
        setupRequestDefault();
        setupRequest();
        if (StringUtils.isBlank(getContentType())) {
            setContentType("text/plain");
        }
        if (handler instanceof RequestProgressListener) {
            this.progressListener = (RequestProgressListener) handler;
        }
        this.asyncHttpRequestParam = finishHttpRequest(ks3AuthHandler);
        if (authListener != null && ks3AuthHandler.isNeedCalculateAuth) {
            if (!TextUtils.isEmpty(authorizationStr)) {
                AuthEvent event = new AuthEvent();
                event.setCode(AuthEventCode.Success);
                event.setContent(authorizationStr);
                Log.d(Constants.LOG_TAG, "retrieve auth string success :"
                        + authorizationStr);
                Log.d(Constants.LOG_TAG, "make requset complete");
                ks3AuthHandler.onSuccess(event);
            } else {
                AuthEvent event = new AuthEvent();
                event.setCode(AuthEventCode.Failure);
                event.setContent("failure reason : authorizaion is not correct :"
                        + authorizationStr);
                Log.d(Constants.LOG_TAG, "make requset failed");
                ks3AuthHandler.onFailure(event);
            }
        }
        Log.d(Constants.LOG_TAG, "make requset complete");
    }

    private void setupRequestDefault() {

        httpMethod = HttpMethod.POST;
        this.setContentMD5("");
        this.addHeader(HttpHeaders.UserAgent, Constants.KS3_SDK_USER_AGENT);
        this.setDate(DateUtil.GetUTCTime());
    }

    @SuppressWarnings("deprecation")
    private AsyncHttpRequsetParam finishHttpRequest(
            Ks3AuthHandler ks3AuthHandler) throws Ks3ClientException {

        // Prepare md5 if need
        if (this instanceof MD5CalculateAble && this.getRequestBody() != null) {
            if (!(this.getRequestBody() instanceof MD5DigestCalculatingInputStream))
                this.setRequestBody(new MD5DigestCalculatingInputStream(this
                        .getRequestBody()));
        }
        String encodedParams = encodeParams();
        this.setUrl(toUrl());

        if (this.getHttpMethod() == HttpMethod.POST) {
            if (requestBody == null && params != null) {
                try {
                    setEntity(new StringEntity(encodedParams));
                } catch (UnsupportedEncodingException e) {
                    throw new Ks3ClientException(
                            "Unable to create HTTP entity:" + e, e);
                }
            } else {
                String length = this.getHeader().get(
                        HttpHeaders.ContentLength.toString());
                HttpEntity entity = new RepeatableInputStreamRequestEntity(
                        requestBody, length);
                try {
                    entity = new BufferedHttpEntity(entity);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new Ks3ClientException("init http request error(" + e
                            + ")", e);
                }
                // Set entity
                setEntity(entity);
            }
        } else if (this.getHttpMethod() == HttpMethod.GET) {
        } else if (this.getHttpMethod() == HttpMethod.PUT) {
            if (requestBody != null) {
                Map<String, String> headrs = this.getHeader();
                String length = headrs
                        .get(HttpHeaders.ContentLength.toString());
                if (length == null)
                    throw new Ks3ClientException(
                            "content-length can not be null when put request");
                RepeatableInputStreamRequestEntity entity = new RepeatableInputStreamRequestEntity(
                        requestBody, length);
                entity.setProgressLisener(this.progressListener);
                setEntity(entity);
            }
        } else if (this.getHttpMethod() == HttpMethod.DELETE) {

        } else if (this.getHttpMethod() == HttpMethod.HEAD) {

        } else {
            throw new Ks3ClientException("Unknow http method : "
                    + this.getHttpMethod());
        }

        if (!StringUtils.isBlank(header.get(HttpHeaders.ContentLength
                .toString()))) {
            header.remove(HttpHeaders.ContentLength.toString());
        }
        if (authListener != null) {

            StringBuffer sBuffer = new StringBuffer();
            sBuffer.append(this.getHttpMethod().toString()).append("\n");
            sBuffer.append(this.getContentMD5()).append("\n");
            sBuffer.append(this.getContentType()).append("\n");
            sBuffer.append(this.getDate()).append("\n");
            sBuffer.append(AuthUtils.CanonicalizedKSSHeaders(this)).append("\n");
            sBuffer.append(AuthUtils.CanonicalizedKSSResource(this));
            String signStr = sBuffer.toString();
            Log.i(Constants.LOG_TAG, "the correct StringToSign should be :"
                    + signStr);
            if (authorization != null) {
                Log.i(Constants.LOG_TAG, "the correct auth string should be "
                        + new DefaultSigner().calculate(authorization, this).trim());
            }
            AuthResult authResult = null;
            if (authListener instanceof ServerDateAuthListener) {
                authResult = ((ServerDateAuthListener) authListener).onCalculateAuthWithServerDate(this
                        .getHttpMethod().toString(), this.getContentType(), this
                        .getDate(), this.getContentMD5(), AuthUtils
                        .CanonicalizedKSSResource(this), AuthUtils
                        .CanonicalizedKSSHeaders(this));
                authorizationStr = authResult.getToken();
                setDate(authResult.getDate());
            } else {
                authorizationStr = authListener.onCalculateAuth(this
                        .getHttpMethod().toString(), this.getContentType(), this
                        .getDate(), this.getContentMD5(), AuthUtils
                        .CanonicalizedKSSResource(this), AuthUtils
                        .CanonicalizedKSSHeaders(this));
            }
            if (authorizationStr == null)
                authorizationStr = "";
            Log.i(Constants.LOG_TAG, "app server return auth string is  :"
                    + authorizationStr.trim());
            this.addHeader(HttpHeaders.Authorization.toString(),
                    authorizationStr.trim());
        } else {
            this.addHeader(HttpHeaders.Authorization.toString(),
                    new DefaultSigner().calculate(authorization, this).trim());
        }
        if (entity != null) {
            return new AsyncHttpRequsetParam(url, getContentType(), header,
                    params, entity);
        } else {
            return new AsyncHttpRequsetParam(url, header, params);
        }

    }

    @SuppressWarnings("deprecation")
    private String encodeParams() {

        List<Map.Entry<String, String>> arrayList = new ArrayList<Map.Entry<String, String>>(
                this.params.entrySet());
        Collections.sort(arrayList,
                new Comparator<Map.Entry<String, String>>() {

                    @Override
                    public int compare(Entry<String, String> o1,
                                       Entry<String, String> o2) {

                        return ByteUtil.compareTo(o1.getKey().toString()
                                .getBytes(), o2.getKey().toString().getBytes());
                    }
                });
        List<String> kvList = new ArrayList<String>();
        List<String> list = new ArrayList<String>();
        for (Entry<String, String> entry : arrayList) {
            String value = null;
            String key = entry.getKey()
                    .replace(String.valueOf((char) 8203), "");
            if (!StringUtils.isBlank(entry.getValue())) {
                value = URLEncoder.encode(entry.getValue());
            }
            if (RequestUtils.subResource.contains(entry.getKey())) {
                if (value != null && !value.equals(""))
                    kvList.add(key + "=" + value);
                else
                    kvList.add(key);
            }
            if (value != null && !value.equals("")) {
                list.add(key + "=" + value);
            } else {
                if (RequestUtils.subResource.contains(key))
                    list.add(key);
            }
        }
        String queryParams = TextUtils.join("&", list.toArray());
        this.setParamsToSign(TextUtils.join("&", kvList.toArray()));
        return queryParams;
    }

    /* Setup header,parameter and so on */
    protected abstract void setupRequest() throws Ks3ClientException;

    /* Validate parameters */
    protected abstract String validateParams() throws Ks3ClientException;

    public AuthListener getAuthListener() {

        return authListener;
    }

    public void setAuthListener(AuthListener authListener) {

        this.authListener = authListener;

    }

    public void setRequestHandler(RequestHandle handler) {

        if (this.handler != null) {
            Log.e(Constants.LOG_TAG,
                    "method : setRequestHandler , is an internal method, and the handler is already set up , ingnore ! ");
            return;
        }

        this.handler = handler;
    }

    public boolean abort() {

        if (this.handler != null) {
            return this.handler.cancel(true);
        } else {
            Log.e(Constants.LOG_TAG,
                    "the request is on RUNNING status , or the request is on sync mode , igonre abort request ! ");
            return false;
        }
    }

    public static String urlEncode(final String value) {

        if (value == null) {
            return "";
        }

        Matcher matcher = ENCODED_CHARACTERS_PATTERN.matcher(value);
        StringBuffer buffer = new StringBuffer(value.length());

        while (matcher.find()) {
            String replacement = matcher.group(0);

            if ("+".equals(replacement)) {
                replacement = "%20";
            } else if ("*".equals(replacement)) {
                replacement = "%2A";
            } else if ("%7E".equals(replacement)) {
                replacement = "~";
            }

            matcher.appendReplacement(buffer, replacement);
        }

        matcher.appendTail(buffer);
        return buffer.toString();

    }

    @Override
    protected void finalize() throws Throwable {
        Log.d("Ks3HttpRequest", "Ks3HttpRequest finalize:" + this);
        super.finalize();
    }

    public String toUrl() {
        String url = "";
        String bucket = this.getBucketname();
        String key = this.getObjectkey();
        String endpoint = this.getEndpoint();
        key = HttpUtils.urlEncode(key, true);
        String encodedParams = HttpUtils.encodeParams(this.getParams());
        boolean pathStyle = Ks3ClientConfiguration.getDefaultConfiguration().isPathStyleAccess();
        boolean domainMode = Ks3ClientConfiguration.getDefaultConfiguration().getDomainMode();

        Ks3ClientConfiguration.PROTOCOL spePro = Ks3ClientConfiguration.getDefaultConfiguration().getProtocol();
        if (spePro == null)
            spePro = Ks3ClientConfiguration.PROTOCOL.http;

        if (bucket == null || pathStyle) {
            url = new StringBuffer()
                    .append(endpoint)
                    .append(StringUtils.isBlank(bucket) ? "" : "/" + bucket)
                    .append(StringUtils.isBlank(key) ? "" : "/" + key)
                    .toString();
        } else {
            url = new StringBuffer()
                    .append(endpoint)
                    .append(StringUtils.isBlank(key) ? "" : "/" + key)
                    .toString();
        }

        url = url.replace("//", "/%2F");
        url = spePro.toString() + "://" + url;
        if (!StringUtils.isBlank(encodedParams))
            url += "?" + encodedParams;
        return url;
    }

}