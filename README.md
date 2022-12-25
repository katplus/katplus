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
    <version>0.0.5</version>
</dependency>
```

Gradle:

```groovy
dependencies {
    implementation 'plus.kat:kat:0.0.5'
}
```

Kotlin Gradle:

```kotlin
dependencies {
    implementation("plus.kat:kat:0.0.5")
}
```

## 1.2 拓展依赖

### 1.2.1 Netty

Maven:

```xml
<dependency>
    <groupId>plus.kat</groupId>
    <artifactId>kat-netty</artifactId>
    <version>0.0.5</version>
</dependency>
```

Java:

```java
import plus.kat.*;
import plus.kat.netty.buffer.*;

ByteBuf buf = ChanBuf.wrappedBuffer(chan);
ByteBuf buf = ChanBuf.wrappedBuffer(chan.getFlow());

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
    <version>0.0.5</version>
</dependency>
```

Java:

```java
import plus.kat.*;
import plus.kat.spring.http.*;

@Configuration
public class Application implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(
        List<HttpMessageConverter<?>> converters
    ) {
        // kat
        converters.add(
            new MutableHttpMessageConverter(Algo.KAT)
        );
        // xml
        converters.add(
            new MutableHttpMessageConverter(Algo.DOC)
        );
        // json
        converters.add(
            0, new MutableHttpMessageConverter(Algo.JSON)
        );
    }
}
```

#### 1.2.3 Okhttp

Maven:

```xml
<dependency>
    <groupId>plus.kat</groupId>
    <artifactId>kat-okhttp</artifactId>
    <version>0.0.5</version>
</dependency>
```

#### 1.2.4 Retrofit

Maven:

```xml
<dependency>
    <groupId>plus.kat</groupId>
    <artifactId>kat-retrofit</artifactId>
    <version>0.0.5</version>
</dependency>
```

# 2. 简单使用

### 2.1 **Data** to **Text**

Java:

```java
import plus.kat.*;

// kat
String kat = Kat.pure(obj);
String kat = Kat.encode(obj);
String kat = Kat.pretty(obj);

// xml
String xml = Doc.encode(obj);
String xml = Doc.pretty(obj);

// json
String json = Json.encode(obj);
String json = Json.pretty(obj);
```

### 2.2 **Text** to **Map**

Java:

```java
import plus.kat.*;

// kat
HashMap<String, Object> data = Kat.decode(
    HashMap.class, "{:id(1):name(kraity)}"
);

// xml
HashMap<String, String> data = Doc.decode(
    HashMap.class, "<user><id>1</id><name>kraity</name></user>"
);

// json
HashMap<String, Object> data = Json.decode(
    HashMap.class, "{\"id\":1,\"name\":\"kraity\"}"
);
```

### 2.3 **Text** to **List**

Java:

```java
import plus.kat.*;

// kat
ArrayList<Integer> data = Kat.decode(
    ArrayList.class, "{(1)(2)(3)}"
);

// xml
ArrayList<String> data = Doc.decode(
    ArrayList.class, "<list><item>1</item><item>2</item></list>"
);

// json
ArrayList<Integer> data = Json.decode(
    ArrayList.class,  "[1,2,3]"
);
```

### 2.4 **Text** to **Object**

Bean:

```java
import plus.kat.anno.*;

@Embed
class User {
    @Expose("id")
    private int id;

    @Expose("name")
    private String name;
}
```

Java:

```java
import plus.kat.*;

// kat
User user = Kat.decode(
    User.class, "{:id(1):name(kraity)}"
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

Bean:

```java
import plus.kat.anno.*;

@Embed
interface Meta {
    int getId();

    String getTag();
}
```

Java:

```java
import plus.kat.*;

// kat
Meta meta = Kat.decode(
    Meta.class, "{:id(1):tag(kat.plus)}"
);

// xml
Meta meta = Doc.decode(
    Meta.class, "<meta><id>1</id><tag>kat.plus</tag></meta>"
);

// json
Meta meta = Json.decode(
    Meta.class, "{\"id\":1,\"tag\":\"kat.plus\"}"
);
```

### 2.5 **Chan** to **Text**

Java:

```java
import plus.kat.*;

// kat
try (Kat kat = new Kat()) {
    kat.set("meta", it -> {
        it.set("id", 100001);
        it.set("title", "kat");
        it.set("author", "User", user -> {
            user.set("id", 1);
            user.set("name", "kraity");
        });
    });

    // M:meta{i:id(100001)s:title(kat)User:author{i:id(1)s:name(kraity)}}
    byte[] src = kat.toBytes();
    String text = kat.toString();
}

// json
try (Json json = new Json()) {
    json.set("meta", it -> {
        it.set("id", 100001);
        it.set("title", "kat");
        it.set("author", "User", user -> {
            user.set("id", 1);
            user.set("name", "kraity");
        });
    });

    // {"id":100001,"title":"kat","author":{"id":1,"name":"kraity"}}
    byte[] src = json.toBytes();
    String text = json.toString();
}

// xml
try (Doc doc = new Doc()) {
    doit.set("Story", it -> {
        it.set("id", 100001);
        it.set("title", "kat");
        it.set("author", "User", user -> {
            user.set("id", 1);
            user.set("name", "kraity");
        });
    });

    // <Story><id>100001</id><title>kat</title><author><id>1</id><name>kraity</name></author></Story>
    byte[] src = doc.toBytes();
    String text = doc.toString();
}
```

Kotlin:

````kotlin
import plus.kat.*

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

Bean:

```java
import plus.kat.anno.*;

@Embed
class User {
    @Expose("id")
    private int id;

    @Expose("name")
    private String name;
}
```

Java:

```java
import plus.kat.*;

// register User
Spare<User> spare = Spare.lookup(User.class);

// kat
User user = spare.read(
    "{:id(1):name(kraity)}"
);
Kat kat = spare.write(user);

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
    "{:id(1):name(kraity)}"
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


// convert between beans
Bean bean = new Bean(
    1, "kraity"
);
User user = spare.cast(bean);
```

FLAT:

```java
import plus.kat.*;

User user = ...
Spare<User> spare = ...

// Visitor
spare.flat(
    user, (key, val) -> {
        String k = key;
        Object v = val;
    }
);

// Visitor
Map<String, Object> collector = ...
spare.flat(
    user, collector::put
);

int id = (int) collector.get("id");
String name = (String) collector.get("name");

// Spoiler
Spoiler spoiler = spare.flat(user);
while (spoiler.hasNext()) {
    String key = spoiler.getKey();
    Object val = spoiler.getValue();
}
```

JDBC:

```java
import java.sql.*;
import plus.kat.*;

// register User
Spare<User> spare = Spare.lookup(User.class);

// test mysql database
String user = "test_user";
String password = "test_password";
String url = "jdbc:mysql://localhost:3306/test_database";

Class.forName("com.mysql.cj.jdbc.Driver");
Connection conn = DriverManager.getConnection(
    url, user, password
);

Statement st = conn.createStatement();
ResultSet rs = st.executeQuery(
    "SELECT `id`, `name` FROM test_user LIMIT 6"
);

List<User> users = new ArrayList<>();
while (rs.next()) {
    users.add(
        spare.apply(rs)
    );
}

rs.close();
st.close();
conn.close();

// print for observation
System.out.println(
    Json.encode(
        users, Flag.PRETTY
    )
);
```

### 3.1 Use **Event**

Create event:

```java
import plus.kat.*;

// create event
Event<?> event = new Event<>();

// specify supplier
event.with(
    Supplier.ins()
);

// specify reader
Reader reader = ...;
event.with(reader);

// specify type
event.with(User.class);

// specify flag
event.with(Flag.INDEX_AS_ENUM);
```

Extends event:

```java
import plus.kat.*;
import java.util.*;

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
import plus.kat.*;

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

// use byte array
byte[] data = ...;
Event<User> event = new Event<>(data);

// use ByteBuffer
ByteBuffer buffer = ...;
Event<User> event = new Event<>(buffer);

// use InputStream
InputStream stream = ...;
Event<User> event = new Event<>(stream);
```

# 4. 进阶使用

### 4.1 Use **Supplier**

Bean:

```java
import plus.kat.anno.*;

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
import plus.kat.*;

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
        "{:id(1):name(kraity)}"
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
import plus.kat.*;

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
        "{:id(1):name(kraity)}"
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
import plus.kat.*;

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
import plus.kat.*;

// your Supplier
Supplier supplier = ...;

// class type
supplier.revoke(User.class);

// package name
supplier.revoke("plus.kat.entity.User");
```

### 4.2 Use custom **Coder**

Bean:

```java
import plus.kat.anno.*;
import plus.kat.spare.*;

@Embed
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
import plus.kat.*;
import plus.kat.chan.*;
import plus.kat.spare.*;

class StatusCoder implements Coder<String> {

    @Override
    public String read(
        Flag flag,
        Value value
    ) throws IOException {
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
    ) throws IOException {
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

Bean:

```java
import plus.kat.anno.*;

// POJO
class User {
    private int id;
    private String name;
    private boolean blocked;
    private User collaborator;
}

@Embed
class Note {
    @Expose("id")
    private int id;

    @Expose(value = "author", with = AuthorCoder.class)
    private User author;
}
```

Java:

```java
import plus.kat.*;
import plus.kat.chain.*;
import plus.kat.spare.*;

import java.io.IOException;

class AuthorCoder implements Coder<User> {

    @Override
    public Boolean getFlag() {
        return true;
    }

    @Override
    public void write(
        Chan chan,
        Object value
    ) throws IOException {
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
    public void onOpen() {
        user = new User();
    }

    @Override
    public Pipage onOpen(
        Space space,
        Alias alias
    ) throws IOException {
        // check key
        if (alias.is("collaborator")) {
            // by supplier
            Spare<?> spare =
                supplier.lookup(User.class);

            // child builder
            Builder<?> child = new AuthorBuilder();

            return child.init(this, (node, result) -> {
                user.setCollaborator(
                    (User) result
                );
            });
        }

        throw new IOException(
            "Unknown attribute: " + alias
        );
    }

    @Override
    public void onEmit(
        Space space,
        Alias alias,
        Value value
    ) {
        if (alias.is("id")) {
            user.setId(
                value.toInt()
            );
        } else if (alias.is("name")) {
            user.setName(
                value.toString()
            );
        } else if (alias.is("blocked")) {
            user.setBlocked(
                value.toBoolean()
            );
        } else {
            throw new IOException(
                "Unknown attribute: " + alias
            );
        }
    }

    @Override
    public User build() {
        return user;
    }

    @Override
    public void onClose() {
        user = null;
    }
}
```

### 4.4 Use custom **Spare**

Bean:

```java
import plus.kat.anno.*;

@Embed(with = UserSpare.class)
class User {
    private int id;
    private String name;
}
```

Java:

```java
import plus.kat.*;

class UserSpare implements Spare<User> {

    @Override
    public String getSpace() {
        return "plus.kat.entity.User";
    }

    @Override
    public Boolean getFlag() {
        return true;
    }

    @Override
    public Boolean getBorder(
        Flag flag
    ) {
        return null;
    }

    @Override
    public Class<User> getType() {
        return User.class;
    }

    @Override
    public void write(
        Chan chan,
        Object value
    ) throws IOException {
        // see: 4.3 Use custom Coder
    }

    @Override
    public Builder<User> getBuilder(Type type) {
        // see: 4.3 Use custom Builder
        return new UserBuilder();
    }
}
```
