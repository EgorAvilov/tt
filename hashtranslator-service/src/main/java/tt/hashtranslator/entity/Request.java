package tt.hashtranslator.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Document("requests")
public class Request {

    @Id
    private String id;

    private Map<String, String> hashes = new HashMap<>();
}
