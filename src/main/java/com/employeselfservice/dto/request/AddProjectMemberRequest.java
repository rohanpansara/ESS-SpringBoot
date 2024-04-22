package com.employeselfservice.dto.request;

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
public class AddProjectMemberRequest {
    private List<Long> members;
    private Long projectId;
}
