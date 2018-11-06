package br.com.salesIntegration.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import br.com.salesIntegration.commons.BusinessException;
import br.com.salesIntegration.vo.AnexoSalesforceVO;

/**
 * Classe de serviço de generica do salesforce
 */
@Path("/salesforce")
public class GenericService extends ServiceBase implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(GenericService.class);

	@POST
	@Produces("application/json; charset=UTF-8")
	@Consumes("application/json; charset=UTF-8")	
	@Path("/get")
	public Object get(String json) throws BusinessException {	
		return super.getObject(json);
	}
	
	@POST
	@Produces("application/json;")
	@Consumes("application/json; charset=UTF-8")
	@Path("/getAnexo")
	public Object getAnexo(String json) throws BusinessException {	
		try{
			JSONObject jsonContentDocumentId = returnContentDocumentId((JSONObject) JSONValue.parse(super.getObject(json).toString()));
			
			List<AnexoSalesforceVO> anexos = returnAnexoId((JSONObject) JSONValue.parse(super.getObject(jsonContentDocumentId.toString()).toString()));
			
			return super.getAnexoSF(anexos);
		}catch (BusinessException e) {
			LOGGER.error("Anexo não encontrado!", e);			
		}
		JSONObject retorno = new JSONObject();
		retorno.put("errorCode", "No Content");
		retorno.put("message", "Provided external ID field does not exist or is not accessible: ");
		return retorno;
	}

	@POST
	@Produces("application/json; charset=UTF-8")
	@Consumes("application/json; charset=UTF-8")	
	@Path("/save")
	public Object insertUpdate(String json) throws ParseException{		
		return super.insertUpdateSF(json);
	}

	@DELETE
	@Produces("application/json; charset=UTF-8")
	@Path("/delete")
	public Object delete(@QueryParam("id") String id, @QueryParam("entity") String entity){
		return super.deleteSF(id, entity);
	}	
	
	public JSONObject returnContentDocumentId(JSONObject json){
		JSONArray array = (JSONArray) json.get("records");
		int count = array.size();
		String id ="";
		if(count > 1){
			JSONObject jsonObject = (JSONObject) array.get(0);
			id = "'"+jsonObject.get("ContentDocumentId").toString()+"'";
			for (int i = 1; i < count; i++) {
	            jsonObject = (JSONObject) array.get(i);
	            
	            id += ","+"'"+jsonObject.get("ContentDocumentId").toString()+"'";	        
			}
		}else{
        	JSONObject jsonObject = (JSONObject) array.get(0);
        	id += "'"+jsonObject.get("ContentDocumentId").toString()+"'";	        
        }
		
		JSONObject j = new JSONObject();
		j.put("filter", "SELECT VersionData,FileExtension,FileType FROM ContentVersion WHERE ContentDocumentId in ( "+id+")");
		return j;
	}
	
	public List<AnexoSalesforceVO> returnAnexoId(JSONObject json){
		JSONArray array = (JSONArray) json.get("records");
		List<AnexoSalesforceVO> anexos = new ArrayList<>();
		int count = array.size();
		for (int i = 0; i < count; i++) {
            JSONObject jsonObject = (JSONObject) array.get(i);
            AnexoSalesforceVO anexo = new AnexoSalesforceVO();
            String[] textoSeparado = jsonObject.get("VersionData").toString().split("/");
            anexo.setId(textoSeparado[6]);
            anexo.setExtensao(jsonObject.get("FileExtension").toString());
            anexos.add(anexo);
        }
		return anexos;
	}
}