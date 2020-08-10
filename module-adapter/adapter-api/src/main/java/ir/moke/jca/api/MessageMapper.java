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

package ir.moke.jca.api;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.stream.Collectors;

public interface MessageMapper {
    static Email mapToEmail(Message message) throws MessagingException {
        Email email = new Email();
        email.setMessageNumber(message.getMessageNumber());
        email.setSubject(message.getSubject());
        email.setFromList(Arrays.stream(message.getFrom()).map(Address::toString).map(e -> e.replaceAll(".*<", "")).map(e -> e.replaceAll(">.*", "")).collect(Collectors.toList()));
        email.setGetReplayToList(Arrays.stream(message.getAllRecipients()).map(Address::toString).map(e -> e.replaceAll(".*<", "")).map(e -> e.replaceAll(">.*", "")).collect(Collectors.toList()));
        email.setGetReplayToList(Arrays.stream(message.getReplyTo()).map(Address::toString).map(e -> e.replace(".*<", "")).map(e -> e.replace(">.*", "")).collect(Collectors.toList()));
        email.setSendDate(message.getSentDate());
        email.setReceivedDate(message.getReceivedDate());
        return email;
    }

    static MimeMessage mapToMoMimeMessage(Email email, Session session) throws MessagingException {
        Address[] toAddresses = email.getRecipientList().stream().map(MessageMapper::mapToAddress).toArray(Address[]::new);
        Address[] replyToAddresses = email.getGetReplayToList() != null ? email.getGetReplayToList().stream().map(MessageMapper::mapToAddress).toArray(Address[]::new) : null;
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setContent(email.getContent(), email.getContentType());
        mimeMessage.setSender(new InternetAddress(email.getFromList().get(0)));
        mimeMessage.setRecipients(Message.RecipientType.TO, toAddresses);
        mimeMessage.setReplyTo(replyToAddresses);
        mimeMessage.setSubject(email.getSubject());
        mimeMessage.setSentDate(email.getSendDate());
        return mimeMessage;
    }

    static Address mapToAddress(String addr) {
        Address address = null;
        try {
            address = new InternetAddress(addr);
        } catch (AddressException e) {
            e.printStackTrace();
        }
        return address;
    }
}
