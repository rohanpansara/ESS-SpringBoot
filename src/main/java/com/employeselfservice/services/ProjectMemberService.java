package com.employeselfservice.services;

import com.employeselfservice.models.Employee;
import com.employeselfservice.models.ProjectMember;
import com.employeselfservice.repositories.ProjectMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberService {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    public ProjectMember findProjectMember(Long id){
        return projectMemberRepository.findById(id).get();
    }

    public List<Employee> findAllProjectMembers(Long projectId){
        return projectMemberRepository.findAllProjectMembersByProjectId(projectId);
    }
}