# Kat+

Kat+ 一个轻量级的 **KAT** + **XML** + **JSON** 库

- 兼容 **Kotlin**
- 兼容 **Android 6+**

致力于安全性、规范性、拓展性和轻量性的微组件

# 1. 使用准备

## 1.1 添加依赖

Maven:

```xml
<dependency>
    <groupId>plus.kat</groupId>
    <artifactId>kat</artifactId>
    <version>0.0.6</version>
</dependency>
```

Gradle:

```groovy
dependencies {
    implementation 'plus.kat:kat:0.0.6'
}
```

Kotlin Gradle:

```kotlin
dependencies {
    implementation("plus.kat:kat:0.0.6")
}
```

## 1.2 拓展依赖

### 1.2.1 Netty

Maven:

```xml
<dependency>
    <groupId>plus.kat</groupId>
    <artifactId>kat-netty</artifactId>
    <version>0.0.6</version>
</dependency>
```

Java:

```java
import plus.kat.*;
import plus.kat.netty.flow.*;

ByteBuf buf = ...
Flow flow = new ByteBufFlow(buf);

Spare<User> spare = Spare.of(User.class);
User user = spare.read(flow); // see: 3.1

try(Chan chan = spare.write(user)) {
    // Use buffer before calling Chan#close
    ByteBuf buffer = ByteBufStream.of(chan);
}

AsciiString str = ...
Flow flow = new AsciiStringFlow(str);
Flow flow = new AsciiStringFlow(str, index(), length());
```

### 1.2.2 Spring

Maven:

```xml
<dependency>
    <groupId>plus.kat</groupId>
    <artifactId>kat-spring</artifactId>
    <version>0.0.6</version>
</dependency>
```

Java:

```java
import plus.kat.spring.http.*;

@Configuration
public class Application implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(
        List<HttpMessageConverter<?>> converters
    ) {
        // kat (if you need it)
        converters.add(
            new MutableHttpMessageConverter("kat")
        );
        // xml (if you need it)
        converters.add(
            new MutableHttpMessageConverter("xml")
        );
        // json (if you need it)
        converters.add(
            0, new MutableHttpMessageConverter("json")
        );
    }
}
```

Config:

```xml
<beans>
    <mvc:annotation-driven>
        <mvc:message-converters>
            <!-- kat (if you need it) -->
            <bean class="plus.kat.spring.http.MutableHttpMessageConverter">
                <constructor-arg index="0" value="kat"/>
            </bean>
            <!-- xml (if you need it) -->
            <bean class="plus.kat.spring.http.MutableHttpMessageConverter">
                <constructor-arg index="0" value="xml"/>
            </bean>
            <!-- json (if you need it) -->
            <bean class="plus.kat.spring.http.MutableHttpMessageConverter">
                <constructor-arg index="0" value="json"/>
            </bean>
        </mvc:message-converters>
    </mvc:annotation-driven>
</beans>
```

#### 1.2.3 Okhttp

Maven:

```xml
<dependency>
    <groupId>plus.kat</groupId>
    <artifactId>kat-okhttp</artifactId>
    <version>0.0.6</version>
</dependency>
```

#### 1.2.4 Retrofit

Maven:

```xml
<dependency>
    <groupId>plus.kat</groupId>
    <artifactId>kat-retrofit</artifactId>
    <version>0.0.6</version>
</dependency>
```

# 2. 简单使用

### 2.1 **Data** to **Text**

Java:

```java
import plus.kat.*;

// kat
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
    HashMap.class, "{id=1,name=kraity}"
);

// xml
HashMap<String, Object> data = Doc.decode(
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
    ArrayList.class, "[1,2,3]"
);

// xml
ArrayList<Integer> data = Doc.decode(
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
import plus.kat.actor.*;

@Magus
class User {
    @Magic("id")
    private int id;

    @Magic("name")
    private String name;
}
```

Java:

```java
import plus.kat.*;

// kat
User user = Kat.decode(
    User.class, "{id=1,name=kraity}"
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
import plus.kat.actor.*;

@Magus
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
    Meta.class, "{id=1,tag=kat.plus}"
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
    kat.set(null, it -> {
        it.set("id", 100001);
        it.set("title", "kat");
        it.set("author", "User", user -> {
            user.set("id", 1);
            user.set("name", "kraity");
        });
    });

    // {id=100001,title="kat",author={id=1,name="kraity"}}
    byte[] src = kat.toBinary();
    String text = kat.toString();
}

// json
try (Json json = new Json()) {
    json.set(null, it -> {
        it.set("id", 100001);
        it.set("title", "kat");
        it.set("author", "User", user -> {
            user.set("id", 1);
            user.set("name", "kraity");
        });
    });

    // {"id":100001,"title":"kat","author":{"id":1,"name":"kraity"}}
    byte[] src = json.toBinary();
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
    byte[] src = doc.toBinary();
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
import plus.kat.actor.*;

@Magus
class User {
    @Magic("id")
    private int id;

    @Magic("name")
    private String name;
}
```

Java:

```java
import plus.kat.*;

// register User
Spare<User> spare = Spare.of(User.class);

// kat
User user = spare.read(
    Flow.of(
        "{id=1,name=kraity}"
    )
);
Chan kat = spare.write(user);

// xml
User user = spare.down(
    Flow.of(
        "<user><id>1</id><name>kraity</name></user>"
    )
);
Chan doc = spare.mark(user);

// json
User user = spare.parse(
    Flow.of(
        "{\"id\":1,\"name\":\"kraity\"}"
    )
);
Chan json = spare.serial(user);
```

# 4. 进阶使用

### 4.1 Use **Supplier**

Bean:

```java
import plus.kat.actor.*;

@Magus
class User {
    @Magic("id")
    private int id;

    @Magic("name")
    private String name;
}
```

Use class type:

```java
import plus.kat.*;

// default supplier
Supplier supplier = Supplier.ins();

// kat
User user = supplier.read(
    User.class, Flow.of(
        "{id=1,name=kraity}"
    );
);

// xml
User user = supplier.down(
    User.class, Flow.of(
        "<user><id>1</id><name>kraity</name></user>"
    );
);

// json
User user = supplier.parse(
    User.class, Flow.of(
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
supplier.active(
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

// class type and spare
supplier.revoke(User.class, getSpare());
```

### 4.2 Use custom **Coder**

Bean:

```java
import plus.kat.actor.*;
import plus.kat.spare.*;

@Magus
class User {
    @Magic("id")
    private int id;

    @Magic("name")
    private String name;

    @Magic(value = "status", agent = StatusCoder.class)
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
        if (value.equals("PUBLISH")) {
            return "open";
        }

        if (value.equals("PRIVATE")) {
            return "self";
        }

        return "unknown";
    }

    @Override
    public void write(
        Flux flux,
        Object value
    ) throws IOException {
        switch ((String) value) {
            case "open": {
                flux.emit("PUBLISH");
                break;
            }
            case "self": {
                flux.emit("PRIVATE");
                break;
            }
            default: {
                flux.emit("UNKNOWN");
            }
        }
    }
}
```

### 4.3 Use custom **Coder** and **Builder**

Bean:

```java
import plus.kat.actor.*;

// POJO
class User {
    private int id;
    private String name;
    private boolean blocked;
    private User collaborator;
}

@Magus
class Note {
    @Magic("id")
    private int id;

    @Magic(value = "author", agent = AuthorCoder.class)
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
    public Boolean getScope() {
        return true;
    }

    @Override
    public Border getBorder() {
        return Border.BRACE;
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
    public Factory getFactory(Type type) {
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
    public Spider onOpen(
        Alias alias,
        Space space
    ) throws IOException {
        // check key
        if (alias.equals("collaborator")) {
            // by builder
            Builder<?> child =
                new AuthorBuilder();

            return child.init(this, context);
        }

        throw new IOException(
            "Unknown attribute: " + alias
        );
    }

    @Override
    public void onEach(
        Object value
    ){
        user.setCollaborator(
            (User) value
        );
    }

    @Override
    public void onEach(
        Alias alias,
        Space space,
        Value value
    ) {
        if (alias.equals("id")) {
            user.setId(
                value.toInt()
            );
        } else if (alias.equals("name")) {
            user.setName(
                value.toString()
            );
        } else if (alias.equals("blocked")) {
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
import plus.kat.actor.*;

@Magus(agent = UserSpare.class)
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
        return "plus.kat.User";
    }

    @Override
    public Boolean getScope() {
        return true;
    }

    @Override
    public Boolean getBorder(
        Flag flag
    ) {
        return Border.BRACE;
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
    public Factory getFactory(Type type) {
        // see: 4.3 Use custom Builder
        return new UserBuilder();
    }
}
```
