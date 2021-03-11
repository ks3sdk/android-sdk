package com.ksyun.ks3.auth;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.ks3.demo.main.utils.DateUtils;
import com.ksyun.ks3.model.acl.Authorization;
import com.ksyun.ks3.services.request.Ks3HttpRequest;
import com.ksyun.ks3.util.ByteUtil;
import com.ksyun.ks3.util.Constants;
import com.ksyun.ks3.util.HttpUtils;
import com.ksyun.ks3.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

//import cz.msebera.android.httpclient.extras.Base64;

public class AuthUtils {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    public static String calcAuthorization(Authorization auth,
                                           Ks3HttpRequest request) throws SignatureException {
        String signature = calcSignature(auth.getAccessKeySecret(), request);
        String value = "KSS " + auth.getAccessKeyId() + ":" + signature;
        return value;
    }

    private static String calcSignature(String accessKeySecret,
                                        Ks3HttpRequest request) throws SignatureException {
        String resource = CanonicalizedKSSResource(request);
        String requestMethod = request.getHttpMethod().toString();
        String contentMd5 = request.getContentMD5();
        String contentType = request.getContentType();
        String _signDate = request.getDate();
        List<String> signList = new ArrayList<String>();
        signList.addAll(Arrays.asList(requestMethod, contentMd5,
                contentType, _signDate));
        String _headers = CanonicalizedKSSHeaders(request).trim();
        if (_headers != null && !_headers.equals("")) {
            signList.add(_headers);
        }
        signList.add(resource);
        String signStr = StringUtils.join(signList.toArray(), "\n");
        String serverSignature = calculateRFC2104HMAC(signStr, accessKeySecret);
        Log.d(Constants.LOG_TAG, "signStr = " + signStr);
        return serverSignature;
    }

    public static String calcAuthToken(String httpMethod, String contentType,
                                       String date, String contentMD5, String resource, String Headers,
                                       String accessKeyId, String accessKeySecret)
            throws SignatureException {
        List<String> signList = new ArrayList<String>();
        signList.addAll(Arrays.asList(httpMethod, contentMD5,
                contentType, date));
        String _headers = Headers;
        if (_headers != null && !_headers.equals("")) {
            signList.add(_headers);
        }
        signList.add(resource);
        String signStr = StringUtils.join(signList.toArray(), "\n");
        String serverSignature = calculateRFC2104HMAC(signStr, accessKeySecret);
        String value = "KSS " + accessKeyId + ":" + serverSignature;
        return value;
    }

    public static String CanonicalizedKSSResource(Ks3HttpRequest request) {

        String bucketName = request.getBucketname();
        String objectKey = request.getObjectkey();

        StringBuffer buffer = new StringBuffer();
        buffer.append("/");
        if (!TextUtils.isEmpty(bucketName)) {
            buffer.append(bucketName).append("/");
        }

        if (!TextUtils.isEmpty(objectKey)) {
            String encodedPath = HttpUtils.urlEncode(objectKey, true);
            encodedPath = encodedPath.replace("//", "/%2F");
            buffer.append(encodedPath);
        }

        String resource = buffer.toString();

        String queryParams = request.getParamsToSign();
        if (queryParams != null && !queryParams.equals(""))
            resource = resource + "?" + queryParams;
        return resource;
    }

    public static String CanonicalizedKSSHeaders(Ks3HttpRequest request) {
        String prefix = "x-kss";
        Map<String, String> headers = request.getHeader();

        List<String> headList = new ArrayList<String>();

        for (String _header : headers.keySet()) {
            if (_header.toLowerCase(Locale.US).startsWith(prefix)) {
                headList.add(_header);
            }
        }

        Collections.sort(headList, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return ByteUtil.compareTo(o1.getBytes(), o2.toString()
                        .getBytes());
            }
        });
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < headList.size(); i++) {
            String _key = headList.get(i);
            buffer.append(headList.get(i) + ":" + headers.get(_key));
            if (i < (headList.size() - 1))
                buffer.append("\n");
        }
        Log.d(Constants.LOG_TAG, "header signer str = " + buffer.toString() +"\n");
        return buffer.toString();
    }

    public static String calculateRFC2104HMAC(String data, String key)
            throws java.security.SignatureException {
        String result;
        try {
            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
                    HMAC_SHA1_ALGORITHM);
            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());
            // base64-encode the hmac
            // result = new String(Base64.encodeBase64(rawHmac), "GBK");
            result = Base64.encodeToString(rawHmac, Base64.DEFAULT);
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e);
        }
        return result;
    }
    //post表单时的签名
    /**
     *
     * @param accessKeySecret
     * @param policy  getPolicy(Date expiration,String bucket)得到的结果
     * @return
     * @throws SignatureException
     */
    public static String calcSignature(String accessKeySecret,String policy) throws SignatureException{
        String signStr = policy;
        return calculateRFC2104HMAC(signStr,accessKeySecret);
    }
    //post表单时的policy
    /**
     *
     * @param expiration 该签名过期时间
     * @param bucket 该签名只能在该bucket上使用
     * @return
     */
    @Deprecated
    public static String getPolicy(Date expiration, String bucket) {
        String policy = "{\"expiration\": \""
                + DateUtils.convertDate2Str(expiration, DateUtils.DATETIME_PROTOCOL.ISO8861)
                +"\",\"conditions\": [ {\"bucket\": \""+bucket+"\"}]}";

        try {
//            String _policy = new String(Base64.encodeBase64(policy.getBytes("UTF-8")),"UTF-8");
//            String ss = new String(policy.getBytes("UTF-8"), "UTF-8");
            String _policy = Base64.encodeToString(policy.getBytes("UTF-8"),Base64.DEFAULT);
            return _policy;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
