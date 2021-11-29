package hexlet.code.model;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Entity
public final class Url extends Model {

    @Id
    private long id;

    @Column(unique = true)
    private String name;

    @WhenCreated
    private Date createdAt;

    @OneToMany
    private List<UrlCheck> urlChecks;


    public Url(String name) {
        this.name = name;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUrlChecks(List<UrlCheck> urlChecks) {
        this.urlChecks = urlChecks;
    }


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public List<UrlCheck> getUrlChecks() {
        return urlChecks;
    }

    public UrlCheck getLastCheck() {
        return urlChecks.stream()
                .sorted(Comparator.comparing(UrlCheck::getCreatedAt).reversed())
                .findFirst()
                .orElse(null);
    }
}
