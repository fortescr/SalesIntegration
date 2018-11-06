package br.com.salesIntegration.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe de json genérica para retorno
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JsonReturn implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "success")
	private String success;
	
	@XmlElement(name = "id")
	private String id;
	
	@XmlElement(name = "error")
	private String error;

	
	public JsonReturn(String success, String id, String error) {
		super();
		this.success = success;
		this.id = id;
		this.error = error;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
