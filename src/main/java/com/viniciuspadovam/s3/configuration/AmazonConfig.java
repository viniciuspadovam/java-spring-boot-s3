package com.viniciuspadovam.s3.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AmazonConfig {
	
	@Value("${s3.accesskey}")
	private String ACCESS_KEY;
	@Value("${s3.secretkey}")
	private String SECRET_KEY;
	@Value("${s3.region}")
	private String REGION;

	@Bean
	S3Client s3Client() {
		return S3Client.builder()
			.region(Region.of(REGION))
			.credentialsProvider(credentials())
			.build();
	}
	
	private StaticCredentialsProvider credentials() {
		return StaticCredentialsProvider.create(
			AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)
        );
	}
	
}
