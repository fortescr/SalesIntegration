package br.com.salesIntegration.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.salesIntegration.repository.BaseEntity;

@Entity
@Table(name = "SF_LOG_REST")
public class SalesLog extends BaseEntity{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "sf_log_seq", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "sf_log_seq", sequenceName = "sf_log_seq", allocationSize = 1)
	@Column(name = "ID_LOG")
	
	private Long id;
	
	@Column(name = "DESCRICAO")
	private String descricao;
	
	@Column(name = "MENSAGEM")
	private String mensagem;
	
	@Column(name = "DATA")
	private Date data;
	
	@Column(name = "TABELA")
	private String tabela;
	
	@Column(name = "COD_RETORNO")
	private String codigoRetorno;
	
	@Column(name = "ID_SF")
	private String idSf;
	
	@Column(name = "ERRORCODE")
	private String errorCode;
	
	@Column(name = "METHOD")
	private String method;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getTabela() {
		return tabela;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}

	public String getCodigoRetorno() {
		return codigoRetorno;
	}

	public void setCodigoRetorno(String codigoRetorno) {
		this.codigoRetorno = codigoRetorno;
	}

	public String getIdSf() {
		return idSf;
	}

	public void setIdSf(String idSf) {
		this.idSf = idSf;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}
