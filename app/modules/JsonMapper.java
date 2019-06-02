package modules;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import play.libs.Json;

public class JsonMapper {

    JsonMapper() {
        Json.setObjectMapper(Json.newDefaultMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
                .registerModule(new Hibernate5Module()));
    }

}
