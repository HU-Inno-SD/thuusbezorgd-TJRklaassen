package nl.hu.inno.thuusbezorgd.security;

import org.springframework.data.repository.CrudRepository;

public interface DbUserRepository extends CrudRepository<DbUser, String> {
}
