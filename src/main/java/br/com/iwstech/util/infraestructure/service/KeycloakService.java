package br.com.iwstech.util.infraestructure.service;


import java.io.IOException;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.iwstech.util.infraestructure.exception.NegocioException;
import br.com.iwstech.util.infraestructure.log.Log;

@Service
public class KeycloakService {

    @Autowired
    private Environment env;

    private String accessToken;

    private void getAdminToken() {

        final String ENDPOINT = env.getProperty("keycloak.auth-server-url") + env.getProperty("admin.keycloak.tokenendpoint");

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
        parts.add("grant_type", "password");
        parts.add("client_id", env.getProperty("admin.keycloak.clientid"));
        parts.add("client_secret", env.getProperty("admin.keycloak.clientsecret"));
        parts.add("username", env.getProperty("admin.keycloak.username"));
        parts.add("password", env.getProperty("admin.keycloak.password"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, Object>> payload = new HttpEntity<MultiValueMap<String, Object>>(parts, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> entity = restTemplate.exchange(ENDPOINT, HttpMethod.POST, payload, String.class);

        try {
            ObjectNode node = new ObjectMapper().readValue(entity.getBody(), ObjectNode.class);
            if (node.has("access_token")) {
                accessToken = node.get("access_token").asText();
            }
        } catch (IOException ex) {
            Log.error(this.getClass(), ex);
        }

    }

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
        Response response = userResource.create(user);

        if(response.getStatus() == HttpStatus.SC_CREATED) {
            String id = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            try {
                defineUserPassword(id, userPassword);
                return id;
            } catch(BadRequestException ex) {
                getInstance().realm(env.getProperty("keycloak.realm")).users().delete(id);
                throw new NegocioException("erro-criar-senha", ex);
            }

        } else if (response.getStatus() == HttpStatus.SC_CONFLICT) {
            throw new NegocioException("erro-perfil-autenticacao");
        }

        throw new NegocioException("erro-criar-usuario");
    }

    public void defineUserPassword(String userId, String userPassword) throws NegocioException {

        Keycloak k = getInstance();
        UserResource userResource = k.realm(env.getProperty("keycloak.realm")).users().get(userId);

        CredentialRepresentation newCredential = new CredentialRepresentation();
        newCredential.setType(CredentialRepresentation.PASSWORD);
        newCredential.setValue(userPassword);
        newCredential.setTemporary(false);

        userResource.resetPassword(newCredential);

    }

}
