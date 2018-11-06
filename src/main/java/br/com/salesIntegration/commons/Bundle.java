package br.com.salesIntegration.commons;

/**
 * Enum que contem o local dos bundles do sistema com as mensagens.
 */
public interface Bundle {

    /**
     * Retorna o pacote do Bundle
     *
     * @return pacote do Bundle
     */
    String getBasename();
}