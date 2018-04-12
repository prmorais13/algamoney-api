package br.paulo.apicurso.storage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;

import br.paulo.apicurso.config.property.ApiCursoProperty;

@Component
public class S3 {
	
	private static final Logger logger = LoggerFactory.getLogger(S3.class);
	
	@Autowired
	private ApiCursoProperty property;
	
	@Autowired
	private AmazonS3 amazonS3;
	
	public String salvarTemporario(MultipartFile arquivo) {
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		
		ObjectMetadata objectMetada = new ObjectMetadata();
		objectMetada.setContentType(arquivo.getContentType());
		objectMetada.setContentLength(arquivo.getSize());
		
		String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename());
		
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(
				this.property.getS3().getBucket(),
				nomeUnico,
				arquivo.getInputStream(),
				objectMetada).withAccessControlList(acl);
			
			putObjectRequest.setTagging(new ObjectTagging(Arrays.asList(
				new Tag("expirar", "true"))));
			
			amazonS3.putObject(putObjectRequest);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Arquivo {} enviado como sucesso para o S3.", arquivo.getOriginalFilename());
			}
			
			return nomeUnico;
			
		} catch (IOException e) {
			throw new RuntimeException("Erro ao enviar arquivo para o S3.", e);
		}

	}
	
	public String configurarUrl(String obj) {
		return "\\\\" + this.property.getS3().getBucket() + ".s3.amazonaws.com/" + obj;
	}

	public void salvar(String obj) {
		SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
			this.property.getS3().getBucket(),
			obj,
			new ObjectTagging(Collections.emptyList()));
		
		this.amazonS3.setObjectTagging(setObjectTaggingRequest);

	}
	
	public void remover(String obj) {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
				this.property.getS3().getBucket(),
				obj);
		
		this.amazonS3.deleteObject(deleteObjectRequest);
	}
	
	public void substituir(String objAntigo, String objNovo) {
		if (StringUtils.hasText(objAntigo)) {
			this.remover(objAntigo);
		}
		
		this.salvar(objNovo);
	}
	
	private String gerarNomeUnico(String originalFilename) {
		return UUID.randomUUID().toString() + "_" + originalFilename;
	}





}
