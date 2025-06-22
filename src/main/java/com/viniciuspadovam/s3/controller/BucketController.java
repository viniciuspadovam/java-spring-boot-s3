package com.viniciuspadovam.s3.controller;

import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viniciuspadovam.s3.dto.BucketResponse;
import com.viniciuspadovam.s3.dto.ObjectResponse;
import com.viniciuspadovam.s3.dto.UploadObjectRequest;
import com.viniciuspadovam.s3.service.BucketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BucketController {
	
	private final BucketService bucketService;

	@GetMapping("/{bucketName}")
	public ResponseEntity<String> getBucket(@PathVariable String bucketName) {
		String bucket = bucketService.getBucket(bucketName);
		return ResponseEntity.ok(bucket);
	}
	
	@GetMapping("/buckets")
	public ResponseEntity<List<BucketResponse>> listOfBuckets() {
		return ResponseEntity.ok(bucketService.listBuckets());
	}
	
	@GetMapping("/{bucketName}/objects")
	public ResponseEntity<List<ObjectResponse>> getObjectsFromBucket(@PathVariable String bucketName) {
		List<ObjectResponse> objects = bucketService.getObjectsFromBucket(bucketName);
		return ResponseEntity.ok(objects);
	}
	
	@PostMapping("/upload-image")
	public ResponseEntity<String> uploadImage(@RequestBody UploadObjectRequest data) {
		String createdObjectTag = bucketService.uploadImage(data);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdObjectTag);
	}
	
	@GetMapping("/download")
	public ResponseEntity<InputStreamResource> downloadObject(
		@RequestParam String bucketName, 
		@RequestParam String filePath
	) {
		var s3Object = bucketService.downloadImage(bucketName, filePath);
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath + "\"")
			.body(new InputStreamResource(s3Object));
	}
	
	@PostMapping("/create-bucket")
	public ResponseEntity<String> createBucket(@RequestParam String bucketName) {
		var bucketLocation = bucketService.createBucket(bucketName);
		return ResponseEntity.status(HttpStatus.CREATED).body(bucketLocation);
	}
	
	@DeleteMapping()
	public ResponseEntity<String> deleteBucket(@RequestParam String bucketName) {
		bucketService.deleteBucket(bucketName);
		return ResponseEntity.noContent().build();
	}
	
}
