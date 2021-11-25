package hexlet.code.model;

import io.ebean.Model;

import javax.persistence.Id;
import java.util.Date;

public class Url extends Model {

    @Id
    private long id;

    private String name;
    private Date createdAt;
}
