package com.texas.smart.job.portal.modules.auth.mapper;

import com.texas.smart.job.portal.modules.auth.dto.response.UserResponse;
import com.texas.smart.job.portal.modules.auth.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}