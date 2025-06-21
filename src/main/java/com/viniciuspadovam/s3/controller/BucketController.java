package com.viniciuspadovam.s3.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.viniciuspadovam.s3.dto.BucketResponse;
import com.viniciuspadovam.s3.dto.ObjectResponse;
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
	
}
