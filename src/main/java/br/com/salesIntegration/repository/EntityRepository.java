package br.com.salesIntegration.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.salesIntegration.commons.BusinessException;
import br.com.salesIntegration.commons.CommonBundle;

/**
 * Classe que contem as opercoes basicas com com banco de dados utilizando JPA.
 * @param <T> objeto serializado
 * @param <I> tipo do id do objeto serializado
 */
public class EntityRepository<T extends BaseEntity, I extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@PersistenceContext
	protected EntityManager entityManager;

	/**
	 * Metodo para salvar a entidade
	 * @param entity entidade a ser salva
	 * @return entidade salva
	 * @throws BusinessException excecao de banco
	 */
	public T save(T entity) throws BusinessException {
		if (entity == null) {
			throw new IllegalArgumentException("Erro ao executar o save: entidade é nula");
		}

		try {
			this.entityManager.persist(entity);
			this.entityManager.flush();
			return entity;
		} catch (PersistenceException e) {
			throw new BusinessException(CommonBundle.GLOBAL, "mensagem.inclusao.erro", e, new Object[]{entity.getClass().getName()});
		}
	}

	/**
	 * Metodo para salvar uma lista de uma entidade
	 * @param entities lista de entidades a serem salvas
	 * @return lista de entidades salvas
	 * @throws BusinessException excecao de banco
	 */
	public List<T> saveAll(List<T> entities) throws BusinessException {
		for (T entity : entities) {
			this.save(entity);
		}
		return entities;
	}

	/**
	 * Metodo para fazer o update de uma entidade
	 * @param entity entidade a ser realizado o update
	 * @return entidade 
	 * @throws BusinessException excecao de banco
	 */
	public T update(T entity) throws BusinessException {
		if (entity == null) {
			throw new IllegalArgumentException("Erro ao executar o update: entidade é nula");
		}
		try {
			entity = this.entityManager.merge(entity);
			this.entityManager.flush();
			return entity;
		} catch (PersistenceException e) {
			throw new BusinessException(CommonBundle.GLOBAL, "mensagem.edicao.erro", e, new Object[]{entity.getClass().getName()});
		}
	}

	/**
	 * Metodo para realizar o update de uma lista de uma entidade
	 * @param entities lista de entidades a serem realizados os updates
	 * @return lista de entidades
	 * @throws BusinessException excecao de banco
	 */
	public List<T> updateAll(List<T> entities) throws BusinessException {
		for (T entity : entities) {
			this.update(entity);
		}
		return entities;
	}

	/**
	 * Metodo para deletar uma entidade
	 * @param entity entidade a ser deletada
	 * @throws BusinessException excecao de banco
	 */
	public void delete(T entity) throws BusinessException {
		if (entity == null) {
			throw new IllegalArgumentException("Erro ao executar o delete: entidade é nula");
		}
		try {
			this.entityManager.remove(entityManager.merge(entity));
			this.entityManager.flush();
		} catch (PersistenceException e) {
			throw new BusinessException(CommonBundle.GLOBAL, "mensagem.excluir.erro", e, new Object[]{entity.getClass().getName()});
		}
	}

	/**
	 * Metodo para deletar uma lista de uma entidade
	 * @param entities lista da entidade a serem deletadas
	 * @throws BusinessException excecao de banco
	 */
	public void deleteAll(List<T> entities) throws BusinessException {
		for (T entity : entities) {
			this.delete(entity);
		}
	}

	/**
	 * Metodo para deletar uma lista de uma entidade
	 * @param namedQuery namedQuery do delete
	 * @throws BusinessException excecao caso a namedQuery seja nula ou vazia, ou excecao de banco
	 */
	public void deleteAll(String namedQuery) throws BusinessException {
		if (namedQuery == null || namedQuery.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar o deleteAll: namedQuery vazio ou nulo");
		}
		try {
			Query query = this.entityManager.createNamedQuery(namedQuery);
			query.executeUpdate();
		} catch (PersistenceException e) {
			throw new BusinessException(CommonBundle.GLOBAL, "mensagem.excluir.erro", e, new Object[]{});
		}
	}

	/**
	 * Metodo para carregar uma entidade de acordo com o id passado por parametro
	 * @param classe classe da entidade
	 * @param id id para realizar a pesquisa
	 * @return entidade carregada a partir do id passado por parametro
	 */
	public T loadById(Class<T> classe, I id) {
		if (classe == null || id == null) {
			throw new IllegalArgumentException("Erro ao executar o loadById: entidade ou ID nulo");
		}
		T entity = this.entityManager.find(classe, id);
		this.entityManager.flush();
		return entity;
	}

	/**
	 * Metodo para carregar uma entidade de acordo com a namedQuery e os parametros passados
	 * @param namedQuery namedQuery para a pesquisa
	 * @param parametros parametros da pesquisa
	 * @return entidade carregada de acordo com os parametros
	 * @throws BusinessException excecao caso namedQuery seja nula ou vazia, caso parametros seja nulo ou vazio, 
	 * caso nao tenha resultados, ou excecao de banco
	 */
	@SuppressWarnings("unchecked")
	public T loadByParameters(String namedQuery, Map<String, Object> parametros) throws BusinessException {
		if (namedQuery == null || namedQuery.isEmpty() || parametros == null || parametros.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar o loadByParameters: namedQuery ou parâmetros, vazio ou nulo");
		}
		try {
			Query query = this.entityManager.createNamedQuery(namedQuery);
			addQueryParameters(query, parametros);
			T entity = (T) query.getSingleResult();
			entityManager.flush();
			return entity;
		} catch (Exception e) {
			throw new BusinessException(CommonBundle.GLOBAL, "mensagem.erroCarregarEntidade", e);
		}
	}

	/**
	 * Carrega entidade de acordo com a criteriaQuery e os parametros passados
	 * @param criteriaQuery criteriaQuery para a pesquisa
	 * @param parametros parametros a serem utilizados na pesquisa
	 * @return entidade carregada de acordo com os parametros
	 * @throws BusinessException excecao caso o criteriaQuery seja nulo, caso parametros seja nulo ou vazio,
	 * caso a pesquisa nao tenha resultados, ou excecao de banco
	 */
	public T loadByParameters(CriteriaQuery<T> criteriaQuery, Map<String, Object> parametros) throws BusinessException {
		if (criteriaQuery == null || parametros == null || parametros.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar o loadByParameters: criteriaQuery ou parâmetros, vazio ou nulo");
		}

		try {
			TypedQuery<T> query = this.entityManager.createQuery(criteriaQuery);
			addQueryParameters(query, parametros);
			T entity = (T) query.getSingleResult();
			entityManager.flush();
			return entity;
		} catch (NoResultException | NonUniqueResultException e) {
			throw new BusinessException(CommonBundle.GLOBAL, "mensagem.erroCarregarEntidade", e);
		}
	}

	/**
	 * Metodo para carregar todos os registros de uma entidade a partir de uma namedQuery passada por parametro
	 * @param namedQuery namedQuery a ser utilizada na pesquisa
	 * @return lista da entidade carregada a partir da namedQuery
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAll(String namedQuery) {
		if (namedQuery == null || namedQuery.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar findAll: namedQuery, vazia ou nula");
		}
		Query query = this.entityManager.createNamedQuery(namedQuery);
		List<T> entities = query.getResultList();
		entityManager.flush();
		return entities;
	}

	/**
	 * Metodo para carregar todos os registros de uma entidade a partir de uma criteriaQuery passada por parametro
	 * @param criteriaQuery criteriaQuery a ser utilizada na pesquisa
	 * @return lista da entidade carregada a partir da criteriaQuery
	 */
	public List<T> findAll(CriteriaQuery<T> criteriaQuery) {
		if (criteriaQuery == null) {
			throw new IllegalArgumentException("Erro ao executar o findAll: criteriaQuery ou parâmetros, vazio ou nulo");
		}
		TypedQuery<T> query = this.entityManager.createQuery(criteriaQuery);
		List<T> entities = query.getResultList();
		entityManager.flush();
		return entities;
	}
	
	/**
	 * Metodo para carregar todos os registros de uma entidade a partir do class passado por parametro
	 * @param classe Classe que sera realizada a pesquisa
	 * @return lista da entidade carregada a partir da classe passada por parametro
	 */
	public List<T> findAll(Class<T> classe) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(classe);
		query.from(classe);
		
		return this.entityManager.createQuery(query).getResultList();
	}

	/**
	 * Carrega a lista paginada de acordo com a namedQuery passada por parametro e o numero de itens por pagina
	 * @param namedQuery namedQuery a ser utilizada na pesquisa
	 * @param paginaAtual pagina atual
	 * @param itensPorPagina itens por pagina
	 * @return lista carregada de acordo com a namedQuery passada por parametro
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAllWithPagination(String namedQuery, Integer paginaAtual, Integer itensPorPagina) {
		if (namedQuery == null || namedQuery.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar findAllWithPagination: namedQuery, vazia ou nula");
		}
		Query query = this.entityManager.createNamedQuery(namedQuery);
		addQueryPagination(query, paginaAtual, itensPorPagina);
		List<T> entities = query.getResultList();
		entityManager.flush();
		return entities;
	}

	/**
	 * Carrega a lista paginada de acordo com a criteriaQuery passada por parametro e o numero de itens por pagina
	 * @param criteriaQuery criteriaQuery a ser utilizada na pesquisa
	 * @param paginaAtual pagina atual
	 * @param itensPorPagina itens por pagina
	 * @return lista carregada de acordo com a criteriaQuery passada por parametro
	 */
	public List<T> findAllWithPagination(CriteriaQuery<T> criteriaQuery, Integer paginaAtual, Integer itensPorPagina) {
		if (criteriaQuery == null) {
			throw new IllegalArgumentException("Erro ao executar findAllWithPagination: criteriaQuery, vazia ou nula");
		}
		TypedQuery<T> query = this.entityManager.createQuery(criteriaQuery);
		addQueryPagination(query, paginaAtual, itensPorPagina);
		return query.getResultList();
	}
	
	/**
	 * Metodo para carregar todos os registros paginados de uma entidade a partir do class passado por parametro
	 * @param classe Classe que sera realizada a pesquisa
	 * @param paginaAtual Pagina atual
	 * @param itensPorPagina Itens por pagina
	 * @return lista da entidade carregada a partir da classe passada por parametro
	 */
	public List<T> findAllWithPagination(Class<T> classe, Integer paginaAtual, Integer itensPorPagina) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(classe);
		query.from(classe);
		
		TypedQuery<T> typedQuery = this.entityManager.createQuery(query);
		addQueryPagination(typedQuery, paginaAtual, itensPorPagina);
		return typedQuery.getResultList();
	}

	/**
	 * Metodo para carregar a lista de uma entidade de acordo com a namedQuery e os parametros passados
	 * @param namedQuery namedQuery a ser utilizado a pesquisa
	 * @param parametros parametros a serem utilizados na pesquisa
	 * @return lista da entidade carregada de acordo com os parametros
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByParameters(String namedQuery, Map<String, Object> parametros) {
		if (namedQuery == null || namedQuery.isEmpty() || parametros == null || parametros.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar o findByParameters: namedQuery ou parâmetros, vazio ou nulo");
		}
		Query query = this.entityManager.createNamedQuery(namedQuery);
		addQueryParameters(query, parametros);
		List<T> entities = query.getResultList();
		entityManager.flush();
		return entities;
	}

	/**
	 * Metodo para carregar a lista de uma entidade de acordo com a criteriaQuery e os parametros passados
	 * @param criteriaQuery criteriaQuery a ser utilizado a pesquisa
	 * @param parametros parametros a serem utilizados na pesquisa
	 * @return lista da entidade carregada de acordo com os parametros
	 */
	public List<T> findByParameters(CriteriaQuery<T> criteriaQuery, Map<String, Object> parametros) {
		if (criteriaQuery == null || parametros == null || parametros.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar o findByParameters: criteriaQuery ou parâmetros, vazio ou nulo");
		}
		TypedQuery<T> query = this.entityManager.createQuery(criteriaQuery);
		addQueryParameters(query, parametros);
		List<T> entities = query.getResultList();
		entityManager.flush();
		return entities;
	}

	/**
	 * Metodo para carregar uma lista paginada de uma entidade de acordo com a namedQuery e os parametros passados
	 * @param namedQuery namedQuery a ser utilizada na pesquisa
	 * @param parametros parametros a serem utilizados na pesquisa
	 * @param paginaAtual pagina atual
	 * @param itensPorPagina itens por pagina
	 * @return lista da entidade carregada a partir dos parametros passados
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByParametersWithPagination(String namedQuery, Map<String, Object> parametros, Integer paginaAtual, Integer itensPorPagina) {
		if (namedQuery == null || namedQuery.isEmpty() || parametros == null || parametros.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar o findByParametersWithPagination: namedQuery ou parâmetros, vazio ou nulo");
		}
		Query query = this.entityManager.createNamedQuery(namedQuery);
		addQueryParameters(query, parametros);
		addQueryPagination(query, paginaAtual, itensPorPagina);
		List<T> entities = query.getResultList();
		entityManager.flush();
		return entities;
	}
	
	/**
	 * Metodo para carregar uma lista paginada de uma entidade de acordo com a criteriaQuery passada por parametro 
	 * @param criteriaQuery criteriaQuery a ser utilizada na pesquisa
	 * @param parametros parametros a serem utilizados na pesquisa
	 * @param paginaAtual pagina atual
	 * @param itensPorPagina itens por pagina
	 * @return lista da entidade carregada a partir dos parametros passados
	 */
	public List<T> findByParametersWithPagination(CriteriaQuery<T> criteriaQuery, Map<String, Object> parametros, Integer paginaAtual, Integer itensPorPagina) {
		if (criteriaQuery == null || parametros == null || parametros.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar o findByParametersWithPagination: criteriaQuery ou parâmetros, vazio ou nulo");
		}
		TypedQuery<T> query = this.entityManager.createQuery(criteriaQuery);
		addQueryParameters(query, parametros);
		addQueryPagination(query, paginaAtual, itensPorPagina);
		List<T> entities = query.getResultList();
		entityManager.flush();
		return entities;
	}

	/**
	 * Metodo para carregar uma lista paginada de uma entidade de acordo com a nativeQuery passada por parametro 
	 * @param query query com a consulta desejada
	 * @param paginaAtual pagina atual
	 * @param itensPorPagina itens por pagina
	 * @return lista da entidade carregada a partir dos parametros passados
	 */
	public List<T> findObjectsUsingNativeQuery(TypedQuery<T> query, Integer paginaAtual, Integer itensPorPagina) {
		if (query == null) {
			throw new IllegalArgumentException("Erro ao executar o findByParametersWithPagination: query nula");
		}
		addQueryPagination(query, paginaAtual, itensPorPagina);
		List<T> entities = query.getResultList();
		entityManager.flush();
		return entities;
	}

	/**
	 * Metodo para contar todos os registros de uma entidade de acordo com a namedQuery passada por parametro
	 * @param namedQuery namedQuery a ser utilizada na pesquisa
	 * @return numero de registros encontrados na pesquisa
	 * @throws BusinessException excecao caso namedQuery seja nula ou vazia, caso a pesquisa nao tenha resultado, excecao de banco
	 */
	public Long countAll(String namedQuery) throws BusinessException {
		if (namedQuery == null || namedQuery.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar o countAll: namedQuery, vazia ou nula");
		}

		try {
			Query query = this.entityManager.createNamedQuery(namedQuery);
			Long count = (Long) query.getSingleResult();
			entityManager.flush();
			return count;
		} catch (NoResultException | NonUniqueResultException e) {
			throw new BusinessException(CommonBundle.GLOBAL, "mensagem.erroCarregarContador", e);
		}
	}

	/**
	 * Metodo para contar todos os registros de uma entidade de acordo com a criteriaQuery passada por parametro
	 * @param criteriaQuery criteriaQuery a ser utilizada na pesquisa
	 * @return numero de registros encontrados na pesquisa
	 * @throws BusinessException excecao caso criteriaQuery seja nula ou vazia, caso a pesquisa nao tenha resultado, excecao de banco
	 */
	public Long countAll(CriteriaQuery criteriaQuery) throws BusinessException {
		if (criteriaQuery == null) {
			throw new IllegalArgumentException("Erro ao executar o countAll: criteriaQuery nulo");
		}
		try {
			Query query = this.entityManager.createQuery(criteriaQuery);
			Long count = (Long) query.getSingleResult();
			entityManager.flush();
			return count;
		} catch (NoResultException | NonUniqueResultException e) {
			throw new BusinessException(CommonBundle.GLOBAL, "mensagem.erroCarregarContador", e);
		}
	}
	
	/**
	 * Metodo para contar todos os registros de uma entidade de acordo com a classe passada por parametro
	 * @param classe Classe que sera utilizada na pesquisa
	 * @return numero de registros encontrados na pesquisa
	 */
	public Long countAll(Class<T> classe) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		query.select(builder.count(query.from(classe)));
		
		return this.entityManager.createQuery(query).getSingleResult();
	}

	/**
	 * Metodo para contar todos os registros de uma entidade de acordo com a namedQuery e os parametros passados
	 * @param namedQuery namedQuery a ser utilizada na pesquisa
	 * @param parametros parametros a serem utilizados na pesquisa
	 * @return numero de registros encontrados na pesquisa
	 * @throws BusinessException excecao caso namedQuery seja nulo ou vazia, caso a pesquisa nao tenha resultado, excecao de banco
	 */
	public Long countByParameters(String namedQuery, Map<String, Object> parametros) throws BusinessException {
		if (namedQuery == null || namedQuery.isEmpty() || parametros == null || parametros.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar o countByParameters: namedQuery ou parâmetros, vazio ou nulo");
		}
		try {
			Query query = this.entityManager.createNamedQuery(namedQuery);
			addQueryParameters(query, parametros);
			Long count = (Long) query.getSingleResult();
			entityManager.flush();
			return count;
		} catch (NoResultException | NonUniqueResultException e) {
			throw new BusinessException(CommonBundle.GLOBAL, "mensagem.erroCarregarContador", e);
		}
	}

	/**
	 * Metodo para contar o numero de registros de acordo com a criteriaQuery e os parametros passados
	 * @param criteriaQuery criteriaQuery a ser utilizada na pesquisa
	 * @param parametros parametros a serem utilizados na pesquisa
	 * @param root root da consulta
	 * @param cb criteriaBuilder
	 * @return numero de registros de acordo com os parametros passados
	 * @throws BusinessException excecao caso a criteriaQuery seja nula, caso parametros seja nulo ou vazio,
	 * caso a pesquisa nao tenha resultado, ou uma excecao de banco
	 */
	public Long countByParameters(CriteriaQuery criteriaQuery, Map<String, Object> parametros, Root root, CriteriaBuilder cb) throws BusinessException {
		if (criteriaQuery == null || parametros == null || parametros.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar o countByParameters: criteriaQuery ou parametros, vazio ou nulo");
		}

		try {
			List<Predicate> filters = addQueryPredications(parametros, root, cb);
			criteriaQuery.where(filters.toArray(new Predicate[filters.size()]));

			Query query = this.entityManager.createQuery(criteriaQuery);
			
			Long count = (Long) query.getSingleResult();
			entityManager.flush();
			return count;
		} catch (NoResultException | NonUniqueResultException e) {
			throw new BusinessException(CommonBundle.GLOBAL, "mensagem.erroCarregarContador", e);
		}
	}
	
	/**
	 * Metodo para contar o numero de registros de uma entidade de acordo com o criteriaQuery e os parametros passados
	 * @param criteriaQuery criteriaQuery a ser utilizado na pesquisa
	 * @param parametros parametros a serem utilizados na pesquisa
	 * @return numero de registros encontrados de acordo com os parametros passados
	 * @throws BusinessException excecao caso a criteriaQuery seja nula, caso parametros seja nulo ou vazio,
	 * caso a pesquisa nao tenha resultado, ou uma excecao de banco
	 */
	public Long countByParameters(CriteriaQuery criteriaQuery, Map<String, Object> parametros) throws BusinessException {
		if (criteriaQuery == null || parametros == null || parametros.isEmpty()) {
			throw new IllegalArgumentException("Erro ao executar o countByParameters: criteriaQuery ou parametros, vazio ou nulo");
		}

		try {
			Query query = this.entityManager.createQuery(criteriaQuery);
			addQueryParameters(query, parametros);
			Long count = (Long) query.getSingleResult();
			entityManager.flush();
			return count;
		} catch (NoResultException | NonUniqueResultException e) {
			throw new BusinessException(CommonBundle.GLOBAL, "mensagem.erroCarregarContador", e);
		}
	}
	
	/**
	 * Adiciona os parametros de selecao na Query
	 * @param query query a ser adicionado os parametros
	 * @param parametros parametros a serem adicionados na query
	 */
	private void addQueryParameters(Query query, Map<String, Object> parametros) {
		if (parametros != null && !parametros.isEmpty()) {
			for (Map.Entry<String, Object> entry : parametros.entrySet()) {
				query = query.setParameter(entry.getKey(), entry.getValue());
			}
		}
	}
	
	/**
	 * Adiciona os parametros de selecao na Query
	 * @param parametros parametros a serem adicionados na query
	 * @param root root type
	 * @param cb query a ser adicionado os parametros
	 * @return lista com os parametros adicionados
	 */
	private List<Predicate> addQueryPredications(Map<String, Object> parametros, Root<T> root, CriteriaBuilder cb) {
		List<Predicate> filters = new ArrayList<>();

		if (parametros != null && !parametros.isEmpty()) {
			for (Map.Entry<String, Object> entry : parametros.entrySet()) {
				Expression<String> attribute = root.get(entry.getKey());
				filters.add(cb.like(cb.upper(attribute), "%" + entry.getValue().toString().toUpperCase() + "%"));
			}
		}
		
		return filters;
	}

	/**
	 * Adiciona o recurso de paginacao na Query. caso for nulo sera retornado uma lista com tamanho maximo 
	 * conforme o valor do parametro itensPorPagina. Caso for nulo sera retornado uma lista apartir da 
	 * posicao informada no parametro paginaAtual. Se ambos forem igual a nulo sera lançada uma excecao, caso contrario sera realizada a paginacao.
	 * @param query query a ser adicionado o recurso
	 * @param paginaAtual pagina atual
	 * @param itensPorPagina itens por pagina
	 */
	private void addQueryPagination(Query query, Integer paginaAtual, Integer itensPorPagina) {
		if (paginaAtual == null && itensPorPagina == null) {
			throw new IllegalArgumentException("Os parâmetros da paginação são nulos, utilize o método findAll");
		} else {
			if (paginaAtual != null) {
				query.setFirstResult(paginaAtual);
			}
			if (itensPorPagina != null) {
				query.setMaxResults(itensPorPagina);
			}
		}
	}

}