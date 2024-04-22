package com.employeselfservice.dto;

import com.employeselfservice.models.Notifications;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseDAO {
    private Long employeeId;
    private List<Notifications> notificationsList;
    private String firstName;
    private String lastName;
}
