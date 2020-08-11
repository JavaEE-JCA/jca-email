## JCA Email Connection 

Custom JavaEE 8 jca resource adapter for connecting to mail servers

#### build and run :     
**TomEE 8**     
`mvn clean compile install ; (cd ear-module/ ; mvn tomee:run)`

**Liberty/OpenLiberty**     
`mvn clean compile install ; (cd ear-module/ ; mvn liberty:run)`


***Example Inbound Implementation :***      
```java
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "name", propertyValue = "Google Gmail"),
                @ActivationConfigProperty(propertyName = "hostname", propertyValue = "imap.gmail.com"),
                @ActivationConfigProperty(propertyName = "port", propertyValue = "993"),
                @ActivationConfigProperty(propertyName = "protocol", propertyValue = "imaps"),
                @ActivationConfigProperty(propertyName = "username", propertyValue = "USERNAME"),
                @ActivationConfigProperty(propertyName = "password", propertyValue = "PASSWORD")
        }
)
public class GoogleGmailMDB implements MailListener {
    private static final Logger logger = Logger.getLogger(GoogleGmailMDB.class.getName());

    @Override
    public void receiveEmail(Email email) {
        logger.info("[Google Gmail] Receive message subject: [" + email.getSubject() + "]");
    }
}
```

***Example Outbound Implementation :***    
_liberty/openLiberty configuration (server.xml) :_      
```xml
<connectionFactory jndiName="eis/outlookSmtpConnection">
        <properties.jca-application.jca-resourceAdapter
                debug="true"
                hostname="smtp-mail.outlook.com"
                portNumber="587"
                username="USERNAME"
                password="PASSWORD"/>
</connectionFactory>
```       
_TomEE 8 configuration (server.xml) :_
```xml
<Resource id="eis/outlookSmtpConnection" type="ir.moke.jca.api.SmtpConnectionFactory"
              class-name="ir.moke.jca.adapter.SmtpManagedConnectionFactory">
        ResourceAdapter=EmailResourceAdapter
        TransactionSupport=none
        debug=true
        hostname=smtp-mail.outlook.com
        portNumber=587
        username=USERNAME
        password=PASSWORD
</Resource>
```    
_now example connectionFactory usage:_    
```java
@Path("email")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SendEmailApi {

    @Resource(name = "eis/outlookSmtpConnection")
    private SmtpConnectionFactory scf;

    @Path("send")
    @POST
    public void sendEmail(Email email) {
        try {
            SmtpConnection smtpConnection = scf.getConnection();
            smtpConnection.sendEmail(email);
        } catch (ResourceException e) {
            e.printStackTrace();
        }
    }
}
```     
_send email:_  
```shell script
curl -X POST -H "Content-Type: application/json" http://localhost:8080/api/v1/email/send -d '{"subject":"Hello dear !","recipientList" : ["user@domain.com"],"content":"How are you ?","contentType":"text/plain"}'
```  