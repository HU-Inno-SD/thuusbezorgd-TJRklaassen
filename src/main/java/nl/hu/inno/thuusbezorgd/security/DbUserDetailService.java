package nl.hu.inno.thuusbezorgd.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class DbUserDetailService implements UserDetailsService {
    private final DbUserRepository users;

    public DbUserDetailService(DbUserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.users.findById(username).orElse(null);

    }
}
