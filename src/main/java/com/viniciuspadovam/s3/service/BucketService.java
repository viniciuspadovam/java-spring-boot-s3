package com.viniciuspadovam.s3.service;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.viniciuspadovam.s3.constants.FileValidFormats;
import com.viniciuspadovam.s3.dto.BucketResponse;
import com.viniciuspadovam.s3.dto.ObjectResponse;
import com.viniciuspadovam.s3.dto.UploadObjectRequest;
import com.viniciuspadovam.s3.exception.InvalidImageFormatException;
import com.viniciuspadovam.s3.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
@RequiredArgsConstructor
@Slf4j
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

		return response.contents().stream()
			.map(obj -> 
				new ObjectResponse(obj.key(), obj.lastModified().toString()))
			.toList();
	}
	
	public String uploadImage(UploadObjectRequest data) {
		var filePathSplit = data.filePath().split("/");
		String fileName = filePathSplit[filePathSplit.length - 1];
		
		if(!isValidImageFormat(fileName))
			throw new InvalidImageFormatException();
		
		try {
			PutObjectRequest request = PutObjectRequest.builder()
					.bucket(data.bucketName())
					.key(fileName)
					.build();
			PutObjectResponse response = s3Client.putObject(request, new File(data.filePath()).toPath());
			return response.eTag();
		} catch(AwsServiceException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}
	
	private boolean isValidImageFormat(String fileName) {
		String extension = fileName.split("\\.")[1];
		return FileValidFormats.validImageFormats.contains(extension);
	}
	
	public InputStream downloadImage(String bucketName, String filePath) {
		try {
			GetObjectRequest request = GetObjectRequest.builder()
					.bucket(bucketName)
					.key(filePath)
					.build();
			return s3Client.getObject(request);			
		} catch(AwsServiceException aws) {
			log.error("[downloadImage] error: \n" + aws.getMessage());
			log.warn(aws.awsErrorDetails().toString());
			throw new ResponseStatusException(aws.statusCode(), aws.getMessage(), new RuntimeException());
		}
	}
	
	public String createBucket(String bucketName) {
		try {
			CreateBucketResponse result = s3Client.createBucket(request -> request.bucket(bucketName));
			return result.location();
		} catch(AwsServiceException e) {
			log.error(e.getMessage());
			log.warn(e.awsErrorDetails().toString());
			throw new ResponseStatusException(e.statusCode(), e.getMessage(), new RuntimeException());
		}
	}
	
	public void deleteBucket(String bucketName) {
		try {
			s3Client.deleteBucket(request -> request.bucket(bucketName));
		} catch(AwsServiceException e) {
			log.error(e.getMessage());
			log.warn(e.awsErrorDetails().toString());
			throw new ResponseStatusException(e.statusCode(), e.getMessage(), new RuntimeException());
		}
	}
	
}
