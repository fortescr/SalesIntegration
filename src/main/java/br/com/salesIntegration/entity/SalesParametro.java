package br.com.salesIntegration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.salesIntegration.repository.BaseEntity;

@Entity
@Table(name = "SF_PARAMETROS")
public class SalesParametro extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "sf_param_seq", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sf_param_seq", sequenceName = "sf_param_seq", allocationSize = 1)
	@Column(name = "ID_PARAMETRO")
	private Long id;
	
	@Column(name = "URL_TOKEN")
	private String urlToken;
	
	@Column(name = "URL_REST")
	private String urlRest;
	
	@Column(name = "CLIENT_ID")
	private String clientId;
	
	@Column(name = "CLIENT_SECRET")
	private String clientSecret;
	
	@Column(name = "USERNAME")
	private String username;
	
	@Column(name = "PASSWORD")
	private String password;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrlToken() {
		return urlToken;
	}

	public void setUrlToken(String urlToken) {
		this.urlToken = urlToken;
	}

	public String getUrlRest() {
		return urlRest;
	}

	public void setUrlRest(String urlRest) {
		this.urlRest = urlRest;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
