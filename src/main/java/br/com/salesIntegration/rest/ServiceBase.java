package br.com.salesIntegration.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.com.salesIntegration.commons.BusinessException;
import br.com.salesIntegration.entity.SalesLog;
import br.com.salesIntegration.rest.util.JsonUtil;
import br.com.salesIntegration.rest.util.RestUtils;
import br.com.salesIntegration.service.SalesLogService;
import br.com.salesIntegration.vo.AnexoSalesforceVO;

public class ServiceBase {

	@Inject
	protected RestUtils restUtils;	

	@EJB
	private SalesLogService salesLogService;

	private static final Logger LOGGER = Logger.getLogger(ServiceBase.class);

	public Object getObject(String select) throws BusinessException{
		SalesLog salesLog = new SalesLog();
		salesLog.setCodigoRetorno("OK");
		salesLog.setData(new Date());
		salesLog.setDescricao(select);  
		salesLog.setTabela("");			
		salesLog.setMethod("POST");
		salesLogService.saveOrUpdate(salesLog);	

		Gson gson = new Gson();
		JsonObject obj = gson.fromJson(select, JsonObject.class);		

		if(obj.get("filter") == null || "".equals(obj.get("filter").getAsString())){
			LOGGER.error("Filtro não definido!");			
			return restUtils.saveLog("get", "get", "Filtro não definido!");
		}else{
			Response response;

			response = restUtils.getResponseFromRestServiceGet("query/?q=".concat(obj.get("filter").getAsString()));

			if (response.getStatus() == Status.OK.getStatusCode()) {
				return response.readEntity(JSONObject.class);
			}else{
				return response.readEntity(JSONArray.class);
			}
		}		
	}

	public Object getAnexoSF(List<AnexoSalesforceVO> anexos) throws BusinessException{			
		SalesLog salesLog = new SalesLog();
		salesLog.setCodigoRetorno("");
		salesLog.setData(new Date());
		salesLog.setDescricao("Size lista de anexos: "+anexos.size());
		salesLog.setTabela("");			
		salesLog.setMethod("POST");
		salesLogService.saveOrUpdate(salesLog);	
		Response response;
		
		JSONArray arquivos = new JSONArray();
		for (AnexoSalesforceVO anexo : anexos) {
			response = restUtils.getResponseFromRestServiceGetAnexo("sobjects/ContentVersion/".concat(anexo.getId()).concat("/VersionData"));
			
			if (response.getStatus() == Status.OK.getStatusCode()) {
				InputStream inputStream = response.readEntity(InputStream.class);				
				String encoded = "";
				try {
					byte[] bytes = IOUtils.toByteArray(inputStream);
					encoded = Base64.getEncoder().encodeToString(bytes);				
				} catch (IOException e) {
					LOGGER.error("Error encode Base64!", e);	
					salesLog = new SalesLog();
					salesLog.setCodigoRetorno("ERROR");
					salesLog.setData(new Date());
					salesLog.setDescricao("Id do documento: "+anexo.getId()+" ERROR: "+e.getMessage());  
					salesLog.setTabela("");			
					salesLog.setMethod("POST");
					salesLogService.saveOrUpdate(salesLog);	
				}			
				JSONObject arquivo = new JSONObject();
				arquivo.put("arquivo", encoded);
				arquivo.put("extensao", anexo.getExtensao());
				arquivos.add(arquivo);
			}else{
				return response.readEntity(JSONArray.class);
			}	
		}
		return arquivos;
				
	}

	public Object insertUpdateSF(String json) throws ParseException {
		SalesLog salesLog = new SalesLog();
		salesLog.setCodigoRetorno("OK");
		salesLog.setData(new Date());
		salesLog.setDescricao(json);  
		salesLog.setTabela("");			
		salesLog.setMethod("POST");

		try {
			salesLogService.saveOrUpdate(salesLog);
		} catch (BusinessException e2) {
			LOGGER.error("Ocorreu um erro!", e2);			
			return restUtils.saveLog("save", "", e2.getMessage());
		}	

		Gson gson = new Gson();
		JsonObject obj = gson.fromJson(json, JsonObject.class);		

		if(json == null || obj.get("entity") == null){
			LOGGER.error("JSON ou entidade inválido!");			
			return restUtils.saveLog("save", "", "JSON ou entidade inválido!");
		}else{
			Response response = null;

			String entity = obj.get("entity").getAsString();
			obj.remove("entity");

			try{
				if(obj.get("Id") == null || "".equals(obj.get("Id").getAsString())){
					obj.remove("Id");
					response = restUtils.getResponseFromRestServicePost(entity, JsonUtil.objectToJson(obj));
					if (response.getStatus() == Status.CREATED.getStatusCode()) {
						return response.readEntity(JSONObject.class);
					}else{
						return response.readEntity(JSONArray.class);
					}
				}else{
					String id = obj.get("Id").getAsString();
					obj.remove("Id");
					response = restUtils.getResponseFromRestServicePut(entity+"/Id/"+id, JsonUtil.objectToJson(obj));
					if (response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
						return new JsonReturn("true", id, "");					
					}else{
						return response.readEntity(JSONArray.class);
					}					
				}			
			}catch (BusinessException e) {		
				try {
					LOGGER.error("Erro interno. Contate responsável. ",e);			
					return restUtils.saveLog(response, "insert", entity);
				} catch (BusinessException e1) {
					LOGGER.error("Erro interno. Contate responsável.",e1);			
					return restUtils.saveLog("save", entity, e1.getMessage());
				}
			}	
		}
	}

	public Object deleteSF(String id, String entity)  {
		if(id == null || entity == null){
			LOGGER.error("JSON ou entidade inválido!");			
			return restUtils.saveLog("save", entity, "Id ou entidade inválido!");
		}else{
			try{
				Response response = restUtils.getResponseFromRestServiceDelete(entity+"/Id/"+id);

				if (response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
					return new JsonReturn("true", id, "");
				}else{
					LOGGER.error(response.getStatus()+" - "+response.getStatusInfo());
					return restUtils.saveLog(response, "delete", entity);
				}
			}catch (BusinessException e) {						
				LOGGER.error("Erro interno. Contate responsável.",e);			
				return restUtils.saveLog("save", entity, e.getMessage());
			}
		}		
	}
}