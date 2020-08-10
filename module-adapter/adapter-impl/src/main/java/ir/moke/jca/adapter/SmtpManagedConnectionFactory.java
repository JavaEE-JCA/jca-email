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

import ir.moke.jca.api.SmtpConnection;
import ir.moke.jca.api.SmtpConnectionFactory;

import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.util.Set;

@ConnectionDefinition(
        connection = SmtpConnection.class,
        connectionImpl = SmtpConnectionImpl.class,
        connectionFactory = SmtpConnectionFactory.class,
        connectionFactoryImpl = SmtpConnectionFactoryImpl.class
)
public class SmtpManagedConnectionFactory implements ManagedConnectionFactory, ResourceAdapterAssociation {
    private ResourceAdapter resourceAdapter;
    private String hostname;
    private String portNumber;
    private String username;
    private String password;
    private String debug = "false";

    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
        return new SmtpConnectionFactoryImpl(this, cxManager);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        return null;
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        return new SmtpManagedConnection(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        return (ManagedConnection) connectionSet.stream()
                .filter(e -> e instanceof SmtpManagedConnection)
                .findFirst()
                .orElse(null);

        /*ManagedConnection managedConnection;
        for (Object o : connectionSet) {
            ManagedConnection mc = (ManagedConnection) o;
            if (mc instanceof SmtpManagedConnection) {
                managedConnection = mc;
            }
        }
        return managedConnection;*/
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {

    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return null;
    }

    @Override
    public ResourceAdapter getResourceAdapter() {
        return this.resourceAdapter;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter resourceAdapter) throws ResourceException {
        this.resourceAdapter = resourceAdapter;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }
}
