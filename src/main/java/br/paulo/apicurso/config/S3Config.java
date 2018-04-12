package br.paulo.apicurso.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;

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
			.withRegion(Regions.US_EAST_1)
			.build();
		
		if (!amazonS3.doesBucketExistV2(this.property.getS3().getBucket())) {
			amazonS3.createBucket(new CreateBucketRequest(this.property.getS3().getBucket()));
			
			BucketLifecycleConfiguration.Rule regraExpiracao = new BucketLifecycleConfiguration.Rule()
				.withId("Regra de expiração de arquivos temporários")
				.withFilter(new LifecycleFilter(new LifecycleTagPredicate(new Tag("expirar", "true"))))
				.withExpirationInDays(1)
				.withStatus(BucketLifecycleConfiguration.ENABLED);
			
			BucketLifecycleConfiguration configuracao = new BucketLifecycleConfiguration()
				.withRules(regraExpiracao);
			
			amazonS3.setBucketLifecycleConfiguration(this.property.getS3().getBucket(), configuracao);
		}
		
		return amazonS3;
	}

}
