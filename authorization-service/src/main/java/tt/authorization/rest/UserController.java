package tt.authorization.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tt.authorization.dto.NewUserDto;
import tt.authorization.entity.Role;
import tt.authorization.entity.User;
import tt.authorization.response.APIResponse;
import tt.authorization.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<APIResponse> create(@RequestBody NewUserDto userDto) {
        log.info("saving {}", userDto.getUsername());
        APIResponse apiResponse = new APIResponse();

        User user = mapDtoToEntity(userDto);
        userService.create(user);
        apiResponse.setData(userDto);
        apiResponse.setMessage("Successfully saved user");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    private User mapDtoToEntity(NewUserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.USER);
        return user;
    }
}
