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

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

public class SmtpConnectionFactoryImpl implements SmtpConnectionFactory {

    private Reference reference;
    private SmtpManagedConnectionFactory smcf;
    private ConnectionManager cxManager;

    public SmtpConnectionFactoryImpl(SmtpManagedConnectionFactory smcf, ConnectionManager cxManager) {
        this.smcf = smcf;
        this.cxManager = cxManager;
    }

    @Override
    public SmtpConnection getConnection() throws ResourceException {
        return (SmtpConnection) cxManager.allocateConnection(smcf,null);
    }

    @Override
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    @Override
    public Reference getReference() throws NamingException {
        return this.reference;
    }
}
