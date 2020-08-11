/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ir.moke.jca.app;

import ir.moke.jca.api.Email;
import ir.moke.jca.api.MailListener;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import java.util.logging.Logger;

/*
 * Google Gmail server information:
 * host: imap.gmail.com
 * port: 993
 * starttls: true
 * auth: true
 * */

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "name", propertyValue = "Google Gmail"),
                @ActivationConfigProperty(propertyName = "hostname", propertyValue = "imap.gmail.com"),
                @ActivationConfigProperty(propertyName = "port", propertyValue = "993"),
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
