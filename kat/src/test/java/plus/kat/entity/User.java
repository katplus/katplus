package plus.kat.entity;

import plus.kat.anno.Expose;

import java.util.HashMap;

@SuppressWarnings("rawtypes")
public class User {

    @Expose("uid")
    public long uid;

    @Expose("name")
    public String name;

    @Expose("role")
    public String role;

    @Expose("blocked")
    public boolean blocked;

    @Expose("resource")
    public HashMap resource;

}
