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

package ir.moke.jca.app.api;

import ir.moke.jca.api.Email;
import ir.moke.jca.api.SmtpConnection;
import ir.moke.jca.api.SmtpConnectionFactory;

import javax.annotation.Resource;
import javax.resource.ResourceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
