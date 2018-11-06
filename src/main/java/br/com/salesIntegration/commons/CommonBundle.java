package br.com.salesIntegration.commons;

/**
 * Enum que cotem os nomes dos pacotes do Bundle
 */
public enum CommonBundle implements Bundle {

    GLOBAL("Erro global!");

    private final String basename;

    CommonBundle(String basename) {
        this.basename = basename;
    }

    /**
     * Retorna o pacote do Bundle
     * @return pacote do Bundle
     * @see br.com.tble.commons.enumeration.Bundle#getBasename()
     */
    public String getBasename() {
        return basename;
    }
    
}
