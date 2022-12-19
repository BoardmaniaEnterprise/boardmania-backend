package com.unibuc.boardmania.service;

import com.unibuc.boardmania.model.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Service
public class KeycloakAdminService {

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    private final Keycloak keycloak;
    private RealmResource realm;

    public KeycloakAdminService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @PostConstruct
    public void initRealmResource() {
        this.realm = this.keycloak.realm(keycloakRealm);
    }

    public void registerUser(User user, String password, String role) {
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setEnabled(true);
        keycloakUser.setUsername(user.getId().toString());
        keycloakUser.setEmail(user.getEmail());
        keycloakUser.setFirstName(user.getFirstName());
        keycloakUser.setLastName(user.getLastName());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        credentialRepresentation.setTemporary(false);

        keycloakUser.setCredentials(Collections.singletonList(credentialRepresentation));

        // Add the user to the Keycloak Realm
        Response response = realm.users().create(keycloakUser);
        String keycloakUserId = getCreatedId(response);

        UserResource userResource = realm.users().get(keycloakUserId);
        RoleRepresentation roleRepresentation = realm.roles().get(role).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));

    }

//    public void deleteUser(Long userId) {
//        UserRepresentation userRepresentation = realm.users().search(userId.toString()).get(0);
//        realm.users().delete(userRepresentation.getId());
//
//    }
//
//    public void deleteAllUsers() {
//        List<UserRepresentation> users = realm.users().list();
//        for(UserRepresentation ur : users) {
//            realm.users().delete(ur.getId());
//        }
//    }

    /*
    public void changePassword(ChangePasswordDto changePasswordDto) {
        UserRepresentation userRepresentation = realm.users().search(changePasswordDto.getUserId().toString()).get(0);

        CredentialRepresentation newCredential = new CredentialRepresentation();
        newCredential.setType(CredentialRepresentation.PASSWORD);
        newCredential.setValue(changePasswordDto.getNewPassword());
        newCredential.setTemporary(false);
        userRepresentation.setCredentials(Collections.singletonList(newCredential));

        realm.users().get(userRepresentation.getId()).update(userRepresentation);
    }
    */

//    public void addRole(String roleName, Long uid) {
//        String keycloakUid = realm.users().search(uid.toString()).get(0).getId();
//        UserResource userResource = realm.users().get(keycloakUid);
//        RoleRepresentation roleToAdd = realm.roles().get(roleName).toRepresentation();
//
//        userResource.roles().realmLevel().add(Collections.singletonList(roleToAdd));
//    }
//
//    public void removeRole(String roleName, Long uid) {
//        String keycloakUid = realm.users().search(uid.toString()).get(0).getId();
//        UserResource userResource = realm.users().get(keycloakUid);
//        RoleRepresentation roleToRemove = realm.roles().get(roleName).toRepresentation();
//
//        userResource.roles().realmLevel().remove(Collections.singletonList(roleToRemove));
//    }

}
