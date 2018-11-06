package br.com.salesIntegration.rest;

import java.io.Serializable;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Classe para mapear todos os RESTs utilizados nesta aplicacao
 */
@ApplicationPath("/rest")
public class JgceApplication extends Application implements Serializable {

	private static final long serialVersionUID = 1L;

}
