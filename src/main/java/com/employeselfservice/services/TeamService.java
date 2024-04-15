package com.employeselfservice.services;

import com.employeselfservice.dao.TeamMemberDAO;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.ProjectTask;
import com.employeselfservice.models.Team;
import com.employeselfservice.repositories.EmployeeRepository;
import com.employeselfservice.repositories.ProjectMemberRepository;
import com.employeselfservice.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private ProjectTaskService projectTaskService;

    public Team findById(Long id){
        return teamRepository.findById(id).get();
    }

    public List<Team> getAllTeams(){
        return teamRepository.findAll();
    }

//    public List<TeamMemberDAO> getTeamMembersDetailsOfManager(Long managerId) {
//        // Find the team managed by the manager
//        Team team = teamRepository.findById(managerId).get();
//
//        List<TeamMemberDAO> teamMembersDetails = new ArrayList<>();
//        List<Employee> teamMembers = employeeService.findAllTeamMembers(team);
//
//        for (Employee employee : teamMembers) {
//            int projectsAssigned = projectMemberService.countProjectsAssigned(employee);
//            int tasksAssigned = projectTaskService.countTasksAssigned(employee);
//            int tasksDone = projectTaskService.countTasksDone(employee);
//            double taskCompletionRatio = tasksAssigned != 0 ? ((double) tasksDone / tasksAssigned) : 0.0;
//
//            TeamMemberDAO teamMemberDAO = new TeamMemberDAO(
//                    employee.getFirstname()+" "+employee.getLastname(),
//                    employee.getDesignation(),
//                    projectsAssigned,
//                    tasksAssigned,
//                    tasksDone
//            );
//
//            teamMembersDetails.add(teamMemberDAO);
//        }
//
//        return teamMembersDetails;
//    }
}
