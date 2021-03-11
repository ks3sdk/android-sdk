package com.ksyun.ks3.util;

import java.util.Arrays;
import java.util.List;

public class RequestUtils {
	public static List<String> subResource = Arrays.asList(new String[]{"acl", "lifecycle", "location", "crr",
			"logging", "notification", "partNumber", "tagging", "fetch",
            "policy", "requestPayment", "torrent", "uploadId", "uploads", "versionId","quota","queryadp","adp",
            "versioning", "versions", "website", "delete", "thumbnail"});
	
	public static List<String> QueryParam = Arrays.asList(new String[]{"response-content-type",
		"response-content-language",
        "response-expires", "response-cache-control",
        "response-content-disposition", "response-content-encoding", 
        "width", "height"});
}
