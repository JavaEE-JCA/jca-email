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

package ir.moke.jca.adapter;

import ir.moke.jca.api.Email;
import ir.moke.jca.api.MessageMapper;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailProducer {

    private final SmtpManagedConnectionFactory smcf;

    public EmailProducer(SmtpManagedConnectionFactory smcf) {
        this.smcf = smcf;
    }

    public void sendEmail(Email email) {
        Session session = Session.getInstance(getProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smcf.getUsername(), smcf.getPassword());
            }
        });

        session.setDebug(Boolean.parseBoolean(smcf.getDebug()));

        try {
            MimeMessage mimeMessage = MessageMapper.mapToMoMimeMessage(email, session);
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.trust", "*");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", smcf.getHostname());
        properties.put("mail.smtp.port", smcf.getPortNumber());
        return properties;
    }
}
