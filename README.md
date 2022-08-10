# KAT+

KAT+ 一个轻量级的 **KAT** + **XML** + **JSON** 库

- 兼容 **Kotlin**
- 兼容 **Android 8+**

致力于安全性、规范性、拓展性和轻量性的微组件

# 1. 使用准备

## 1.1 添加依赖

Maven:

```xml
<dependency>
    <groupId>plus.kat</groupId>
    <artifactId>kat</artifactId>
    <version>0.0.2</version>
</dependency>
```

Gradle:

```groovy
dependencies {
    implementation 'plus.kat:kat:0.0.2'
}
```

Kotlin Gradle:

```kotlin
dependencies {
    implementation("plus.kat:kat:0.0.2")
}
```

## 1.2 拓展依赖

### 1.2.1 Netty

Maven:

```xml
<dependency>
    <groupId>plus.kat</groupId>
    <artifactId>kat-netty</artifactId>
    <version>0.0.2</version>
</dependency>
```

Java:

```java
// ByteBuf
Kat chan = Kat.encode(...);
Doc chan = Doc.encode(...);
Json chan = Json.encode(...);

ByteBuf buf = ChanBuf.wrappedBuffer(chan);
ByteBuf buf = ChanBuf.wrappedBuffer(chan.getFlow());
ByteBuf buf = ChanBuf.wrappedBuffer(new Value("..."));

// ByteBuf Reader
ByteBuf buf = ...;
Event<User> event = new Event<>(
    new ByteBufReader(buf)
);
```

### 1.2.2 Spring

Maven:

```xml
<dependency>
    <groupId>plus.kat</groupId>
    <artifactId>kat-spring</artifactId>
    <version>0.0.2</version>
</dependency>
```

Java:

```java
@Configuration
public class Application implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(
        List<HttpMessageConverter<?>> converters
    ) {
        // kat
        converters.add(
            new MutableHttpMessageConverter(Job.KAT)
        );
        // xml
        converters.add(
            new MutableHttpMessageConverter(Job.DOC)
        );
        // json
        converters.add(
            0, new MutableHttpMessageConverter(Job.JSON)
        );
    }
}
```

# 2. 简单使用

### 2.1 **Data** to **Text**

Java:

```java
// kat
String kat = Kat.encode(obj);

// xml
String xml = Doc.encode(obj);

// json
String json = Json.encode(obj);
```

### 2.2 **Text** to **Map**

Java:

```java
// kat
HashMap<String, Object> data = Kat.decode(
    HashMap.class, new Event<>(
        "${i:id(1)s:name(kraity)}"
    )
);

// xml
HashMap<String, String> data = Doc.decode(
    HashMap.class, new Event<>(
        "<user><id>1</id><name>kraity</name></user>"
    )
);

// json
HashMap<String, Object> data = Json.decode(
    HashMap.class, new Event<>(
        "{\"id\":1,\"name\":\"kraity\"}"
    )
);
```

### 2.3 **Text** to **List**

Java:

```java
// kat
ArrayList<Integer> data = Kat.decode(
    ArrayList.class, new Event<>(
        "${i(1)i(2)i(3)}"
    )
);

// xml
ArrayList<String> data = Doc.decode(
    ArrayList.class, new Event<>(
        "<list><item>1</item><item>2</item></list>"
    )
);

// json
ArrayList<Integer> data = Json.decode(
    ArrayList.class, new Event<>(
        "[1,2,3]"
    )
);
```

### 2.4 **Text** to **Object**

```java
@Embed()
class User {
    @Expose("id")
    private int id;

    @Expose("name")
    private String name;
}
```

Java:

```java
// kat
User user = Kat.decode(
    User.class, "${i:id(1)s:name(kraity)}"
);

// xml
User user = Doc.decode(
    User.class, "<user><id>1</id><name>kraity</name></user>"
);

// json
User user = Json.decode(
    User.class, "{\"id\":1,\"name\":\"kraity\"}"
);
```

### 2.5 **Chan** to **Text**

Java:

```java
// kat
Chan chan = new Chan(c -> {
    c.set("id", 100001);
    c.set("title", "kat");
    c.set("author", "User", $ -> {
        $.set("id", 1);
        $.set("name", "kraity");
    });
});

// M{i:id(100001)s:title(kat)User:author{i:id(1)s:name(kraity)}}
byte[] src = chan.getFlow.copyBytes();
String text = chan.toString()

// json
Json json = new Json(c -> {
    c.set("id", 100001);
    c.set("title", "kat");
    c.set("author", "User", $ -> {
        $.set("id", 1);
        $.set("name", "kraity");
    });
});
// {"id":100001,"title":"kat","author":{"id":1,"name":"kraity"}}
byte[] src = json.getFlow.copyBytes();
String text = json.toString()

// xml
Doc doc = new Doc("Story", c -> {
    c.set("id", 100001);
    c.set("title", "kat");
    c.set("author", "User", $ -> {
        $.set("id", 1);
        $.set("name", "kraity");
    });
});
// <Story><id>100001</id><title>kat</title><author><id>1</id><name>kraity</name></author></Story>
byte[] src = doc.getFlow.copyBytes();
String text = doc.toString()
```

Kotlin:

````kotlin
// kat
val text = kat {
    it["id"] = 100001
    it["title"] = "kat"
    it["meta"] = { meta ->
        meta["view"] = 99
    }
    it["author", "User"] = { user ->
        user["id"] = 1
        user["name"] = "kraity"
    }
}

// json
val text = json {
    it["id"] = 100001
    it["title"] = "kat"
    it["meta"] = { meta ->
        meta["view"] = 99
    }
    it["author"] = { user ->
        user["id"] = 1
        user["name"] = "kraity"
    }
}

// xml
val text = doc("Story") {
    it["id"] = 100001
    it["title"] = "kat"
    it["meta"] = { meta ->
        meta["view"] = 99
    }
    it["author"] = { user ->
        user["id"] = 1
        user["name"] = "kraity"
    }
}
````

# 3. 基础使用

### 3.1 Use **Spare**

```java
@Embed()
class User {
    @Expose("id")
    private int id;

    @Expose("name")
    private String name;
}
```

Java:

```java
// register User
Spare<User> spare = Spare.lookup(User.class);

// kat
User user = spare.read(
    "${i:id(1)s:name(kraity)}"
);
Chan chan = spare.write(user);

// xml
User user = spare.down(
    "<user><id>1</id><name>kraity</name></user>"
);
Doc doc = spare.mark(user);

// json
User user = spare.parse(
    "{\"id\":1,\"name\":\"kraity\"}"
);
Json json = spare.serial(user);

// cast
User user = spare.cast(
    "${i:id(1)s:name(kraity)}"
);

// cast
User user = spare.cast(
    "{\"id\":1,\"name\":\"kraity\"}"
);

// cast
User user = spare.cast(
    "<user><id>1</id><name>kraity</name></user>"
);

// cast
User user = spare.cast(
    Map.of("id", 1, "name", "kraity")
);
```

FLAT:

```java
Spare<User> spare = ...
Map<String, Object> collector = ...

User user = ...
spare.flat(
    user, collector::put
);

int id = (int) collector.get("id");
String name = (String) collector.get("name");
```

JDBC:

```java
// register User
Spare<User> spare = Spare.lookup(User.class);

ResultSet rs = stmt.executeQuery(sql);
List<User> users = new ArrayList<>();

while (rs.next()) {
    users.add(
        spare.apply(rs)
    );
}
```

### 3.1 Use **Event**

Create event:

```java
// create event
Event<?> event = new Event<>();

// specify supplier
event.with(
    Supplier.ins()
);

// specify type
event.with(User.class);

// specify reader
Reader reader = ...;
event.setReader(reader);

// specify flag
event.with(Flag.INDEX_AS_ENUM);
```

Extends event:

```java
// default supplier
Supplier supplier = Supplier.ins();

User[] data = supplier.read(
    "A", new Event<User[]>("...") {}  // similar: event.with(User[].class);
);

ArrayList<User> data = supplier.read(
    "L", new Event<ArrayList<User>>("...") {}
);

HashMap<Long, User> data = supplier.read(
    "M", new Event<HashMap<Long, User>>("...") {}
);
```

Mutable event:

```java
// use file
File file = ...;
Event<User> event = new Event<>(file);
Event<User> event = Event.file("./test/entity/user.kat");

// use remote
URL url = ...;
Event<User> event = new Event<>(url);
Event<User> event = Event.remote("https://kat.plus/test/entity/user.kat");

// use String
String data = ...;
Event<User> event = new Event<>(data);

// use String with Cipher
String data = ...;
Cipher cipher = ...;
Event<User> event = new Event<>(data, cipher);

// use byte array
byte[] data = ...;
Event<User> event = new Event<>(data);

// use byte array with Cipher
byte[] data = ...;
Cipher cipher = ...;
Event<User> event = new Event<>(data, cipher);

// use InputStream
InputStream stream = ...;
Event<User> event = new Event<>(stream);

// use InputStream with Cipher
Cipher cipher = Cipher.getInstance(
    "AES/CBC/PKCS5Padding"
);
cipher.init(
    Cipher.DECRYPT_MODE,
    new SecretKeySpec(
        "key".getBytes(), "AES"
    ),
    new IvParameterSpec(
        "iv".getBytes()
    )
);
InputStream stream = ...;
Event<User> event = new Event<>(stream, cipher);
```

# 4. 进阶使用

### 4.1 Use **Supplier**

```java
@Embed("plus.kat.entity.User")
class User {
    @Expose("id")
    private int id;

    @Expose("name")
    private String name;
}
```

Use class type:

```java
// default supplier
Supplier supplier = Supplier.ins();

// cast
User user = supplier.cast(
    User.class, Map.of(
        "id", 1, "name", "kraity"
    )
);

// kat
User user = supplier.read(
    User.class, new Event<>(
        "${i:id(1)s:name(kraity)}"
    );
);

// xml
User user = supplier.down(
    User.class, new Event<>(
        "<user><id>1</id><name>kraity</name></user>"
    );
);

// json
User user = supplier.parse(
    User.class, new Event<>(
        "{\"id\":1,\"name\":\"kraity\"}"
    );
);
```

Use package name:

```java
// default Supplier
Supplier supplier = Supplier.ins();

// register User
supplier.lookup(User.class);

// cast
User user = supplier.cast(
    "plus.kat.entity.User", Map.of(
        "id", 1, "name", "kraity"
    )
);

// kat
User user = supplier.read(
    "plus.kat.entity.User", new Event<>(
        "${i:id(1)s:name(kraity)}"
    );
);

// xml
User user = supplier.down(
    "plus.kat.entity.User", new Event<>(
        "<user><id>1</id><name>kraity</name></user>"
    );
);

// json
User user = supplier.parse(
    "plus.kat.entity.User", new Event<>(
        "{\"id\":1,\"name\":\"kraity\"}"
    );
);
```

Register custom Spare:

```java
// your Supplier
Supplier supplier = ...;

// register Spare of User
// see: 4.4 Use custom Spare
 supplier.embed(
     User.class, new UserSpare()
);
```

Removes Spare of specified Class

```java
// your Supplier
Supplier supplier = ...;

// class type
supplier.revoke(User.class);

// package name
supplier.revoke("plus.kat.entity.User");
```

### 4.2 Use custom **Coder**

```java
@Embed()
class User {
    @Expose("id")
    private int id;

    @Expose("name")
    private String name;

    @Expose(value = "status", with = StatusCoder.class)
    private String status;
}
```

Java:

```java
class StatusCoder implements Coder<String> {

    @Override
    public String read(
        Flag flag,
        Value value
    ) {
        if (value.is("PUBLISH")) {
            return "open";
        }

        if (value.is("PRIVATE")) {
            return "self";
        }

        return "unknown";
    }

    @Override
    public void write(
        Flow flow,
        Object value
    ) throws IOCrash {
        switch ((String) value) {
            case "open": {
                flow.emit("PUBLISH");
                break;
            }
            case "self": {
                flow.emit("PRIVATE");
                break;
            }
            default: {
                flow.emit("UNKNOWN");
            }
        }
    }
}
```

### 4.3 Use custom **Coder** and **Builder**

```java
// POJO
class User {
    private int id;
    private String name;
    private boolean blocked;
    private User collaborator;
}

@Embed()
class Note {
    @Expose("id")
    private int id;

    @Expose(value = "author", with = AuthorCoder.class)
    private User author;
}
```

Java:

```java
class AuthorCoder implements Coder<User> {

    @Override
    public Boolean getFlag() {
        return true;
    }

    @Override
    public void write(
        Chan chan,
        Object value
    ) throws IOCrash {
        User user = (User) value;

        chan.set("id", user.getId());
        chan.set("name", user.getName());
        chan.set("blocked", user.isBlocked());
    }

    @Override
    public Builder<User> getBuilder(Type type) {
        return new AuthorBuilder();
    }
}

class AuthorBuilder extends Builder<User> {
    private User user;

    @Override
    public void onCreate(
        Alias alias
    ) throws Crash, IOCrash {
        user = new User();
    }

    @Override
    public void onAccept(
        Alias alias,
        Builder<?> child
    ) throws IOCrash {
        // check key
        if (alias.is("collaborator")) {
            user.setCollaborator(
                (User) child.getResult()
            );
        }
    }

    @Override
    public void onAccept(
        Space space,
        Alias alias,
        Value value
    ) throws IOCrash {
        if (alias.is("id")) {
            user.setId(
                value.toInt()
            );
        }

        else if (alias.is("name")) {
            user.setName(
                value.toString()
            );
        }

        else if (alias.is("blocked")) {
            user.setBlocked(
                value.toBoolean()
            );
        }
    }

    @Override
    public Builder<?> getBuilder(
        Space space,
        Alias alias
    ) throws IOCrash {
        // check key
        if (alias.is("collaborator")) {
            // by supplier
            Spare<?> spare = supplier.lookup(User.class);

            // skip if null
            if (spare != null) {
                return spare.getBuilder(User.class);
            }

            // by custom builder
            return new AuthorBuilder();
        }
        return null;
    }

    @Override
    public User getResult() {
        return user;
    }

    @Override
    public void onDestroy() {
        user = null;
    }
}
```

### 4.4 Use custom **Spare**

```java
@Embed(with = UserSpare.class)
class User {
    private int id;
    private String name;
}
```

Java:

```java
class UserSpare implements Spare<User> {

    // Register Spare of User by ClassLoader
    // If this static block is removed, UserSpare will be created by reflection
    static {
        // Register in the global Spare Cluster
        Spare.embed(
            User.class, new UserSpare()
        );

        // or register in your Supplier
        // Supplier supplier = getSupplier();
        // supplier.embed(
        //     User.class, new UserSpare()
        // );
    }

    @Override
    public CharSequence getSpace() {
        return "plus.kat.entity.User";
    }

    @Override
    public Boolean getFlag() {
        return true;
    }

    @Override
    public boolean accept(
        Class<?> klass
    ) {
        return klass.isAssignableFrom(User.class);
    }

    @Override
    public Class<User> getType() {
        return User.class;
    }

    @Override
    public void write(
        Chan chan,
        Object value
    ) throws IOCrash {
        // see: 4.3 Use custom Coder
    }

    @Override
    public Builder<User> getBuilder(Type type) {
        // see: 4.3 Use custom Builder
        return new UserBuilder();
    }
}
```
