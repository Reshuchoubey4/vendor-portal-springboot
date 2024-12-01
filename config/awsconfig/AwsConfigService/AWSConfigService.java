package com.application.main.config.awsconfig.AwsConfigService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleWithWebIdentityResult;
import com.application.main.config.awsconfig.AWSConfig;

@Service
public class AWSConfigService implements AWSConfig {

	@Value("${minio.rest-url}")
	private String baseUrl;

	@Value("${minio.rest-port}")
	private String port;

	@Override
	public BasicSessionCredentials client(String token) {
		System.err.println(baseUrl + ":" + port);
		AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
				.withEndpointConfiguration(
						new AwsClientBuilder.EndpointConfiguration(baseUrl + ":" + port, Regions.US_EAST_1.getName()))
				.withCredentials(
						(AWSCredentialsProvider) new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
				.build();
		AssumeRoleWithWebIdentityRequest assumeRequest = new AssumeRoleWithWebIdentityRequest()
				.withDurationSeconds(3600).withWebIdentityToken(token);
		AssumeRoleWithWebIdentityResult assumeResult = stsClient.assumeRoleWithWebIdentity(assumeRequest);

		BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
				assumeResult.getCredentials().getAccessKeyId(), assumeResult.getCredentials().getSecretAccessKey(),
				assumeResult.getCredentials().getSessionToken());
		return sessionCredentials;
	}

}