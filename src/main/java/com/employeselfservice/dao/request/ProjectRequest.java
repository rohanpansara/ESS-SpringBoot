package com.employeselfservice.dao.request;

import com.employeselfservice.models.Project.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectRequest {

    private Long ownerId;
    private String projectName;
    private String projectKey;
    private String projectDescription;
    private LocalDate projectCreatedOn;
    private LocalDate projectInitiation;
    private LocalDate projectDeadline;
//    private List<Long> projectMemberIds;
    private ProjectStatus projectStatus;
    private int projectProgress;
    private LocalDate projectLastActivity;

    public String generateKey(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder keyBuilder = new StringBuilder();

        String[] words = input.split("\\s+");

        for (String word : words) {
            if (!word.isEmpty()) {
                keyBuilder.append(Character.toUpperCase(word.charAt(0)));
            }
        }

        return keyBuilder.toString();
    }

}

