package com.employeselfservice.services;

import com.employeselfservice.models.ProjectMember;
import com.employeselfservice.repositories.ProjectMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectMemberService {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    public ProjectMember findProjectMember(Long id){
        return projectMemberRepository.findById(id).get();
    }
}