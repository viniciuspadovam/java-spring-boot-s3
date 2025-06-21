package com.viniciuspadovam.s3.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.viniciuspadovam.s3.dto.BucketResponse;
import com.viniciuspadovam.s3.dto.ObjectResponse;
import com.viniciuspadovam.s3.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

@Service
@RequiredArgsConstructor
public class BucketService {
	
	private final S3Client s3Client;

	public String getBucket(String bucketName) {
		ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
		Optional<Bucket> hasBucket = listBucketsResponse
				.buckets().stream()
				.filter(b -> b.name().equals(bucketName))
				.findFirst();
		
		if(hasBucket.isEmpty()) throw new ResourceNotFoundException("Bucket not found.");
		
		Bucket bucket = hasBucket.get();
		return "Encontrado bucket \"" + bucket.name() + 
				"\" criado em " + bucket.creationDate() + ".";
		
	}
	
	public List<BucketResponse> listBuckets() {
	    return s3Client.listBuckets().buckets().stream()
	            .map(bucket -> 
	            	new BucketResponse(bucket.name(), bucket.creationDate().toString()))
	            .toList();
	}
	
	public List<ObjectResponse> getObjectsFromBucket(String bucketName) {
		ListObjectsV2Request request = ListObjectsV2Request.builder()
			.bucket(bucketName)
			.build();
		ListObjectsV2Response response = s3Client.listObjectsV2(request);
		System.out.println(response);
		return response.contents().stream()
			.map(obj -> 
				new ObjectResponse(obj.key(), obj.lastModified().toString()))
			.toList();
	}
	
}
