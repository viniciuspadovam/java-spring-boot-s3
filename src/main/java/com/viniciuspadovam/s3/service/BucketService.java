package com.viniciuspadovam.s3.service;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.viniciuspadovam.s3.constants.FileValidFormats;
import com.viniciuspadovam.s3.dto.BucketResponse;
import com.viniciuspadovam.s3.dto.ObjectResponse;
import com.viniciuspadovam.s3.dto.UploadObjectRequest;
import com.viniciuspadovam.s3.exception.InvalidImageFormatException;
import com.viniciuspadovam.s3.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

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
		} catch(Exception e) {
			System.err.println(e.getMessage());
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}
	
	private boolean isValidImageFormat(String fileName) {
		String extension = fileName.split("\\.")[1];
		return FileValidFormats.validImageFormats.contains(extension);
	}
	
}
