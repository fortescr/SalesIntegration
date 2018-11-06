package br.com.salesIntegration.service;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.salesIntegration.commons.BusinessException;
import br.com.salesIntegration.entity.SalesParametro;
import br.com.salesIntegration.repository.SalesParametroRepository;

@Stateless
public class SalesParametroService implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private SalesParametroRepository salesParametroRepository;	
	
	public SalesParametro findByName(String nome) throws BusinessException{
		return salesParametroRepository.findByName(nome);
	}
	
	public SalesParametro find() throws BusinessException{
		return salesParametroRepository.find();
	}	
}
