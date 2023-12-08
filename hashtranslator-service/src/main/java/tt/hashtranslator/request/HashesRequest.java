package tt.hashtranslator.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HashesRequest {
    private List<String> hashes;
}
