package com.mbancer.web.rest.mapper;

import com.mbancer.domain.Authority;
import com.mbancer.domain.User;
import com.mbancer.web.rest.dto.UserDTO;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for the entity User and its DTO UserDTO.
 */
@Mapper(componentModel = "spring", uses = {ProjectMapper.class, TaskMapper.class})
public interface UserMapper {

    default UserDTO userToUserDTO(User user){
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO(user);

        if ( userDTO.getAuthorities() != null ) {
            Collection<String> targetCollection = stringsFromAuthorities( user.getAuthorities() );
            if ( targetCollection != null ) {
                userDTO.getAuthorities().addAll( targetCollection );
            }
        }

        return userDTO;
    }

    List<UserDTO> usersToUserDTOs(List<User> users);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "persistentTokens", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activationKey", ignore = true)
    @Mapping(target = "resetKey", ignore = true)
    @Mapping(target = "resetDate", ignore = true)
    @Mapping(target = "password", ignore = true)
    User userDTOToUser(UserDTO userDTO);

    List<User> userDTOsToUsers(List<UserDTO> userDTOs);

    default User userFromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }

    default Set<String> stringsFromAuthorities (Set<Authority> authorities) {
        return authorities.stream().map(Authority::getName)
            .collect(Collectors.toSet());
    }

    default Set<Authority> authoritiesFromStrings(Set<String> strings) {
        return strings.stream().map(string -> {
            Authority auth = new Authority();
            auth.setName(string);
            return auth;
        }).collect(Collectors.toSet());
    }
}
