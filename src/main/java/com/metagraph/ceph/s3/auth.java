package com.metagraph.ceph.s3;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.PropertiesFileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.StringUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;


/**
 * Created by johnny on 8/2/17.
 */

public class auth {
    public static void main(String args[]) {
        String access_key = "9HQ1ZH2SQGXBEYALVH9M";
        String secret_key = "eRbj2Rz5OxfO4NI1HNh4uGjIbaqsozO5ZA9oD2JP";

        AWSCredentials credentials = new BasicAWSCredentials(access_key, secret_key);
        ClientConfiguration clientconf = new ClientConfiguration();
        clientconf.setProtocol(Protocol.HTTP);
        AmazonS3 conn = new AmazonS3Client(credentials, clientconf);
        conn.setEndpoint("192.168.199.156");
        System.out.println("success...");
//        conn.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());

        Bucket test = conn.createBucket("my-new-bucket");
        ByteArrayInputStream input = new ByteArrayInputStream("Hello World!".getBytes());
        conn.putObject(test.getName(), "hello.txt", input, new ObjectMetadata());
        TransferManager tm = new TransferManager(new ProfileCredentialsProvider());
        Upload upload = tm.upload("my-new-bucket", "xml", new File("/Users/johnny/Downloads/mononoki.zip") );
        try {
            upload.waitForCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ObjectListing objects = conn.listObjects(test.getName());
        do {
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                System.out.println(objectSummary.getKey() + "\t" +
                        objectSummary.getSize() + "\t" +
                        StringUtils.fromDate(objectSummary.getLastModified()));
            }
            objects = conn.listNextBatchOfObjects(objects);
        } while (objects.isTruncated());
//
        List<Bucket> buckets = conn.listBuckets();
        for(Bucket bucket : buckets) {
            System.out.println(bucket.getName() + "\t" +
                    StringUtils.fromDate(bucket.getCreationDate()));
        }
    }
}
