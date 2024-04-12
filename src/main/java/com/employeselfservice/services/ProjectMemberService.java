package com.employeselfservice.services;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Project;
import com.employeselfservice.models.ProjectMember;
import com.employeselfservice.repositories.ProjectMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberService {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    public ProjectMember findProjectMember(Long id) {
        return projectMemberRepository.findById(id).get();
    }

    public List<Employee> findAllProjectMembers(Long projectId) {
        return projectMemberRepository.findAllProjectMembersByProjectId(projectId);
    }

    public boolean addProjectMembers(Project project, List<Long> selectedMembers) {
        // Loop through the selected members and add them to the project
        for (Long memberId : selectedMembers) {
            // Check if the member is already associated with the project
            if (!projectMemberRepository.existsByProjectIdAndEmployeeId(project.getId(), memberId)) {
                // Create a new ProjectMember instance
                ProjectMember projectMember = new ProjectMember();
                projectMember.setProject(project);

                // Fetch the employee entity using its ID
                Employee employee = new Employee();
                employee.setId(memberId);
                projectMember.setEmployee(employee);

                // Persist the ProjectMember
                projectMemberRepository.save(projectMember);
            } else {
                return false;
            }
        }
        return true;
    }
}