package br.com.salesIntegration.rest.util;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.JsonArray;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import br.com.salesIntegration.commons.BusinessException;
import br.com.salesIntegration.entity.SalesLog;
import br.com.salesIntegration.entity.SalesParametro;
import br.com.salesIntegration.service.SalesLogService;
import br.com.salesIntegration.service.SalesParametroService;

/**
 * Classe util de integração com salesforce
 */
@Named
public class RestUtils implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(RestUtils.class);

	private String urlRest;

	@Inject
	private TokenSalesforce tokenSalesforce;

	@EJB
	private SalesLogService salesLogService;

	@EJB
	private SalesParametroService salesParametroService;

	public Response getResponseFromRestServiceGet(String serviceToComsume) {
		LOGGER.info("Buscando token GET...");
		this.callAuthAplicationRest();

		LOGGER.info("Criando request GET...");
		Client client = ClientBuilder.newClient();

		WebTarget target = client.target(urlRest.concat(serviceToComsume));

		LOGGER.info("Retornando request GET...");

		return target.request().header("Authorization", "Bearer ".concat(tokenSalesforce.getSalesforce())).header("Content-Type", "application/json; charset=UTF-8").get();

	}
	
	public Response getResponseFromRestServiceGetAnexo(String serviceToComsume) {
		LOGGER.info("Buscando token GET...");
		this.callAuthAplicationRest();

		LOGGER.info("Criando request GET...");
		Client client = ClientBuilder.newClient();

		WebTarget target = client.target(urlRest.concat(serviceToComsume));

		LOGGER.info("Retornando request GET...");

		return target.request(MediaType.APPLICATION_OCTET_STREAM).header("Authorization", "Bearer ".concat(tokenSalesforce.getSalesforce())).get();

	}

	public Response getResponseFromRestServicePost(String serviceToComsume, Object object) throws BusinessException {
		LOGGER.info("Buscando token POST...");
		this.callAuthAplicationRest();

		LOGGER.info("Criando request POST...");
		Client client = ClientBuilder.newClient();

		WebTarget target = client.target(urlRest.concat("sobjects/").concat(serviceToComsume));

		LOGGER.info("Retornando request POST...");
		return target.request().header("Authorization", "Bearer ".concat(tokenSalesforce.getSalesforce())).header("Content-Type", "application/json").post(Entity.json(object));		
	}

	public Response getResponseFromRestServiceDelete(String serviceToComsume) throws BusinessException {
		LOGGER.info("Buscando token DELETE...");
		this.callAuthAplicationRest();

		LOGGER.info("Criando request DELETE...");
		Client client = ClientBuilder.newClient();

		WebTarget target = client.target(urlRest.concat("sobjects/").concat(serviceToComsume));

		LOGGER.info("Retornando request DELETE...");
		return target.request().header("Authorization", "Bearer ".concat(tokenSalesforce.getSalesforce())).header("Content-Type", "application/json").delete();
	}

	public Response getResponseFromRestServicePut(String serviceToComsume, Object object) throws BusinessException {
		LOGGER.info("Buscando token PUT...");
		this.callAuthAplicationRest();

		LOGGER.info("Criando request PUT...");
		Client client = ClientBuilder.newClient();

		WebTarget target = client.target(urlRest.concat("sobjects/").concat(serviceToComsume));

		LOGGER.info("Retornando request PUT...");
		Response response = target.request().
				header("Authorization", "Bearer ".concat(tokenSalesforce.getSalesforce())).
				header("Content-Type", "application/json").
				build("PATCH", Entity.json(object)).invoke();

		if (response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
			return response;
		}else{
			LOGGER.error(response.getStatus()+" - "+response.getStatusInfo());
			return response;
		}
	}

	private void callAuthAplicationRest() {

		Client client = ClientBuilder.newClient();

		StringBuilder content = new StringBuilder();

		SalesParametro salesParametro = new SalesParametro();

		try{
			salesParametro = salesParametroService.find();
			content.append(salesParametro.getUrlToken());
			content.append("?grant_type=password");
			content.append("&client_id=".concat(salesParametro.getClientId()));
			content.append("&client_secret=".concat(salesParametro.getClientSecret()));
			content.append("&username=".concat(salesParametro.getUsername()));
			content.append("&password=".concat(salesParametro.getPassword()));
			urlRest = salesParametro.getUrlRest();
		}catch(BusinessException e){
			LOGGER.error("Erro na busca de parâmetros.",e);
			LOGGER.error(e);
		}
		LOGGER.info("Autenticando");
		WebTarget target = client.target(content.toString());

		Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity("", MediaType.APPLICATION_FORM_URLENCODED));

		LOGGER.info("Autenticado");
		if (response.getStatus() == Status.OK.getStatusCode()) {
			JsonObject token = response.readEntity(JsonObject.class);
			Gson gson = new Gson();
			JsonElement element = gson.fromJson(token.toString(), JsonElement.class);
			tokenSalesforce.setSalesforce(element.getAsJsonObject().get("access_token").getAsString());
		} else {
			LOGGER.info("Erro ao gerar token Salesforce");
		}

	}

	public SalesLog saveLog(Response response, String method, String entity) throws BusinessException{
		SalesLog salesLog = new SalesLog();		
		salesLog.setData(new Date());		
		salesLog.setTabela(entity);			
		salesLog.setMethod(method);
		try{
			salesLog.setDescricao(response.getStatusInfo().toString());
			salesLog.setCodigoRetorno(Integer.toString(response.getStatus()));
			JsonArray jsonArray = response.readEntity(JsonArray.class);
			for(int i=0;i<jsonArray.size();i++){
				HashMap<String, String> passedValues = (HashMap<String, String>) jsonArray.get(i);
				for (Entry<String, String> mapTemp : passedValues.entrySet()) {
					if (mapTemp.getKey().equalsIgnoreCase("message")) {
						salesLog.setMensagem(mapTemp.getValue());
					}
					if (mapTemp.getKey().equalsIgnoreCase("errorCode")) {
						salesLog.setErrorCode(mapTemp.getValue());
					}
				}
			} 
			salesLogService.saveOrUpdate(salesLog);			
			return salesLog;
		}catch(BusinessException e){
			LOGGER.error("Ocorreu um erro!", e);
			salesLogService.saveOrUpdate(salesLog);			
			return salesLog;
		}		
	}

	public SalesLog saveLog(String method, String entity, String descricao) {
		SalesLog salesLog = new SalesLog();		
		try{
			
			salesLog.setData(new Date());		
			salesLog.setTabela(entity);			
			salesLog.setMethod(method);
			salesLog.setDescricao(descricao);		
			salesLogService.saveOrUpdate(salesLog);			
			return salesLog;
		}catch(BusinessException e){
			LOGGER.error("Ocorreu um erro!", e);			
			return salesLog;
		}		
	}
}
