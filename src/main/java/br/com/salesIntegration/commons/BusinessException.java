package br.com.salesIntegration.commons;

/**
 * Classe responsavel pela captura das excecoes e logar na API de logs do Java.
 */
public class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;
	/**
	 * Atributo para criacao de logs
	 */
	private Bundle bundle;
	private String key;
	private Object[] parameters;
	private String message;

	/**
	 * Gera uma BusinessException com uma mensagem customizada apartir dos
	 * bundles do sistema.
	 * @param bundle bundle com a mensagem
	 * @param key chave da mensagem no bundle
	 * @param throwable excecao a ser lancada
	 * @param parameters parametros
	 */
	public BusinessException(Bundle bundle, String key, Throwable throwable, Object... parameters) {
		this.bundle = bundle;
		this.key = key;
		this.parameters = parameters;
		this.initCause(throwable);
	}

	/**
	 * Gera uma BusinessException com uma mensagem customizada apartir dos
	 * bundles do sistema.
	 * @param bundle bundle com a mensagem
	 * @param key chave da mensagem no bundle
	 * @param parameters paramentros
	 */
	public BusinessException(Bundle bundle, String key, Object... parameters) {
		this.bundle = bundle;
		this.key = key;
		this.parameters = parameters;
	}

	/**
	 * Retorna o enum do Bundle informado na excecao.
	 * @return enum do Bundle informado na excecao
	 */
	public Bundle getBundle() {
		return bundle;
	}

	/**
	 * Retorna o valor da chave declarado na excecao.
	 * @return valor da chave declarado na excecao
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Retora uma array de objetos informado na excecao.
	 *
	 * @return array de objetos informado na excecao
	 */
	public Object[] getParameters() {
		return parameters;
	}

	/**
	 * Retora a mensagem da excecao.
	 * @return mensagem da excecao
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}
}
