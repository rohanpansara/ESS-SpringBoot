package com.employeselfservice.services;

import com.employeselfservice.dao.TeamMemberDAO;
import com.employeselfservice.dao.request.AddTeamRequest;
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
    private ProjectService projectService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private ProjectTaskService projectTaskService;

    public boolean addTeam(AddTeamRequest addTeamRequest){
        Team team = new Team();
        team.setName(addTeamRequest.getTeamName());
        if(addTeamRequest.getTeamDescription().isEmpty()){
            team.setDescription(addTeamRequest.getTeamName()+" Team Description");
        } else{
            team.setDescription(addTeamRequest.getTeamDescription());
        }
        Team savedTeam = teamRepository.save(team);
        return teamRepository.findById(savedTeam.getId()).isPresent();
    }

    public Team findById(Long id){
        return teamRepository.findById(id).get();
    }

    public List<Team> getAllTeams(){
        return teamRepository.findAll();
    }

    public List<TeamMemberDAO> getTeamMembersDetailsOfManager(Long managerId) {
        // Find the team managed by the manager
        Team team = teamRepository.findById(managerId).get();
        System.out.println(team);

        List<TeamMemberDAO> teamMembersDetails = new ArrayList<>();
        List<Employee> teamMembers = employeeService.findAllTeamMembers(team);

        for (Employee employee : teamMembers) {
            int projectsAssigned = projectService.getProjectsAssignedToTheEmployee(employee.getId()).size();
            int tasksAssigned = projectTaskService.getAllTaskForEmployee(employee.getId()).size();
            int tasksDone = projectTaskService.getAllTaskDoneByEmployee(employee.getId()).size();

            TeamMemberDAO teamMemberDAO = new TeamMemberDAO(
                    employee.getId(),
                    employee.getFirstname()+" "+employee.getLastname(),
                    employee.getDateOfJoining(),
                    employee.getDesignation(),
                    projectsAssigned,
                    tasksAssigned,
                    tasksDone
            );

            teamMembersDetails.add(teamMemberDAO);
        }

        return teamMembersDetails;
    }
}
