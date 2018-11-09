# SalesIntegration
### Integração Rest com Salesforce

Autenticação Feito através de parâmetros gravados no banco de dados

### Requisições para entidades

Método: GET

Media Type/Accept: Application/json

URL: http://localhost:8080/salesIntegration/rest/get/

Retorno JSON
```sh
{"filter":"SELECT+Department__c,Phone2__c,Mobile2__c,Email2__c,ContractualRole__c,BranchLine__c,BranchLine2__c,ActiveContact__c,JigsawContactId,Jigsaw,PhotoUrl,IsEmailBounced,EmailBouncedDate,EmailBouncedReason,LastReferencedDate,LastViewedDate,LastCUUpdateDate,LastCURequestDate,LastActivityDate,SystemModstamp,LastModifiedById,CreatedById,CreatedDate,OwnerId,Department,Title,Email,ReportsToId,MobilePhone,Fax,Phone,MailingAddress,MailingGeocodeAccuracy,MailingLongitude,MailingLatitude,MailingCountry,MailingPostalCode,MailingState,MailingCity,MailingStreet,RecordTypeId,Name,Suffix,MiddleName,Salutation,FirstName,LastName,AccountId,MasterRecordId,IsDeleted,Id+From+Contact"}
```

Método: POST 

URL: http://localhost:8080/salesIntegration/rest/contact/save

Media Type/Accept: Application/json

Body Insert 
```sh	
{ "FirstName": "Contato", "LastName": "de teste 2", "Phone": "01170105840", "Email": "test2e@engie.com", "AccountId":"0012F000008U5svQAC" }
```

Body Update 
```sh
{ "entity":"Contact", "Id":"0012F00000AcHGshGU", "FirstName": "Contato", "LastName": "de teste 2", "Phone": "01170105840", "Email": "test2e@engie.com", "AccountId":"0012F00000AcXNxQAN" }
```
Método: DELETE 

URL: http://localhost:8080/salesIntegration/rest/salesforce/delete/?id=0012F00000AcHGshGU&entity=Contact

### Retorno de Anexos

Método: GET 

URL: localhost:8080/salesIntegration/rest/salesforce/getAnexo

Retorno JSON
```sh
{"filter": "SELECT+ContentDocumentId+FROM+ContentDocumentLink+WHERE+LinkedEntityId+=+'0Q0W0000000IWJOKA4'+ORDER+BY+SystemModstamp+DESC"}
```
