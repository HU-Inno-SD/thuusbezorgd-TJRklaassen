package nl.hu.inno.thuusbezorgd.security;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GithubUserRepository extends CrudRepository<GithubUser, String> {

    Optional<GithubUser> findByGithubId(Long githubId);
}
