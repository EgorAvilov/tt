
package tt.authorization.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tt.authorization.service.UserService;

import java.util.Base64;

@Slf4j
@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;

    @RequestMapping(value = "/validateToken", method = RequestMethod.GET)
    public ResponseEntity<Boolean> validateToken(@RequestHeader HttpHeaders headers) {
        String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        String base64Credentials = authorizationHeader.substring("Basic".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));

        String[] splitCredentials = credentials.split(":", 2);
        String username = splitCredentials[0];

        UserDetails userDetails = userService.loadUserByUsername(username);
        if (userDetails != null) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }
}

