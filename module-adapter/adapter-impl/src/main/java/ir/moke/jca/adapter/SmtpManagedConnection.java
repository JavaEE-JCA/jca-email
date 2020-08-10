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

import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SmtpManagedConnection implements ManagedConnection {

    private static final List<ConnectionEventListener> CONNECTION_EVENT_LISTENERS = new ArrayList<>();
    private SmtpManagedConnectionFactory smcf;
    private SmtpConnectionImpl connection;

    public SmtpManagedConnection(SmtpManagedConnectionFactory smcf) {
        this.smcf = smcf;
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        return new SmtpConnectionImpl(this);
    }

    @Override
    public void destroy() throws ResourceException {

    }

    @Override
    public void cleanup() throws ResourceException {

    }

    @Override
    public void associateConnection(Object connection) throws ResourceException {
        this.connection = (SmtpConnectionImpl) connection;
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        CONNECTION_EVENT_LISTENERS.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        CONNECTION_EVENT_LISTENERS.remove(listener);
    }

    @Override
    public XAResource getXAResource() throws ResourceException {
        return null;
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {

    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return null;
    }

    public void sendEmail(Email email) {
        final EmailResourceAdapter emailResourceAdapter = (EmailResourceAdapter) smcf.getResourceAdapter();
        email.setFromList(Collections.singletonList(smcf.getUsername()));
        emailResourceAdapter.sendEmail(email, smcf);
    }
}
