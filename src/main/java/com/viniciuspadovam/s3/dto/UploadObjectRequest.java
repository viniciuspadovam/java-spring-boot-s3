package com.viniciuspadovam.s3.dto;

public record UploadObjectRequest(
	String bucketName,
	String filePath
) {}
