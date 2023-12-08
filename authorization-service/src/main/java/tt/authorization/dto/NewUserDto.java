package tt.authorization.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class NewUserDto {

    private String username;

    private String password;
}
