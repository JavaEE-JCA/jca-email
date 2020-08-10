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

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.imap.IdleManager;
import ir.moke.jca.api.Email;
import ir.moke.jca.api.MailListener;
import ir.moke.jca.api.MessageMapper;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class EmailConsumer extends Thread {

    private static final Logger logger = Logger.getLogger(EmailConsumer.class.getName());

    private static final String PROTOCOL = "imaps";
    private final MessageEndpointFactory messageEndpointFactory;
    private final EmailActivationSpec spec;
    private IMAPSSLStore store;
    private Session emailSession;
    private MessageEndpoint messageEndpoint;

    public EmailConsumer(MessageEndpointFactory messageEndpointFactory, EmailActivationSpec spec) {
        this.messageEndpointFactory = messageEndpointFactory;
        this.spec = spec;
    }

    @Override
    public void run() {
        String hostname = spec.getHostname();
        int port = spec.getPort();
        String username = spec.getUsername();
        String password = spec.getPassword();

        Properties properties = new Properties();
        properties.put("mail.imaps.host", hostname);
        properties.put("mail.imaps.port", port);
        properties.put("mail.imaps.ssl.trust", "*");
        properties.put("mail.imaps.timeout", "10000");
        properties.put("mail.imaps.starttls.enable", "true");
        properties.put("mail.imaps.auth", "true");
        properties.put("mail.imaps.usesocketchannels", "true");


        try {
            emailSession = Session.getInstance(properties);
            store = (IMAPSSLStore) emailSession.getStore(PROTOCOL);
            store.connect(username, password);

            if (!store.hasCapability("IDLE")) {
                logger.fine(spec.getName() + " does not supported idle connection");
            }

            if (store.isConnected()) {
                logger.info("Connection Success [" + spec.getName() + "]");
            } else {
                logger.warning("Connection Failure [" + spec.getName() + "]");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (store.isConnected()) {
            final IMAPFolder inbox;
            try {
                inbox = (IMAPFolder) store.getFolder("INBOX");
                inbox.open(Folder.READ_WRITE);

                IdleManager idleManager = new IdleManager(emailSession, Executors.newCachedThreadPool());
                messageEndpoint = messageEndpointFactory.createEndpoint(null);
                inbox.addMessageCountListener(new MessageCountAdapter() {
                    @Override
                    public void messagesAdded(MessageCountEvent e) {
                        for (Message message : e.getMessages()) {
                            try {
                                handleEmail(MessageMapper.mapToEmail(message), messageEndpoint);
                                idleManager.watch(inbox);
                            } catch (MessagingException messagingException) {
                                messagingException.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void messagesRemoved(MessageCountEvent e) {
                        super.messagesRemoved(e);
                    }
                });

                logger.info("Message Count : " + inbox.getMessageCount());
                idleManager.watch(inbox);
                while (store.isConnected()) {
                    if (!store.isConnected()) {
                        store.connect(spec.getUsername(), spec.getPassword());
                    }

                    if (!inbox.isOpen()) {
                        inbox.open(Folder.READ_WRITE);
                        idleManager.watch(inbox);
                    }
                }
            } catch (MessagingException | IOException | UnavailableException e) {
                e.printStackTrace();
            }
        }

        logger.info("#### " + spec.getName() + " FINISHED ####");
    }

    private void handleEmail(Email email, MessageEndpoint messageEndpoint) {
        try {
            MailListener mailListener = (MailListener) messageEndpoint;
            mailListener.receiveEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MessageEndpoint getMessageEndpoint() {
        return messageEndpoint;
    }
}
