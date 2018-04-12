package br.paulo.apicurso.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;

import br.paulo.apicurso.config.property.ApiCursoProperty;

@Configuration
public class S3Config {
	
	@Autowired
	private ApiCursoProperty property;
	
	@Bean
	public AmazonS3 amazonS3() {
		
		AWSCredentials credenciais = new BasicAWSCredentials(
			this.property.getS3().getAccessKeyId(),
			this.property.getS3().getSecretAccessKey());
		
		AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credenciais))
			.build();
		
		if (!amazonS3.doesBucketExistV2(this.property.getS3().getBucket())) {
			amazonS3.createBucket(new CreateBucketRequest(this.property.getS3().getBucket()));
		}
		
		return amazonS3;
	}

}
