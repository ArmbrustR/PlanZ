package de.armbrust.planz.security;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AppUserDb extends PagingAndSortingRepository<AppUser, String> {

}
