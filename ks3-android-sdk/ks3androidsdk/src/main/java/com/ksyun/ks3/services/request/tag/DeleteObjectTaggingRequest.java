package com.ksyun.ks3.services.request.tag;

import com.ksyun.ks3.exception.Ks3ClientException;
import com.ksyun.ks3.model.HttpMethod;
import com.ksyun.ks3.services.request.common.Ks3HttpObjectRequest;
import com.ksyun.ks3.util.StringUtils;

public class DeleteObjectTaggingRequest extends Ks3HttpObjectRequest {

    public DeleteObjectTaggingRequest(String bucketName, String objectName) {
        setBucketname(bucketName);
        setObjectkey(objectName);
    }
    public DeleteObjectTaggingRequest(String bucketName, String objectName, String versionId) {
        setBucketname(bucketName);
        setObjectkey(objectName);
        setVersionId(versionId);
    }

    @Override
    protected void setupRequest() throws Ks3ClientException {
        this.setHttpMethod(HttpMethod.DELETE);
        this.addParams("tagging", "");
        super.setupRequest();
    }

    @Override
    protected String validateParams() throws Ks3ClientException {
        if (StringUtils.validateBucketName(this.getBucketname()) == null)
            throw new Ks3ClientException("bucket name is not correct");

        if (StringUtils.isBlank(this.getObjectkey())) {
            throw new Ks3ClientException("object key can not be null");
        }
        return null;
    }

}
