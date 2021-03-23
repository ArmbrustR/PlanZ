package de.armbrust.planz.service;

import de.armbrust.planz.security.AppUser;
import de.armbrust.planz.security.AppUserDb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class AppUserService {

    private final AppUserDb appUserDb;

    public AppUserService(AppUserDb appUserDb) {
        this.appUserDb = appUserDb;
    }

    public AppUser findAppUserInAppUserDb(String appUser) {
        if (appUserDb.findById(appUser).isPresent()) {
            return appUserDb.findById(appUser).get();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User" + appUser + " is not in database");
    }

}
