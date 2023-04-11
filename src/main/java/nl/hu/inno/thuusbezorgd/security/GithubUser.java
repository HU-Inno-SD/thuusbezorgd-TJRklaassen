package nl.hu.inno.thuusbezorgd.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.Entity;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Entity
public class GithubUser extends BaseUser implements OAuth2User, Serializable {

    private Long githubId;

    protected GithubUser() {
    }

    public Long getGithubId() {
        return githubId;
    }

    public String getRealName() {
        return realName;
    }

    private String realName;

    private LocalDateTime lastLoggedIn;

    public GithubUser(Long githubId, String login, String name) {
        super(login);
        this.githubId = githubId;
        this.realName = name;
    }

    public void markLogin() {
        this.lastLoggedIn = LocalDateTime.now();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of("id", this.name, "name", this.realName, "login", this.name, "githubId", this.githubId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> auths = new ArrayList<>();
        auths.add(new SimpleGrantedAuthority("ROLE_USER"));
        return auths;
    }

    public LocalDateTime getLastLoggedIn() {
        return lastLoggedIn;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GithubUser that = (GithubUser) o;
        return githubId.equals(that.githubId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), githubId);
    }

    @Override
    public String toString() {
        return "GithubUser{" +
                "name='" + name + '\'' +
                ", githubId=" + githubId +
                ", realName='" + realName + '\'' +
                '}';
    }
}
