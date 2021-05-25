package com.ks3.demo.main;

import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.ks3.demo.main.BucketInpuDialog.OnBucketDialogListener;
import com.ks3.demo.main.BucketObjectInpuDialog.OnBucketObjectDialogListener;
import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.Bucket;
import com.ksyun.ks3.model.Ks3ObjectSummary;
import com.ksyun.ks3.model.ObjecVersiontListing;
import com.ksyun.ks3.model.ObjectListing;
import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.Owner;
import com.ksyun.ks3.model.PostObjectFormFields;
import com.ksyun.ks3.model.acl.AccessControlPolicy;
import com.ksyun.ks3.model.acl.CannedAccessControlList;
import com.ksyun.ks3.model.acl.Grant;
import com.ksyun.ks3.model.result.BucketQuota;
import com.ksyun.ks3.model.result.CopyResult;
import com.ksyun.ks3.model.result.GetObjectResult;
import com.ksyun.ks3.model.result.HeadObjectResult;
import com.ksyun.ks3.model.result.ListPartsResult;
import com.ksyun.ks3.model.result.PutAdpResult;
import com.ksyun.ks3.model.result.ReplicationRule;
import com.ksyun.ks3.model.result.policy.BucketPolicyConditionRule;
import com.ksyun.ks3.model.result.policy.BucketPolicyRule;
import com.ksyun.ks3.services.Ks3Client;
import com.ksyun.ks3.services.Ks3ClientConfiguration;
import com.ksyun.ks3.services.handler.CopyObjectResponseHandler;
import com.ksyun.ks3.services.handler.CreateBucketResponceHandler;
import com.ksyun.ks3.services.handler.DeleteBucketReplicationConfigResponceHandler;
import com.ksyun.ks3.services.handler.DeleteBucketResponceHandler;
import com.ksyun.ks3.services.handler.DeleteObjectRequestHandler;
import com.ksyun.ks3.services.handler.GetBucketACLResponceHandler;
import com.ksyun.ks3.services.handler.GetBucketPolicyResponceHandler;
import com.ksyun.ks3.services.handler.GetBucketQuotaResponceHandler;
import com.ksyun.ks3.services.handler.GetBucketReplicationConfigResponceHandler;
import com.ksyun.ks3.services.handler.GetBucketVersioningHandler;
import com.ksyun.ks3.services.handler.GetObjectACLResponseHandler;
import com.ksyun.ks3.services.handler.GetObjectResponseHandler;
import com.ksyun.ks3.services.handler.GetObjectTaggingResponseHandler;
import com.ksyun.ks3.services.handler.HeadBucketResponseHandler;
import com.ksyun.ks3.services.handler.HeadObjectResponseHandler;
import com.ksyun.ks3.services.handler.Ks3HttpResponceHandler;
import com.ksyun.ks3.services.handler.ListBucketsResponceHandler;
import com.ksyun.ks3.services.handler.ListObjectsResponseHandler;
import com.ksyun.ks3.services.handler.ListObjectsVersionResponseHandler;
import com.ksyun.ks3.services.handler.ListPartsResponseHandler;
import com.ksyun.ks3.services.handler.PutBucketACLResponseHandler;
import com.ksyun.ks3.services.handler.PutBucketReplicationResponceHandler;
import com.ksyun.ks3.services.handler.PutObjectACLResponseHandler;
import com.ksyun.ks3.services.handler.PutObjectAdpResponceHandler;
import com.ksyun.ks3.services.handler.PutObjectFetchResponseHandler;
import com.ksyun.ks3.services.handler.PutObjectResponseHandler;
import com.ksyun.ks3.services.request.CopyObjectRequest;
import com.ksyun.ks3.services.request.DeleteBucketPolicyRequest;
import com.ksyun.ks3.services.request.DeleteBucketReplicationConfigRequest;
import com.ksyun.ks3.services.request.DeleteObjectRequest;
import com.ksyun.ks3.services.request.GetBucketPolicyRequest;
import com.ksyun.ks3.services.request.GetBucketQuotaRequest;
import com.ksyun.ks3.services.request.GetBucketReplicationConfigRequest;
import com.ksyun.ks3.services.request.GetObjectRequest;
import com.ksyun.ks3.services.request.ListObjectVersionsRequest;
import com.ksyun.ks3.services.request.ListObjectsRequest;
import com.ksyun.ks3.services.request.PutBuckePolicyRequest;
import com.ksyun.ks3.services.request.PutBuckeQuotaRequest;
import com.ksyun.ks3.services.request.PutBucketACLRequest;
import com.ksyun.ks3.services.request.PutBucketReplicationConfigRequest;
import com.ksyun.ks3.services.request.PutObjectACLRequest;
import com.ksyun.ks3.services.request.adp.Adp;
import com.ksyun.ks3.services.request.adp.PutAdpRequest;
import com.ksyun.ks3.services.request.object.PutObjectFetchRequest;
import com.ksyun.ks3.services.request.object.PutObjectFetchResult;
import com.ksyun.ks3.services.request.tag.DeleteObjectTaggingRequest;
import com.ksyun.ks3.services.request.tag.GetObjectTaggingRequest;
import com.ksyun.ks3.services.request.tag.ObjectTagging;
import com.ksyun.ks3.services.request.tag.PutObjectTaggingRequest;
import com.ksyun.ks3.services.request.version.BucketVersioningConfiguration;
import com.ksyun.ks3.services.request.version.GetBucketVersioningRequest;
import com.ksyun.ks3.services.request.version.PutBucketVersioningRequest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static com.ks3.demo.main.Constants.*;
import static com.ksyun.ks3.services.request.version.BucketVersioningConfiguration.ENABLED;

/**
 * 包含一系列资源管理操作Api使用示例
 */
public class MainActivity extends AppCompatActivity {

    // Bucket
    public static final int LIST_BUCKETS = 0;
    public static final int CREATE_BUCKET = 1;
    public static final int GET_BUCKET_ACL = 2;
    public static final int PUT_BUCKET_ACL = 3;
    public static final int HEAD_BUCKET = 4;
    public static final int DELETE_BUCKET = 5;
    public static final int PUT_BUCKET_CRR = 18;
    public static final int PUT_BUCKET_POLICY = 19;
    public static final int PUT_BUCKET_QUOTA = 20;
    // Object
    public static final int GET_OBJECT = 6;
    public static final int HEAD_OBJECT = 7;
    public static final int PUT_OBJECT = 8;
    public static final int DELETE_OBJECT = 9;
    public static final int GET_OBJECT_ACL = 10;
    public static final int PUT_OBJECT_ACL = 11;
    public static final int LIST_OBJECTS = 12;
    public static final int COPY_OBJECT = 17;
    public static final int PUT_OBJECT_ADP = 21;
    public static final int PUT_OBJECT_TAG = 22;
    public static final int FETCH_OBJECT = 23;
    public static final int POST_OBJECT = 24;
    public static final int VERSION = 25;
    // Upload
    public static final int UPLOAD = 13;
    // Download
    public static final int DOWNLOAD = 14;
    public static final int LIST_PART = 15;
    public static final int MultiPartUpload = 16;
    private static final String API = "api";
    private static final String RESULT = "result";
    private Ks3ClientConfiguration configuration;
    private Ks3Client client;
    private TextView resultTv;
    private ListView commandList;
    private String[] command_array;
    private Builder bucketDialogBuilder;
    private BucketInpuDialog bucketInpuDialog;
    private BucketObjectInpuDialog bucketObjectInpuDialog;
    private BucketCopyObjectInpuDialog bucketCopyObjectInpuDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        setUpKs3Client();
        setUpUserInterface();
        PermissionUtils.permission(PermissionConstants.STORAGE).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied() {

            }
        }).request();
    }

    public static void main(String[] args) {

//       Ks3Client  client = Ks3ClientFactory.getDefaultClient(this);
//        Ks3ClientConfiguration configuration2 = Ks3ClientConfiguration.getDefaultConfiguration();
//        configuration2.setPathStyleAccess(false);
    }

    private void setUpUserInterface() {
        bucketCopyObjectInpuDialog = new BucketCopyObjectInpuDialog(MainActivity.this);
        bucketInpuDialog = new BucketInpuDialog(MainActivity.this);
        bucketObjectInpuDialog = new BucketObjectInpuDialog(MainActivity.this);
        commandList = (ListView) findViewById(R.id.command_list);
        command_array = getResources().getStringArray(R.array.command_array);
        commandList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, command_array));
        commandList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                switch (position) {
                    case UPLOAD:
                        Intent intent_upload = new Intent(MainActivity.this,
                                UploadActivity.class);
                        startActivity(intent_upload);
                        break;
                    case DOWNLOAD:
                        Intent intent_download = new Intent(MainActivity.this,
                                DownloadActivity.class);
                        startActivity(intent_download);
                        break;
                    case LIST_BUCKETS:
                        listBuckets();
                        break;
                    case CREATE_BUCKET:
                        createBucket();
                        break;
                    case GET_BUCKET_ACL:
                        getBucketACL();
                        break;
                    case PUT_BUCKET_ACL:
                        putBucketACL();
                        break;
                    case HEAD_BUCKET:
                        headBucket();
                        break;
                    case DELETE_BUCKET:
                        deleteBucket();
                        break;
                    case GET_OBJECT:
                        getObject();
                        break;
                    case HEAD_OBJECT:
                        headObject();
                        break;
                    case PUT_OBJECT:
                        putObject();
                        break;
                    case DELETE_OBJECT:
                        deleteObject();
                        break;
                    case GET_OBJECT_ACL:
                        getObjectACL();
                        break;
                    case PUT_OBJECT_ACL:
                        putObjectACL();
                        break;
                    case LIST_OBJECTS:
                        listObjects();
                        break;
                    case LIST_PART:
                        listParts();
                        break;
                    case MultiPartUpload:
                        Intent multi_upload = new Intent(MainActivity.this,
                                MultiUploadActivity.class);
                        startActivity(multi_upload);
                        break;
                    case COPY_OBJECT:
                        copyObject();
                        break;
                    case PUT_BUCKET_CRR:
                        // getBucketCrr();
                        // deleteBucketCrr();
                        putBucketCrr();
                        break;
                    case PUT_BUCKET_QUOTA:
                        putBucketQuota();
                        getBucketQuota();
                        break;
                    case PUT_BUCKET_POLICY:
                        putBucketPolicy();
                        break;
                    case PUT_OBJECT_ADP:
                        testPutAndQueryAdp();
                        break;
                    case PUT_OBJECT_TAG:
                        testPutObjTag();
                        break;
                    case FETCH_OBJECT:
                        testPutFetchObj();
                        break;
                    case POST_OBJECT:
                        postObject();
                        break;
                    case VERSION:
                        testVersion();
                        break;
                    default:
                        break;
                }
            }

        });
    }

    private void copyObject() {
        ObjectTagging objectTagging = new ObjectTagging();
        objectTagging.addObjectTag("tagA", "b");
        objectTagging.setTaggingDirective("Replace");

        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(SRC_BUCKETNAME, "ZZZb", SRC_BUCKETNAME, SRC_OBJECTKEY, objectTagging);
        client.copyObject(copyObjectRequest, new CopyObjectResponseHandler() {
            @Override
            public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
                System.out.println("fail copyObjectResponse is " + new String(response));
            }

            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, CopyResult result) {
                System.out.println("success copyObjectResponse is " + result.toString());
            }
        });
    }

    private void putObject() {
        final File file = new File(Constants.TEST_MULTIUPLOAD_FILE);

        bucketObjectInpuDialog
                .setOnBucketObjectDialogListener(new OnBucketObjectDialogListener() {
                    @Override
                    public void confirmBucketAndObject(String name, String key) {
                        client.putObject(name, key, file,
                                new PutObjectResponseHandler() {

                                    @Override
                                    public void onTaskSuccess(int statesCode,
                                                              Header[] responceHeaders) {
                                        StringBuffer stringBuffer = new StringBuffer();
                                        stringBuffer.append(
                                                "upload file success,file = "
                                                        + Constants.TEST_IMG
                                                        + ",states code = "
                                                        + statesCode).append(
                                                "\n");

                                        Intent intent = new Intent(
                                                MainActivity.this,
                                                RESTAPITestResult.class);

                                        Bundle data = new Bundle();
                                        data.putString(RESULT,
                                                stringBuffer.toString());
                                        data.putString(API, "put object Result");
                                        intent.putExtras(data);
                                        startActivity(intent);
                                        Log.e("tag", "putObject--onTaskSuccess---" + stringBuffer.toString());

                                    }

                                    @Override
                                    public void onTaskStart() {
                                        Log.e("tag", "putObject--onTaskStart---");
                                    }

                                    @Override
                                    public void onTaskFinish() {
                                        Log.e("tag", "putObject--onTaskFinish---");
                                    }

                                    @Override
                                    public void onTaskProgress(double progress) {
                                        Log.e("tag", "putObject--onTaskProgress---" + progress);
                                    }

                                    @Override
                                    public void onTaskCancel() {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onTaskFailure(int statesCode,
                                                              Ks3Error error,
                                                              Header[] responceHeaders,
                                                              String response,
                                                              Throwable paramThrowable) {
                                        StringBuffer stringBuffer = new StringBuffer();
                                        stringBuffer.append(
                                                "upload file failure,file = "
                                                        + Constants.TEST_IMG
                                                        + ",states code = "
                                                        + statesCode).append(
                                                "\n").append("response:").append(response);
                                        Log.e("tag", "putObject--onTaskFailure---" + stringBuffer.toString());

                                    }
                                });
                    }
                });
        bucketObjectInpuDialog.show();

    }

    protected void listParts() {
        bucketObjectInpuDialog
                .setOnBucketObjectDialogListener(new OnBucketObjectDialogListener() {
                    @Override
                    public void confirmBucketAndObject(String name, String key) {

                        client.listParts(name, key, Constants.UPLOAD_ID,
                                new ListPartsResponseHandler() {

                                    @Override
                                    public void onSuccess(int statesCode,
                                                          Header[] responceHeaders,
                                                          ListPartsResult listPartsResult) {
                                        Log.e("tag", "listParts--onSuccess---" + statesCode);
                                    }

                                    @Override
                                    public void onFailure(int statesCode,
                                                          Ks3Error error,
                                                          Header[] responceHeaders,
                                                          String response,
                                                          Throwable paramThrowable) {
                                        // TODO Auto-generated method stub
                                        Log.e("tag", "listParts--onFailure---" + statesCode);

                                    }
                                });
                    }
                });
        bucketObjectInpuDialog.show();

    }

    private void setUpKs3Client() {
        // AK&SK形式直接初始化，仅建议测试时使用，正式环境下请替换AuthListener方式
//        client = new Ks3Client(Constants.ACCESS_KEY__ID,
//                Constants.ACCESS_KEY_SECRET, MainActivity.this);
//        client.setEndpoint("ks3-cn-beijing.ksyun.com");
//        client.setConfiguration(configuration);
//
        client = Ks3ClientFactory.getDefaultClient(this);
        configuration = Ks3ClientConfiguration.getDefaultConfiguration();
        configuration.setPathStyleAccess(true);
        // AuthListener方式初始化
        // client = new Ks3Client(new AuthListener() {
        // @Override
        // public String onCalculateAuth(final String httpMethod,
        // final String ContentType, final String Date,
        // final String ContentMD5, final String Resource,
        // final String Headers) {
        // // 此处应由APP端向业务服务器发送post请求返回Token。
        // // 需要注意该回调方法运行在非主线程
        // // 此处内部写法仅为示例，开发者请根据自身情况修改
        // StringBuffer result = new StringBuffer();
        // HttpPost request = new HttpPost(Constants.APP_SERTVER_HOST);
        // StringEntity se;
        // try {
        // JSONObject object = new JSONObject();
        // object.put("http_method", httpMethod.toString());
        // object.put("content_type", ContentType);
        // object.put("date", Date);
        // object.put("content_md5", ContentMD5);
        // object.put("resource", Resource);
        // object.put("headers", Headers);
        // se = new StringEntity(object.toString());
        // request.setEntity(se);
        // HttpResponse httpResponse = new DefaultHttpClient().execute(request);
        // String retSrc = EntityUtils.toString(httpResponse
        // .getEntity());
        // result.append(retSrc);
        // } catch (JSONException e) {
        // e.printStackTrace();
        // } catch (UnsupportedEncodingException e) {
        // e.printStackTrace();
        // } catch (ClientProtocolException e) {
        // e.printStackTrace();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // return result.toString();
        // }
        // }, MainActivity.this);
        // client.setConfiguration(configuration);

    }

    //这块有疑问，没进行操作
    protected void getObject() {
        Toast.makeText(MainActivity.this, "Please See Download Activity",
                Toast.LENGTH_SHORT).show();
    }

    private void headObject() {
        bucketObjectInpuDialog
                .setOnBucketObjectDialogListener(new OnBucketObjectDialogListener() {
                    @Override
                    public void confirmBucketAndObject(String name, String key) {
                        client.headObject(name, key,
                                new HeadObjectResponseHandler() {
                                    @Override
                                    public void onSuccess(int statesCode,
                                                          Header[] responceHeaders,
                                                          HeadObjectResult headObjectResult) {

                                        StringBuffer stringBuffer = new StringBuffer();
                                        stringBuffer
                                                .append("lastModifiedDate      = "
                                                        + headObjectResult
                                                        .getLastmodified())
                                                .append("\n");
                                        stringBuffer.append("ETag                  = "
                                                + headObjectResult
                                                .getETag())
                                                .append("\n");
                                        ObjectMetadata metadata = headObjectResult
                                                .getObjectMetadata();
                                        stringBuffer.append(metadata);
                                        Intent intent = new Intent(
                                                MainActivity.this,
                                                RESTAPITestResult.class);

                                        Bundle data = new Bundle();
                                        data.putString(RESULT, headObjectResult.toString());
                                        data.putString(API, "head object Result");
                                        intent.putExtras(data);
                                        startActivity(intent);
                                        Log.e("tag", "headObject--onSuccess---" + headObjectResult.toString());
                                    }

                                    @Override
                                    public void onFailure(int statesCode,
                                                          Ks3Error error,
                                                          Header[] responceHeaders,
                                                          String response,
                                                          Throwable paramThrowable) {
                                        StringBuffer stringBuffer = new StringBuffer();
                                        stringBuffer.append(
                                                "head object , states code :"
                                                        + statesCode).append(
                                                "\n");
                                        stringBuffer.append("Exception :"
                                                + paramThrowable.toString());
                                        Intent intent = new Intent(
                                                MainActivity.this,
                                                RESTAPITestResult.class);
                                        Bundle data = new Bundle();
                                        data.putString(RESULT,
                                                stringBuffer.toString());
                                        data.putString(API,
                                                "head object Result");
                                        intent.putExtras(data);
                                        startActivity(intent);
                                        Log.e("tag", "headObject--onFailure---" + stringBuffer.toString());
                                    }
                                });

                    }
                });
        bucketObjectInpuDialog.show();
    }

    private void getObjectACL() {
        bucketObjectInpuDialog
                .setOnBucketObjectDialogListener(new OnBucketObjectDialogListener() {
                    @Override
                    public void confirmBucketAndObject(String name, String key) {
                        client.getObjectACL(name, key,
                                new GetObjectACLResponseHandler() {

                                    @Override
                                    public void onSuccess(
                                            int statesCode,
                                            Header[] responceHeaders,
                                            AccessControlPolicy accessControlPolicy) {
                                        StringBuffer stringBuffer = new StringBuffer();
                                        Owner owner = accessControlPolicy
                                                .getOwner();
                                        stringBuffer
                                                .append("=======Owner : ID "
                                                        + owner.getId()
                                                        + " ; NAME :"
                                                        + owner.getDisplayName())
                                                .append("\n");
                                        stringBuffer
                                                .append("==============ACL LIST=========");
                                        HashSet<Grant> grants = accessControlPolicy
                                                .getAccessControlList()
                                                .getGrants();
                                        for (Grant grant : grants) {
                                            stringBuffer
                                                    .append(grant.getGrantee()
                                                            .getIdentifier()
                                                            + "========>"
                                                            + grant.getPermission()
                                                            .toString())
                                                    .append("\n");
                                        }
                                        Intent intent = new Intent(
                                                MainActivity.this,
                                                RESTAPITestResult.class);

                                        Bundle data = new Bundle();
                                        data.putString(RESULT,
                                                stringBuffer.toString());
                                        data.putString(API,
                                                "head object Result");
                                        intent.putExtras(data);
                                        startActivity(intent);
                                        Log.e("tag", "getObjectACL--onSuccess---" + stringBuffer.toString());


                                    }

                                    @Override
                                    public void onFailure(int statesCode,
                                                          Ks3Error error,
                                                          Header[] responceHeaders,
                                                          String response,
                                                          Throwable paramThrowable) {
                                        StringBuffer stringBuffer = new StringBuffer();
                                        stringBuffer.append(
                                                "get object ACL FAIL !!!!!!, states code :"
                                                        + statesCode).append(
                                                "\n").append("response:").append(response);
                                        stringBuffer.append("Exception :"
                                                + paramThrowable.toString());
                                        Intent intent = new Intent(
                                                MainActivity.this,
                                                RESTAPITestResult.class);
                                        Bundle data = new Bundle();
                                        data.putString(RESULT,
                                                stringBuffer.toString());
                                        data.putString(API,
                                                "GET Object ACL Result");
                                        intent.putExtras(data);
                                        startActivity(intent);
                                        Log.e("tag", "getObjectACL--onFailure---" + stringBuffer.toString());

                                    }
                                });

                    }
                });
        bucketObjectInpuDialog.show();
    }

    private void deleteBucket() {
        bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
            @Override
            public void confirmBucket(String name) {
                client.deleteBucket(name, new DeleteBucketResponceHandler() {

                    @Override
                    public void onSuccess(int statesCode,
                                          Header[] responceHeaders) {
                        Intent intent = new Intent(MainActivity.this,
                                RESTAPITestResult.class);
                        Bundle data = new Bundle();
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer
                                .append("Delete bucket success , states code :"
                                        + statesCode);
                        data.putString(RESULT, stringBuffer.toString());
                        data.putString(API, "Delete Bucket Result");
                        intent.putExtras(data);
                        startActivity(intent);
                        Log.e("tag", "deleteBucket--onSuccess---" + stringBuffer.toString());

                    }

                    @Override
                    public void onFailure(int statesCode, Ks3Error error,
                                          Header[] responceHeaders, String response,
                                          Throwable paramThrowable) {
                        Intent intent = new Intent(MainActivity.this,
                                RESTAPITestResult.class);
                        Bundle data = new Bundle();
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer
                                .append("Delete bucket failed , states code :"
                                        + statesCode).append("/n").append("responce:").append(response);
                        data.putString(RESULT, stringBuffer.toString());
                        data.putString(API, "Delete Bucket Result");
                        intent.putExtras(data);
                        startActivity(intent);

                        Log.e("tag", "deleteBucket--onFailure---" + stringBuffer.toString());
                    }
                });
            }
        });
        bucketInpuDialog.show();
    }

    private void putObjectACL() {
        bucketObjectInpuDialog
                .setOnBucketObjectDialogListener(new OnBucketObjectDialogListener() {
                    @Override
                    public void confirmBucketAndObject(String name, String key) {
                        PutObjectACLRequest request = new PutObjectACLRequest(
                                name, key);
                        CannedAccessControlList cannedList = CannedAccessControlList.PublicRead;
                        // AccessControlList acList = new AccessControlList();

                        // GranteeId grantee = new GranteeId();
                        // grantee.setIdentifier("123456");
                        // grantee.setDisplayName("TESTTEST1");
                        // acList.addGrant(grantee, Permission.Read);
                        // GranteeId grantee1 = new GranteeId();
                        // grantee1.setIdentifier("1235789");
                        // grantee1.setDisplayName("TESTTEST1");
                        // acList.addGrant(grantee1, Permission.FullControl);
                        //
                        // request.setAccessControlList(acList);
                        request.setCannedAcl(cannedList);

                        client.putObjectACL(request,
                                new PutObjectACLResponseHandler() {

                                    @Override
                                    public void onSuccess(int statesCode,
                                                          Header[] responceHeaders) {
                                        StringBuffer stringBuffer = new StringBuffer();
                                        stringBuffer
                                                .append("Put Object ACL success , states code :"
                                                        + statesCode);
                                        Intent intent = new Intent(
                                                MainActivity.this,
                                                RESTAPITestResult.class);
                                        Bundle data = new Bundle();
                                        data.putString(RESULT,
                                                stringBuffer.toString());
                                        data.putString(API,
                                                "Put Object ACL Result");
                                        intent.putExtras(data);
                                        startActivity(intent);
                                        Log.e("tag", "putObjectACL--onSuccess---" + stringBuffer.toString());
                                    }

                                    @Override
                                    public void onFailure(int statesCode,
                                                          Ks3Error error,
                                                          Header[] responceHeaders,
                                                          String response,
                                                          Throwable paramThrowable) {
                                        StringBuffer stringBuffer = new StringBuffer();
                                        stringBuffer.append(
                                                "PUT Object ACL FAIL !!!!!!!!!, states code :"
                                                        + statesCode).append(
                                                "\n").append("responce:").append(response);
                                        stringBuffer.append("Exception :"
                                                + paramThrowable.toString());
                                        Intent intent = new Intent(
                                                MainActivity.this,
                                                RESTAPITestResult.class);
                                        Bundle data = new Bundle();
                                        data.putString(RESULT,
                                                stringBuffer.toString());
                                        data.putString(API,
                                                "PUT Object ACL Result");
                                        intent.putExtras(data);
                                        startActivity(intent);
                                        Log.e("tag", "putObjectACL--onFailure---" + stringBuffer.toString());
                                    }
                                });
                    }
                });
        bucketObjectInpuDialog.show();
    }

    private void headBucket() {
        bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
            @Override
            public void confirmBucket(String name) {
                client.headBucket(name, new HeadBucketResponseHandler() {
                    @Override
                    public void onSuccess(int statesCode,
                                          Header[] responceHeaders) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer
                                .append("head Bucket success , states code :"
                                        + statesCode);
                        Intent intent = new Intent(MainActivity.this,
                                RESTAPITestResult.class);
                        Bundle data = new Bundle();
                        data.putString(RESULT, stringBuffer.toString());
                        data.putString(API, "head Bucket Result");
                        intent.putExtras(data);
                        startActivity(intent);
                        Log.e("tag", "headBucket--onSuccess---" + stringBuffer.toString());

                    }

                    @Override
                    public void onFailure(int statesCode, Ks3Error error,
                                          Header[] responceHeaders, String response,
                                          Throwable paramThrowable) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(
                                "head Bucket Fail, states code :" + statesCode)
                                .append("\n").append("response:").append(response);
                        stringBuffer.append("Exception :"
                                + paramThrowable.toString());
                        Intent intent = new Intent(MainActivity.this,
                                RESTAPITestResult.class);
                        Bundle data = new Bundle();
                        data.putString(RESULT, stringBuffer.toString());
                        data.putString(API, "head Bucket Result");
                        intent.putExtras(data);
                        startActivity(intent);

                        Log.e("tag", "headBucket--onFailure---" + stringBuffer.toString());
                    }
                });
            }
        });
        bucketInpuDialog.show();
    }

    private void putBucketACL() {
        bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
            @Override
            public void confirmBucket(String name) {
                PutBucketACLRequest request = new PutBucketACLRequest(name);
                // AccessControlList acl = new AccessControlList();
                // // GranteeUri urigrantee = GranteeUri.AllUsers;
                // // Permission permission = Permission.Read;
                //
                // GranteeEmail email = new GranteeEmail();
                // email.setEmail("guoli@gmail.com");
                // Permission permission = Permission.Read;
                // Grant g = new Grant(email, permission);
                //
                // GranteeUri uirGroup = GranteeUri.AllUsers;
                // Permission uripermission = Permission.Read;
                // Grant g1 = new Grant(uirGroup, uripermission);

                // acl.addGrant(g);
                // acl.addGrant(g1);

                // GranteeId grantee = new GranteeId() ;
                // grantee.setIdentifier("12773456");
                // grantee.setDisplayName("guoliTest222");
                // acl.addGrant(grantee, Permission.Read);

                // GranteeId grantee1 = new GranteeId() ;
                // grantee1.setIdentifier("123005789");
                // grantee1.setDisplayName("guoliTest2D2");
                // acl.addGrant(grantee1, Permission.Write);

                // request.setAccessControlList(acl) ;

                CannedAccessControlList cannedAcl = CannedAccessControlList.PublicReadWrite;
                request.setCannedAcl(cannedAcl);
                // request.setAccessControlList(acl);
                client.putBucketACL(request, new PutBucketACLResponseHandler() {

                    @Override
                    public void onSuccess(int statesCode,
                                          Header[] responceHeaders) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer
                                .append("Put Bucket ACL success, states code :"
                                        + statesCode);
                        Intent intent = new Intent(MainActivity.this,
                                RESTAPITestResult.class);
                        Bundle data = new Bundle();
                        data.putString(RESULT, stringBuffer.toString());
                        data.putString(API, "Put Bucket ACL Result");
                        intent.putExtras(data);
                        startActivity(intent);

                        Log.e("tag", "putBucketACL--onSuccess---" + stringBuffer.toString());
                    }

                    @Override
                    public void onFailure(int statesCode, Ks3Error error,
                                          Header[] responceHeaders, String response,
                                          Throwable paramThrowable) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(
                                "PUT Bucket ACL FAIL, states code :"
                                        + statesCode).append("\n").append("responce :").append(response);
                        stringBuffer.append("Exception :"
                                + paramThrowable.toString());
                        Intent intent = new Intent(MainActivity.this,
                                RESTAPITestResult.class);
                        Bundle data = new Bundle();
                        data.putString(RESULT, stringBuffer.toString());
                        data.putString(API, "PUT Bucket ACL Result");
                        intent.putExtras(data);
                        startActivity(intent);

                        Log.e("tag", "putBucketACL--onFailure---" + stringBuffer.toString());
                    }
                });
            }
        });
        bucketInpuDialog.show();

    }

    private void getBucketACL() {
        bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
            @Override
            public void confirmBucket(String name) {
                client.getBucketACL(name, new GetBucketACLResponceHandler() {

                    @Override
                    public void onSuccess(int statesCode,
                                          Header[] responceHeaders,
                                          AccessControlPolicy accessControlPolicy) {
                        StringBuffer stringBuffer = new StringBuffer();
                        Owner owner = accessControlPolicy.getOwner();
                        stringBuffer.append(
                                "=======Owner : ID " + owner.getId()
                                        + " ; NAME :" + owner.getDisplayName())
                                .append("\n");
                        stringBuffer.append("==============ACL LIST=========");
                        HashSet<Grant> grants = accessControlPolicy
                                .getAccessControlList().getGrants();
                        for (Grant grant : grants) {
                            stringBuffer.append(
                                    grant.getGrantee().getIdentifier()
                                            + "========>"
                                            + grant.getPermission().toString())
                                    .append("\n");
                        }
                        Intent intent = new Intent(MainActivity.this,
                                RESTAPITestResult.class);
                        Bundle data = new Bundle();
                        data.putString(RESULT, stringBuffer.toString());
                        data.putString(API, "GET BUCKET ACL Result");
                        intent.putExtras(data);
                        startActivity(intent);

                        Log.e("tag", "getBucketACL--onSuccess---" + stringBuffer.toString());
                    }

                    @Override
                    public void onFailure(int statesCode, Ks3Error error,
                                          Header[] responceHeaders, String response,
                                          Throwable paramThrowable) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(
                                "GET BUCKET ACL fail , states code :"
                                        + statesCode).append("\n").append("response = ").append(response);
                        stringBuffer.append("Exception :"
                                + paramThrowable.toString());
                        Intent intent = new Intent(MainActivity.this,
                                RESTAPITestResult.class);
                        Bundle data = new Bundle();
                        data.putString(RESULT, stringBuffer.toString());
                        data.putString(API, "GET BUCKET ACL Result");
                        intent.putExtras(data);
                        startActivity(intent);

                        Log.e("tag", "getBucketACL--onFailure---" + stringBuffer.toString());
                    }
                });
            }
        });
        bucketInpuDialog.show();

    }

    private void deleteObject() {
        bucketObjectInpuDialog
                .setOnBucketObjectDialogListener(new OnBucketObjectDialogListener() {
                    @Override
                    public void confirmBucketAndObject(String name, String key) {
                        DeleteObjectRequest request = new DeleteObjectRequest(
                                name, key);
                        client.deleteObject(request,
                                new DeleteObjectRequestHandler() {

                                    @Override
                                    public void onSuccess(int statesCode,
                                                          Header[] responceHeaders) {
                                        StringBuffer stringBuffer = new StringBuffer();
                                        stringBuffer
                                                .append("Delete success , states code :"
                                                        + statesCode);
                                        Intent intent = new Intent(
                                                MainActivity.this,
                                                RESTAPITestResult.class);
                                        Bundle data = new Bundle();
                                        data.putString(RESULT,
                                                stringBuffer.toString());
                                        data.putString(API,
                                                "Delete Object Result");
                                        intent.putExtras(data);
                                        startActivity(intent);
                                        Log.e("tag", "deleteObject--onSuccess---" + stringBuffer.toString());
                                    }

                                    @Override
                                    public void onFailure(int statesCode,
                                                          Ks3Error error,
                                                          Header[] responceHeaders,
                                                          String response,
                                                          Throwable paramThrowable) {
                                        StringBuffer stringBuffer = new StringBuffer();
                                        stringBuffer.append(
                                                "Delete fail , states code :"
                                                        + statesCode).append(
                                                "\n").append("response:").append(response);
                                        stringBuffer.append("Exception :"
                                                + paramThrowable.toString());
                                        Intent intent = new Intent(
                                                MainActivity.this,
                                                RESTAPITestResult.class);
                                        Bundle data = new Bundle();
                                        data.putString(RESULT,
                                                stringBuffer.toString());
                                        data.putString(API,
                                                "Delete Object Result");
                                        intent.putExtras(data);
                                        startActivity(intent);
                                        Log.e("tag", "deleteObject--onFailure---" + stringBuffer.toString());
                                    }
                                });
                    }
                });
        bucketObjectInpuDialog.show();

    }

    private void listObjects() {
        bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
            @Override
            public void confirmBucket(String name) {
                ListObjectsRequest request = new ListObjectsRequest(name);
                // request.setPrefix("android_test/");
                // request.setDelimiter("/");
                client.listObjects(request, new ListObjectsResponseHandler() {

                    @Override
                    public void onSuccess(int statesCode,
                                          Header[] responceHeaders,
                                          ObjectListing objectListing) {
                        //StirngBuffer过长 oppo会报：android oppo msg's executing time is too longcopy
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(
                                "name   =    " + objectListing.getBucketName())
                                .append("\n");
                        stringBuffer.append(
                                "Prefix =    " + objectListing.getPrefix())
                                .append("\n");
                        stringBuffer.append(
                                "Marker =    " + objectListing.getMarker())
                                .append("\n");
                        stringBuffer.append(
                                "Delimiter = " + objectListing.getDelimiter())
                                .append("\n");
                        stringBuffer.append(
                                "IsTruncated = " + objectListing.isTruncated())
                                .append("\n");
                        List<Ks3ObjectSummary> objectSummaries = objectListing
                                .getObjectSummaries();
                        Ks3ObjectSummary objectSummary = null;
                        Owner owner = null;
                        for (int i = 0; i < objectSummaries.size(); i++) {
                            objectSummary = objectSummaries.get(i);
                            owner = objectSummary.getOwner();
                            stringBuffer.append(
                                    "================Object :" + i
                                            + " ===================").append(
                                    "\n");
                            stringBuffer.append(
                                    "     key             ="
                                            + objectSummary.getKey()).append(
                                    "\n");
//                            stringBuffer.append(
//                                    "     LastModified    ="
//                                            + objectSummary.getLastModified())
//                                    .append("\n");
//                            stringBuffer.append(
//                                    "     ETag   =" + objectSummary.getETag())
//                                    .append("\n");
                            stringBuffer.append(
                                    "     Size    =" + objectSummary.getSize())
                                    .append("\n");
                            stringBuffer.append(
                                    "     owner.ID    =" + owner.getId())
                                    .append("\n");
//                            stringBuffer.append(
//                                    "     Size.displayName    ="
//                                            + owner.getDisplayName()).append(
//                                    "\n");
//                            stringBuffer.append(
//                                    "     StorageClass    = "
//                                            + objectSummary.getStorageClass())
//                                    .append("\n");
                        }

                        List<String> commonPrefixes = objectListing
                                .getCommonPrefixes();
                        for (int i = 0; i < commonPrefixes.size(); i++) {
                            stringBuffer.append(
                                    "     commonPrefixes =>" + i + "="
                                            + objectSummary.getStorageClass())
                                    .append("\n");
                        }
                        Intent intent = new Intent(MainActivity.this,
                                RESTAPITestResult.class);
                        Bundle data = new Bundle();
                        data.putString(RESULT, stringBuffer.toString());
                        data.putString(API, "RESULT for ListObjects");
                        intent.putExtras(data);
                        startActivity(intent);

                        Log.e("tag", "listObjects--onSuccess---" + stringBuffer.toString());
                    }

                    @Override
                    public void onFailure(int statesCode, Ks3Error error,
                                          Header[] responceHeaders, String response,
                                          Throwable paramThrowable) {
                        // TODO Auto-generated method stub
                        Log.e("tag", "listObjects--onFailure---" + statesCode);
                    }
                });
            }
        });
        bucketInpuDialog.show();

    }

    private void listBuckets() {
        client.listBuckets(new ListBucketsResponceHandler() {
            @Override
            public void onSuccess(int paramInt, Header[] paramArrayOfHeader,
                                  ArrayList<Bucket> resultList) {
                StringBuffer stringBuffer = new StringBuffer();
                for (Bucket bucket : resultList) {
                    stringBuffer.append(bucket.getName()).append("\n");
//                    stringBuffer.append(bucket.getCreationDate()).append("\n");
//                    stringBuffer.append(bucket.getOwner().getDisplayName())
//                            .append("\n");
//                    stringBuffer.append(bucket.getOwner().getId()).append("\n");
                }
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, stringBuffer.toString());
                data.putString(API, "List Bucket Result");
                intent.putExtras(data);
                startActivity(intent);

                Log.e("tag", "listBuckets--onSuccess:" + stringBuffer.toString());
            }

            @Override
            public void onFailure(int statesCode, Ks3Error error,
                                  Header[] responceHeaders, String response,
                                  Throwable paramThrowable) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(
                        "list bucket fail , states code :" + statesCode)
                        .append("\n").append("responce :").append(response);
                stringBuffer.append("Exception :" + paramThrowable.toString());
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, stringBuffer.toString());
                data.putString(API, "List Buckets");
                intent.putExtras(data);
                startActivity(intent);

                Log.e("tag", "listBuckets--onFailure:" + stringBuffer.toString());
            }
        });
    }

    private void createBucket() {
        bucketInpuDialog.setOnBucketInputListener(new OnBucketDialogListener() {
            @Override
            public void confirmBucket(String name) {
                client.createBucket(name, new CreateBucketResponceHandler() {
                    @Override
                    public void onSuccess(int statesCode,
                                          Header[] responceHeaders) {
                        Intent intent = new Intent(MainActivity.this,
                                RESTAPITestResult.class);
                        Bundle data = new Bundle();
                        data.putString(RESULT, "success");
                        data.putString(API, "Create Bucket Result");
                        intent.putExtras(data);
                        startActivity(intent);

                        Log.e("tag", "createBucket--onSuccess---" + "statesCode:" + statesCode);
                    }

                    @Override
                    public void onFailure(int statesCode, Ks3Error error,
                                          Header[] responceHeaders, String response,
                                          Throwable paramThrowable) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append(
                                "Delete fail , states code :" + statesCode)
                                .append("\n").append("responce :").append(response);
                        stringBuffer.append("Exception :"
                                + paramThrowable.toString());
                        Intent intent = new Intent(MainActivity.this,
                                RESTAPITestResult.class);
                        Bundle data = new Bundle();
                        data.putString(RESULT, stringBuffer.toString());
                        data.putString(API, "List Buckets");
                        intent.putExtras(data);
                        startActivity(intent);

                        Log.e("tag", "createBucket--onFailure:" + stringBuffer.toString());
                    }
                });
            }
        });
        bucketInpuDialog.show();
    }

    private void putBucketCrr() {

        //设置规则
        ReplicationRule rule = new ReplicationRule();
        List<String> prefixList = new ArrayList<String>();
        prefixList.add("test");
        rule.setPrefixList(prefixList);
        rule.setTargetBucket("qichao-bja");
        rule.setRegion("BJA");
        rule.setDeleteMarkerStatus(false);

        client.putBucketCrr(new PutBucketReplicationConfigRequest("cqc-test-b", rule), new PutBucketReplicationResponceHandler() {
            @Override
            public void onSuccess(int statesCode,
                                  Header[] responceHeaders) {
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, "success");
                data.putString(API, "PutBucketCRR  Result");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "PutBucketCRR--onSuccess---" + "statesCode:" + statesCode);
                //  deleteBucketCrr();
            }

            @Override
            public void onFailure(int statesCode, Ks3Error error,
                                  Header[] responceHeaders, String response,
                                  Throwable paramThrowable) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(
                        "putBucketCrr fail , states code :" + statesCode)
                        .append("\n").append("responce :").append(response);
                stringBuffer.append("Exception :"
                        + paramThrowable.toString());
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, stringBuffer.toString());
                data.putString(API, "PutBucketCRR");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "PutBucketCRR--onFailure:" + stringBuffer.toString());
            }
        });
    }

    private void getBucketCrr() {

        //获取规则
        client.getBucketCrr(new GetBucketReplicationConfigRequest("jiangrantest"), new GetBucketReplicationConfigResponceHandler() {
            @Override
            public void onFailure(int statesCode, Ks3Error error,
                                  Header[] responceHeaders, String response,
                                  Throwable paramThrowable) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(
                        "getBucketCrr fail , states code :" + statesCode)
                        .append("\n").append("responce :").append(response);
                stringBuffer.append("Exception :"
                        + paramThrowable.toString());
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, stringBuffer.toString());
                data.putString(API, "getBucketCRR");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "getBucketCRR--onFailure:" + stringBuffer.toString());
            }

            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, ReplicationRule replicationRule) {
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, "success" + "/n" + replicationRule.toString());
                data.putString(API, "getBucketCRR  Result");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "getBucketCRR--onSuccess---" + "statesCode:" + statesCode);
            }
        });
    }

    /**
     * 删除跨区域复制规则
     */
    private void deleteBucketCrr() {

        //获取规则
        client.deleteBucketCrr(new DeleteBucketReplicationConfigRequest("jiangrantest"), new DeleteBucketReplicationConfigResponceHandler() {
            @Override
            public void onFailure(int statesCode, Ks3Error error,
                                  Header[] responceHeaders, String response,
                                  Throwable paramThrowable) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(
                        "deleteBucketCrr fail , states code :" + statesCode)
                        .append("\n").append("responce :").append(response);
                stringBuffer.append("Exception :"
                        + paramThrowable.toString());
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, stringBuffer.toString());
                data.putString(API, "deleteBucketCrr");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "deleteBucketCrr--onFailure:" + stringBuffer.toString());
            }

            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders) {

                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(API, "deleteBucketCrr  Result");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "deleteBucketCrr--onSuccess---" + "statesCode:" + statesCode);
            }
        });
    }

    /**
     * 设置空间策略
     */
    private void putBucketPolicy() {

        BucketPolicyRule policyRule = new BucketPolicyRule()
                .addAllAction()
                .addPrincipalByAccountId("2000090561")
                .addPrincipalByAccountIdAndUserName("123123", "123123")
                .addBucketResource("jiangrantest")
                .addConditionSouceIp("11.11.11.11", true)
                .addSourceHeader("Connection: keep-alivE", BucketPolicyConditionRule.StringLike)
                .addSourceHeader("Connection: keep-alivE123", BucketPolicyConditionRule.StringEquals)
                .setEffect("Allow");

        PutBuckePolicyRequest putBuckePolicyRequest = new PutBuckePolicyRequest("jiangrantest", policyRule);
        client.putBucketPolicy(putBuckePolicyRequest, new Ks3HttpResponceHandler() {
            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, byte[] response) {
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, "success");
                data.putString(API, "putBucketPolicy  Result");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "putBucketPolicy--onSuccess---" + "statesCode:" + statesCode);
            }

            @Override
            public void onFailure(int statesCode, Header[] responceHeaders, byte[] response, Throwable throwable) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(
                        "putBucketPolicy fail , states code :" + statesCode)
                        .append("\n").append("responce :").append(response);
                stringBuffer.append("Exception :"
                        + throwable.toString());
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, stringBuffer.toString());
                data.putString(API, "putBucketPolicy");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "putBucketPolicy--onFailure:" + stringBuffer.toString());
            }
        });
    }

    /**
     * 获取空间策略
     */
    private void getBucketPolicy() {

        GetBucketPolicyRequest request = new GetBucketPolicyRequest("uptools2");
        client.getBucketPolicy(request, new GetBucketPolicyResponceHandler() {
            @Override
            public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(
                        "getBucketPolicy fail , states code :" + statesCode)
                        .append("\n").append("responce :").append(response);
                stringBuffer.append("Exception :"
                        + paramThrowable.toString());
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, stringBuffer.toString());
                data.putString(API, "getBucketPolicy");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "getBucketPolicy--onFailure:" + stringBuffer.toString());
            }

            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, String policy) {
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, "success ! policy is : " + policy);
                data.putString(API, "getBucketPolicy  Result");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "getBucketPolicy--onSuccess---" + "statesCode:" + statesCode);
            }
        });
    }

    /**
     * 删除空间策略
     */
    private void deleteBucketPolicy() {

        DeleteBucketPolicyRequest request = new DeleteBucketPolicyRequest("uptools2");
        client.deleteBucketPolicy(request, new Ks3HttpResponceHandler() {
            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, byte[] response) {
                System.out.println("onSuccess  statesCode is " + statesCode);
            }

            @Override
            public void onFailure(int statesCode, Header[] responceHeaders, byte[] response, Throwable throwable) {
                System.out.println("onFailure  statesCode is " + statesCode);
            }

        });
    }

    /**
     * 设置桶配额
     */
    private void putBucketQuota() {

        //请求内容
        BucketQuota quota = new BucketQuota(1000000);

        PutBuckeQuotaRequest quotaRequest = new PutBuckeQuotaRequest("chenqichen", quota);

        client.putBucketQuota(quotaRequest, new Ks3HttpResponceHandler() {
            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, byte[] response) {
                System.out.println("onSuccess " + new String(response));
            }

            @Override
            public void onFailure(int statesCode, Header[] responceHeaders, byte[] response, Throwable throwable) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(
                        "putBucketQuota fail , states code :" + statesCode)
                        .append("\n").append("responce :").append(response);
                stringBuffer.append("Exception :"
                        + throwable.toString());
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, stringBuffer.toString());
                data.putString(API, "putBucketQuota");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "putBucketQuota--onFailure:" + stringBuffer.toString());
            }
        });
    }

    /**
     * 获取桶配额
     */
    private void getBucketQuota() {

        GetBucketQuotaRequest quotaRequest = new GetBucketQuotaRequest("chenqichen");

        client.getBucketQuota(quotaRequest, new GetBucketQuotaResponceHandler() {
            @Override
            public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(
                        "getBucketQuota fail , states code :" + statesCode)
                        .append("\n").append("responce :").append(response);
                stringBuffer.append("Exception :"
                        + paramThrowable.toString());
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, stringBuffer.toString());
                data.putString(API, "getBucketQuota");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "getBucketQuota--onFailure:" + stringBuffer.toString());
            }

            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, BucketQuota quota) {
                System.out.println("onSuccess storageQuota  is " + quota.getStorageQuota());
            }
        });
    }

    /**
     * 音视频处理
     */
    public void testPutAndQueryAdp() {

        String srcObjectKey = "test/file1.mp4";
        String newObjectKey = "new/Upload3.mp4";
        //音视频处理
        Adp avop = new Adp();
        avop.setBucket(DST_BUCKETNAME);
        avop.setCommand("tag=avop&f=mp4&res=1080x720&vbr=1000k&abr=64k");
        avop.setKey(newObjectKey);

        //视频截图
        Adp avscrnshot = new Adp();
        avscrnshot.setBucket(DST_BUCKETNAME);
        avscrnshot.setCommand("tag=avscrnshot&ss=10&res=640x360&rotate=90");
        avscrnshot.setKey(newObjectKey);

        //视频采样截图
        Adp avsample = new Adp();
        avsample.setBucket(DST_BUCKETNAME);
        avsample.setCommand("tag=avsample&ss=5&t=30&res=640x360&rotate=90&interval=5&pattern=5oiq5Zu+LSUzZC5qcGc=");
        avsample.setKey(newObjectKey);

        //音视频切片
        Adp avm3u8 = new Adp();
        avm3u8.setBucket(DST_BUCKETNAME);
        avm3u8.setCommand("tag=avm3u8&segtime=10&abr=128k&vbr=1000k&res=1280x720");
        avm3u8.setKey(newObjectKey);

        //视频拼接
        Adp avconcat = new Adp();
        avconcat.setBucket(DST_BUCKETNAME);
        avconcat.setCommand("tag=avconcat&f=mp4&mode=1&file=" + com.ksyun.ks3.util.Base64.encode("test/file2.mp4".getBytes()));
        avconcat.setKey(newObjectKey);

        PutAdpRequest adpRequest = new PutAdpRequest(SRC_BUCKETNAME, srcObjectKey, Arrays.asList(avconcat));
        adpRequest.setNotifyURL("http://127.0.0.1:9000/notify/url");

        //音视频元数据获取
//        GetAdpRequest getAdpRequest = new GetAdpRequest("taskId");
//        client.getAdpTask(getAdpRequest, new GetObjectAdpResponceHandler() {
//            @Override
//            public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
//                StringBuffer stringBuffer = new StringBuffer();
//                stringBuffer.append(
//                        "getAdpRequest fail , states code :" + statesCode)
//                        .append("\n").append("responce :").append(response);
//                stringBuffer.append("Exception :"
//                        + paramThrowable.toString());
//                Intent intent = new Intent(MainActivity.this,
//                        RESTAPITestResult.class);
//                Bundle data = new Bundle();
//                data.putString(RESULT, stringBuffer.toString());
//                data.putString(API, "getAdpRequest");
//                intent.putExtras(data);
//                startActivity(intent);
//                Log.e("tag", "getAdpRequest--onFailure:" + stringBuffer.toString());
//            }
//
//            @Override
//            public void onSuccess(int statesCode, Header[] responceHeaders, AdpTask adpTask) {
//                System.out.println("getAdpTask is " + adpTask.toString());
//            }
//        });

        //发送请求
        client.putAdpTask(adpRequest, new PutObjectAdpResponceHandler() {
            @Override
            public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(
                        "putAdpTask fail , states code :" + statesCode)
                        .append("\n").append("responce :").append(response);
                stringBuffer.append("Exception :"
                        + paramThrowable.toString());
                Intent intent = new Intent(MainActivity.this,
                        RESTAPITestResult.class);
                Bundle data = new Bundle();
                data.putString(RESULT, stringBuffer.toString());
                data.putString(API, "putAdpTask");
                intent.putExtras(data);
                startActivity(intent);
                Log.e("tag", "putAdpTask--onFailure:" + stringBuffer.toString());
            }

            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, PutAdpResult adpResult) {
                System.out.println("taskId is " + adpResult.getTaskId());
            }
        });
    }

    public void postObject() {

        final String srcObjectKey = "OnlineTest/sdk/demo/KS3SDKDemo.zip";
        final File file = new File(TEST_MULTIUPLOAD_FILE);
        Map<String, String> postData = new HashMap<String, String>();
        postData.put("acl", "public-read");
        postData.put("key", "20150115/中文/${filename}");
        List<String> unknowValueField = new ArrayList<String>();
        unknowValueField.add("name");
        PostObjectFormFields fields = client.getObjectFormFields(SRC_BUCKETNAME, file.getName(), postData, (Map<String, String>) unknowValueField);
        fields.getKssAccessKeyId();
        fields.getPolicy();
        fields.getSignature();
        String uploadUrl = END_POINT;
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + file.getName()
                    + "\"" + end);
            dos.writeBytes(end);
            //将SD 文件通过输入流读到Java代码中-++++++++++++++++++++++++++++++`````````````````````````
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);

            }
            fis.close();
            System.out.println("file send to server............");
            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            //读取服务器返回结果
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();

            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            dos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
            setTitle(e.getMessage());
        }

    }

    public void putObjectFetch() {

        final String srcObjectKey = "OnlineTest/sdk/demo/KS3SDKDemo.zip";
        String sourceUrl = "";
        PutObjectFetchRequest putObjectFetchRequest = new PutObjectFetchRequest(SRC_BUCKETNAME, srcObjectKey, sourceUrl);
        client.putObjectFetch(putObjectFetchRequest, new PutObjectFetchResponseHandler() {
            @Override
            public void onTaskFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
                System.out.println("fail putObjectFetch is " + response);
            }

            @Override
            public void onTaskSuccess(int statesCode, Header[] responceHeaders, PutObjectFetchResult result) {
                System.out.println("success putObjectFetch is " + result.toString());
            }
        });

    }

    /**
     * put object tag
     */
    public void testPutObjTag() {

        //文档参考 -> https://docs.ksyun.com/documents/949
        ObjectTagging objectTagging = new ObjectTagging();
        objectTagging.addObjectTag("tagA", "A");

        PutObjectTaggingRequest taggingRequest = new PutObjectTaggingRequest(SRC_BUCKETNAME, SRC_OBJECTKEY, objectTagging);
        client.putObjectTag(taggingRequest, new Ks3HttpResponceHandler() {
            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, byte[] response) {

                System.out.println("success putObjectTaggingResponse is " + response.toString());
                GetObjectTaggingRequest taggingRequest = new GetObjectTaggingRequest(SRC_BUCKETNAME, SRC_OBJECTKEY);
                client.getObjectTag(taggingRequest, new GetObjectTaggingResponseHandler() {
                    @Override
                    public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
                        System.out.println("fail getObjectTaggingResponse is " + response.toString());
                    }

                    @Override
                    public void onSuccess(int statesCode, Header[] responceHeaders, ObjectTagging tagging) {
                        System.out.println("success getObjectTaggingResponse is " + tagging.toString());
                    }
                });
            }

            @Override
            public void onFailure(int statesCode, Header[] responceHeaders, byte[] response, Throwable throwable) {
                System.out.println("fail putObjectTaggingResponse is " + new String(response));
            }
        });

        DeleteObjectTaggingRequest deleteObjectTaggingRequest = new DeleteObjectTaggingRequest(SRC_BUCKETNAME, SRC_OBJECTKEY);
        client.deleteObjectTag(deleteObjectTaggingRequest, new HeadBucketResponseHandler() {
            @Override
            public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
                System.out.println("fail DeleteObjectTaggingRequest is " + new String(response));
            }

            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders) {
                System.out.println("onSuccess DeleteObjectTaggingRequest");
            }
        });
    }

    /**
     * testPutFetchObj
     */
    public void testPutFetchObj() {

        ObjectTagging objectTagging = new ObjectTagging();
        objectTagging.addObjectTag("tagA", "A");

        PutObjectFetchRequest putObjectFetchRequest = new PutObjectFetchRequest(SRC_BUCKETNAME, "zzzz", "http://ks3tools-online.ks3-cn-beijing.ksyun.com/tools/release/ks3up-tool-2.1.1-dist.zip", objectTagging);
        putObjectFetchRequest.setCallBack("https://open.feishu.cn/open-apis/bot/v2/hook/704de274-447f-400f-a634-075df20fd1ba", putObjectFetchRequest.getCallBackBody(), putObjectFetchRequest.getHeader());
        client.putObjectFetch(putObjectFetchRequest, new Ks3HttpResponceHandler() {
            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, byte[] response) {

                System.out.println("success putObjectFetchResponse is " + new String(response));
            }

            @Override
            public void onFailure(int statesCode, Header[] responceHeaders, byte[] response, Throwable throwable) {
                System.out.println("fail putObjectFetchResponse is " + new String(response));
            }
        });
    }


    /**
     * putBucketVersion
     */
    public void testVersion() {

        // getBucketVersion();
        // putBucketVersion();
        //listObjectVersion();
        getObjectVersion();
    }

    /**
     * putBucketVersion
     */
    public void putBucketVersion() {

     //  getBucketVersion();
        BucketVersioningConfiguration bucketVersioningConfiguration = new BucketVersioningConfiguration();
        bucketVersioningConfiguration.setStatus(ENABLED);
        PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketVersioningConfiguration);
        putBucketVersioningRequest.setBucketname(SRC_BUCKETNAME);
        client.putBucketVersion(putBucketVersioningRequest, new Ks3HttpResponceHandler() {
            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, byte[] response) {
                System.out.println("success putBucketVersionResponse");
            }
            @Override
            public void onFailure(int statesCode, Header[] responceHeaders, byte[] response, Throwable throwable) {
                System.out.println("fail putBucketVersionResponse is " + response);
            }
        });
    }
    /**
     * getBucketVersion
     */
    public void getBucketVersion() {

        GetBucketVersioningRequest getBucketVersioningRequest = new GetBucketVersioningRequest();
        getBucketVersioningRequest.setBucketname(SRC_BUCKETNAME);
        client.getBucketVersion(getBucketVersioningRequest, new GetBucketVersioningHandler() {

            @Override
            public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
                System.out.println("fail getBucketVersionResponse is " + response);
            }

            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, BucketVersioningConfiguration versioningConfiguration) {
                System.out.println("success getBucketVersionResponse is " + versioningConfiguration.toString());
            }
        });
    }

    /**
     * listObjectVersion
     */
    public void listObjectVersion() {

        ListObjectVersionsRequest listObjectVersionsRequest = new ListObjectVersionsRequest(SRC_BUCKETNAME);
        client.listObjectVersions(listObjectVersionsRequest, new ListObjectsVersionResponseHandler() {

            @Override
            public void onFailure(int statesCode, Ks3Error error, Header[] responceHeaders, String response, Throwable paramThrowable) {
                System.out.println("fail getBucketVersionResponse is " + response);
            }

            @Override
            public void onSuccess(int statesCode, Header[] responceHeaders, ObjecVersiontListing objectListing) {
                System.out.println("success getObjectSummaries size is " + objectListing.getObjectSummaries().size());
            }
        });
    }

    /**
     * getobject-version
     */
    public void getObjectVersion() {

        GetObjectRequest getObjectRequest = new GetObjectRequest(SRC_BUCKETNAME,SRC_OBJECTKEY,"Kvi7y10K0T8gMdolCfUjy9IZNSFdjYnQAz/n6jewSk4=");
        client.getObject(getObjectRequest, new GetObjectResponseHandler(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),SRC_OBJECTKEY),
                SRC_BUCKETNAME, SRC_OBJECTKEY) {
            @Override
            public void onTaskProgress(double progress) {

            }

            @Override
            public void onTaskStart() {

            }

            @Override
            public void onTaskFinish() {

            }

            @Override
            public void onTaskCancel() {

            }

            @Override
            public void onTaskSuccess(int paramInt, Header[] paramArrayOfHeader, GetObjectResult getObjectResult) {
                System.out.println("OK getObjectResult is " + getObjectResult.getObject().getContentETag());
            }

            @Override
            public void onTaskFailure(int paramInt, Ks3Error error, Header[] paramArrayOfHeader, Throwable paramThrowable, File paramFile) {
                System.out.println("fail getObjectResult is " + error);
            }

        });
    }
}
