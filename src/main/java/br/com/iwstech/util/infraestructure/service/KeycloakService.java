package br.com.iwstech.util.infraestructure.service;


import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import br.com.iwstech.util.infraestructure.exception.KCUsuarioJaExisteException;
import br.com.iwstech.util.infraestructure.exception.NegocioException;

@Service
public class KeycloakService {

    @Autowired
    private Environment env;

    private Keycloak getInstance() {
        // "manage-users, view-clients, view-realm, view-users" roles for "realm-management"
        return Keycloak.getInstance(
            env.getProperty("keycloak.auth-server-url"),
            env.getProperty("keycloak.realm"),
            env.getProperty("admin.keycloak.username"),
            env.getProperty("admin.keycloak.password"),
            env.getProperty("admin.keycloak.clientid"));
    }

    public String createUser(String firstName, String lastName, String userEmail, String userPassword) throws NegocioException {

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userEmail);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(userEmail);
        user.setEnabled(true);

        RealmResource realm = getInstance().realm(env.getProperty("keycloak.realm"));
        UsersResource userResource = realm.users();

        // CRIANDO O USUARIO
        Response response = userResource.create(user);
        if(response.getStatus() == HttpStatus.SC_CREATED) {

            // RECUPERANDO O ID
            String id = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            try {

                // DEFININDO A SENHA
                defineUserPassword(id, userPassword);
                return id;

            } catch(BadRequestException ex) {
                getInstance().realm(env.getProperty("keycloak.realm")).users().delete(id);
                throw new NegocioException("erro-criar-senha", ex);
            }

        } else if (response.getStatus() == HttpStatus.SC_CONFLICT) {
            throw new KCUsuarioJaExisteException("email-ja-registrado");
        }

        throw new NegocioException("erro-criar-usuario");

    }

    public void defineUserPassword(String userId, String userPassword) throws NegocioException {

        RealmResource realm = getInstance().realm(env.getProperty("keycloak.realm"));
        UserResource userResource = realm.users().get(userId);

        CredentialRepresentation newCredential = new CredentialRepresentation();
        newCredential.setType(CredentialRepresentation.PASSWORD);
        newCredential.setValue(userPassword);
        newCredential.setTemporary(false);

        userResource.resetPassword(newCredential);

    }

    public UserRepresentation findUserByEmail(String email) throws NegocioException {

        RealmResource realm = getInstance().realm(env.getProperty("keycloak.realm"));
        List<UserRepresentation> users = realm.users().search(null, null, null, email, null, null);

        if(users != null && !users.isEmpty()) {
            return users.get(0);
        }

        return null;

    }

}
