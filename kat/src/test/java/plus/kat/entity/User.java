package plus.kat.entity;

import plus.kat.actor.Magic;

/**
 * @author kraity
 */
public class User {
    @Magic("id")
    public long id;

    @Magic("name")
    public String name;
}
