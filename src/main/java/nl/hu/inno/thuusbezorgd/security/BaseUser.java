package nl.hu.inno.thuusbezorgd.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.Objects;

@Entity
@Inheritance
public abstract class BaseUser implements Principal, Serializable {
    @Id
    protected String name;

    protected BaseUser() {
    }

    public BaseUser(String name) {
        Assert.notNull(name, "Name cannot be null");
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseUser baseUser = (BaseUser) o;
        return name.equals(baseUser.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "BaseUser{" +
                "name='" + name + '\'' +
                '}';
    }

    public abstract Collection<? extends GrantedAuthority> getAuthorities();
}
