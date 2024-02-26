package com.tripweaver.controllers.rest;

import com.tripweaver.controllers.helpers.AuthenticationHelper;
import com.tripweaver.controllers.helpers.contracts.ModelsMapper;
import com.tripweaver.models.User;
import com.tripweaver.models.filterOptions.UserFilterOptions;
import com.tripweaver.models.dtos.UserDto;
import com.tripweaver.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserRestController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final ModelsMapper modelsMapper;

    public UserRestController(UserService userService,
                              AuthenticationHelper authenticationHelper,
                              ModelsMapper modelsMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.modelsMapper = modelsMapper;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@RequestBody @Valid UserDto userDto) {
        return null;
    }

    @PutMapping("/{id}")
    User updateUser(@PathVariable int id) {
        return null;
    }

    @GetMapping("/search")
    public List<User> getAllUsers(@RequestHeader HttpHeaders headers,
                                  @RequestParam(required = false) String username,
                                  @RequestParam(required = false) String email,
                                  @RequestParam(required = false) String firstName,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortOrder) {

        return userService.getAllUsers(new UserFilterOptions(null,null,null,null,null), new User());
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {

        return userService.getUserById(id);
    }


}
