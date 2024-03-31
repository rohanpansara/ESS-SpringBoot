package com.employeselfservice.services;

import com.employeselfservice.models.ProjectLog;
import com.employeselfservice.repositories.ProjectLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectLogService {

    @Autowired
    private ProjectLogRepository projectLogRepository;

    public List<ProjectLog> getProjectLog(Long projectId){
        return projectLogRepository.findAllLogsByProjectId(projectId);
    }

}
