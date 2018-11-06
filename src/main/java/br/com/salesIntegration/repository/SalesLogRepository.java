package br.com.salesIntegration.repository;

import javax.inject.Named;

import br.com.salesIntegration.entity.SalesLog;

@Named
public class SalesLogRepository extends EntityRepository<SalesLog, Long>{

	private static final long serialVersionUID = 1L;	
	
}
