## JCA Email Connection 

Custom JavaEE 8 jca resource adapter for connecting to mail servers

#### build and run :     
**TomEE 8**     
`mvn clean compile install ; (cd ear-module/ ; mvn tomee:run)`

**Liberty/OpenLiberty**     
`mvn clean compile install ; (cd ear-module/ ; mvn liberty:run)`


***Example Implementation :***      
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
    public void receiveEmail(Message email) throws MessagingException {
        logger.info("[Google Gmail] Receive message subject: [" + email.getSubject() + "]");
    }
}
```