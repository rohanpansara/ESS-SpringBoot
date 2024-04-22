package com.employeselfservice.dto.request;

import com.employeselfservice.models.Project.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectRequest {

    private String projectName;
    private String projectDescription;
    private LocalDate projectInitiation;
    private LocalDate projectDeadline;
    private ProjectStatus projectStatus;

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

