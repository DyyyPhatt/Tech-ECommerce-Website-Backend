package hcmute.tech_ecommerce_website.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdSerializer extends JsonSerializer<ObjectId> {

    @Override
    public void serialize(ObjectId objectId, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (objectId != null) {
            jsonGenerator.writeString(objectId.toHexString());
        } else {
            jsonGenerator.writeNull();
        }
    }
}