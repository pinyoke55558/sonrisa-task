package com.example.alerts.mapper;

import com.example.alerts.dto.UserResponse;
import com.example.alerts.model.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    UserResponse toResponse(User user, List<String> roles);
}
