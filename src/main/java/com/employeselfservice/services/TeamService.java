package com.employeselfservice.services;

import com.employeselfservice.dto.response.TeamMemberDTO;
import com.employeselfservice.dto.request.AddTeamRequestDTO;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.Team;
import com.employeselfservice.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public boolean addTeam(AddTeamRequestDTO addTeamRequestDTO){
        Team team = new Team();
        team.setName(addTeamRequestDTO.getTeamName());
        if(addTeamRequestDTO.getTeamDescription().isEmpty()){
            team.setDescription(addTeamRequestDTO.getTeamName()+" Team Description");
        } else{
            team.setDescription(addTeamRequestDTO.getTeamDescription());
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

    public List<TeamMemberDTO> getTeamMembersDetailsOfManager(Long managerId) {
        // Find the team managed by the manager
        Team team = teamRepository.findById(managerId).get();
        System.out.println(team);

        List<TeamMemberDTO> teamMembersDetails = new ArrayList<>();
        List<Employee> teamMembers = employeeService.findAllTeamMembers(team);

        for (Employee employee : teamMembers) {
            int projectsAssigned = projectService.getProjectsAssignedToTheEmployee(employee.getId()).size();
            int tasksAssigned = projectTaskService.getAllTaskForEmployee(employee.getId()).size();
            int tasksDone = projectTaskService.getAllTaskDoneByEmployee(employee.getId()).size();

            TeamMemberDTO teamMemberDTO = new TeamMemberDTO(
                    employee.getId(),
                    employee.getFirstname()+" "+employee.getLastname(),
                    employee.getDateOfJoining(),
                    employee.getDesignation(),
                    projectsAssigned,
                    tasksAssigned,
                    tasksDone
            );

            teamMembersDetails.add(teamMemberDTO);
        }

        return teamMembersDetails;
    }
}
