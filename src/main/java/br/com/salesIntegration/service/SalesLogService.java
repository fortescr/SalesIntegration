package br.com.salesIntegration.service;

import java.io.Serializable;

import javax.ejb.Stateless;

import br.com.salesIntegration.commons.BusinessException;
import br.com.salesIntegration.entity.SalesLog;

@Stateless
public class SalesLogService implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private SalesLogService caSalesLogService = new SalesLogService();
	
	public SalesLog saveOrUpdate(SalesLog salesLog) throws BusinessException{
		return caSalesLogService.saveOrUpdate(salesLog);		
	}
	
}
