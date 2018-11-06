package br.com.salesIntegration.rest.util;

import java.io.Serializable;

import javax.faces.bean.ApplicationScoped;

@ApplicationScoped
public class TokenSalesforce implements Serializable {

	private static final long serialVersionUID = 1L;

	private  String salesforce;

	public  String getSalesforce() {
		return salesforce;
	}

	public  void setSalesforce(String salesforce) {
		this.salesforce = salesforce;
	}

}