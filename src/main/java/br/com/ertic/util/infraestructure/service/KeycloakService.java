package br.com.ertic.util.infraestructure.service;


import java.io.IOException;
import java.util.ArrayList;

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

import br.com.ertic.util.infraestructure.exception.InternalException;
import br.com.ertic.util.infraestructure.exception.NegocioException;
import br.com.ertic.util.infraestructure.log.Log;

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

    public String createUser(String firstName, String lastName, String userEmail, String userPassword) throws NegocioException {

        if(accessToken == null) {
            getAdminToken();
        }

        if(accessToken == null) {
            throw new InternalException("erro-recuperar-admin-token");
        }

        final String ENDPOINT = env.getProperty("keycloak.auth-server-url") + env.getProperty("admin.keycloak.usersendpoint");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        CredentialRepresentation newCredential = new CredentialRepresentation();
        newCredential.setType("password");
        newCredential.setValue(userPassword);
        newCredential.setTemporary(false);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userEmail);
        user.setEmail(userEmail);
        user.setEmailVerified(true);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCredentials(new ArrayList<CredentialRepresentation>());
        user.getCredentials().add(newCredential);

        HttpEntity<?> payload = new HttpEntity<UserRepresentation>(user, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> entity = restTemplate.exchange(ENDPOINT, HttpMethod.POST, payload, String.class);

        if(entity.getStatusCodeValue() == 201) {
            String id = entity.getHeaders().getLocation().toString();
            id = id.substring(id.lastIndexOf("/")+1);

            defineUserPassword(id, userPassword);

            accessToken = null;

        } else if(entity.getStatusCodeValue() == 409) {
            throw new NegocioException("usuario-ja-existe");
        }

        return null;
    }

    public void defineUserPassword(String userId, String userPassword) throws NegocioException {

        if(accessToken == null) {
            getAdminToken();
        }

        if(accessToken == null) {
            throw new InternalException("erro-recuperar-admin-token");
        }

        String tempUri = env.getProperty("admin.keycloak.resetpasswordendpoint");
        tempUri = tempUri.replaceAll("\\{id\\}", userId);
        final String ENDPOINT = env.getProperty("keycloak.auth-server-url") + tempUri;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        CredentialRepresentation newCredential = new CredentialRepresentation();
        newCredential.setType(CredentialRepresentation.PASSWORD);
        newCredential.setValue(userPassword);
        newCredential.setTemporary(false);

        HttpEntity<?> payload = new HttpEntity<CredentialRepresentation>(newCredential, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> entity = restTemplate.exchange(ENDPOINT, HttpMethod.PUT, payload, String.class);

        if(entity.getStatusCodeValue() != 204) {
            throw new NegocioException("senha-nao-determinada");
        }

    }
}
