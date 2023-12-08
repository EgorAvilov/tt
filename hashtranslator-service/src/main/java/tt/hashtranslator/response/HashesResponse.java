package tt.hashtranslator.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class HashesResponse {

    private List<Map<String, String>> hashes;
}
