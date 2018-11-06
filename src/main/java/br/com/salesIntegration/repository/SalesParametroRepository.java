package br.com.salesIntegration.repository;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import br.com.salesIntegration.entity.SalesParametro;

@Named
public class SalesParametroRepository extends EntityRepository<SalesParametro, Long>{

	private static final long serialVersionUID = 1L;

	public SalesParametro findByName(String nome) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select s from SalesParametro s ");
		sql.append(" where nome = :nome ");
		TypedQuery<SalesParametro> query = entityManager.createQuery(sql.toString(), SalesParametro.class);
		query.setParameter("nome", nome);
		query.setFirstResult(0);
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}
	
	public SalesParametro find() {
		StringBuilder sql = new StringBuilder();
		sql.append(" select s from SalesParametro s ");
		TypedQuery<SalesParametro> query = entityManager.createQuery(sql.toString(), SalesParametro.class);
		query.setFirstResult(0);
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}
	
	
}
