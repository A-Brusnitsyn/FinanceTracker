package org.brusnitsyn.financetracker.model.mappers;

import org.brusnitsyn.financetracker.model.dto.UserResponse;
import org.brusnitsyn.financetracker.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse userToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .build();
    }

}


