package com.employeselfservice.services;

import com.employeselfservice.dto.response.AttendanceDTO;
import com.employeselfservice.models.Attendance;
import com.employeselfservice.models.Employee;
import com.employeselfservice.models.PunchIn;
import com.employeselfservice.models.PunchOut;
import com.employeselfservice.repositories.AttendanceRepository;
import com.employeselfservice.repositories.PunchInRepository;
import com.employeselfservice.repositories.PunchOutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private PunchInRepository punchInRepository;

    @Autowired
    private PunchInService punchInService;

    @Autowired
    private PunchOutRepository punchOutRepository;

    @Autowired
    private PunchOutService punchOutService;

    public Attendance calculateAttendance(Long employeeId, LocalDate date) {
        // check if an attendance record already exists for the given employee and date
        Optional<Attendance> existingAttendanceOptional = attendanceRepository.findByEmployeeIdAndDate(employeeId, date);
        if (existingAttendanceOptional.isPresent()) {
            // if an attendance record already exists, update it with new work hours
            Attendance existingAttendance = existingAttendanceOptional.get();
            updateWorkHours(existingAttendance, employeeId, date);
            return attendanceRepository.save(existingAttendance);
        } else {
            // if no attendance record exists, create a new one
            return createNewAttendance(employeeId, date);
        }
    }

    private void updateWorkHours(Attendance attendance, Long employeeId, LocalDate date) {
        // fetch PunchIn and PunchOut records for the employee and date from repositories
        List<PunchIn> punchIns = punchInRepository.findByEmployeeIdAndDate(employeeId, date);
        List<PunchOut> punchOuts = punchOutRepository.findByEmployeeIdAndDate(employeeId, date);

        // calculate work hrs and set in attendance entity
        attendance.calculateWorkHours(punchIns, punchOuts);
    }

    private Attendance createNewAttendance(Long employeeId, LocalDate date) {
        // fetch PunchIn and PunchOut records for the employee and date
        List<PunchIn> punchIns = punchInRepository.findByEmployeeIdAndDate(employeeId, date);
        List<PunchOut> punchOuts = punchOutRepository.findByEmployeeIdAndDate(employeeId, date);

        Attendance attendance = new Attendance();
        attendance.setEmployee(new Employee(employeeId));
        attendance.setDate(date);

        // calculate work hrs and set in attendance entity
        attendance.calculateWorkHours(punchIns, punchOuts);

        // save the new attendance record
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceWhereEmployeeIsPR(Long employeeId) {
        List<Attendance> allAttendances = attendanceRepository.findByEmployeeId(employeeId);
        List<Attendance> filteredAttendances = new ArrayList<>();

        for (Attendance attendance : allAttendances) {
            String workHoursString = attendance.getWorkHours();
            if (workHoursString != null) {
                String[] parts = workHoursString.split(":");
                if (parts.length == 2) {
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1].substring(0, 2)); // removing 'pm' or 'am'

                    // calculate total duration in mins
                    long totalMinutes = hours * 60L + minutes;

                    // convert total duration to Duration object
                    Duration duration = Duration.ofMinutes(totalMinutes);

                    // compare with 7 hrs and 30 mins
                    if (duration.compareTo(Duration.ofHours(7).plusMinutes(30)) > 0) {
                        // if duration is more than 7 hrs and 30 mins, add attendance to filtered list
                        filteredAttendances.add(attendance);
                    }
                }
            }
        }

        return filteredAttendances;
    }

    public List<Attendance> findAllByEmployeeId(Long id){
        return attendanceRepository.findByEmployeeId(id);
    }

    public Map<LocalDateTime, String> mergePunchInsAndPunchOuts(List<PunchIn> punchIns, List<PunchOut> punchOuts) {
        Map<LocalDateTime, String> punchMap = new TreeMap<>(); // to maintain sorted order by time

        // add PunchIn times
        for (PunchIn punchIn : punchIns) {
            punchMap.put(punchIn.getPunchInTime(), "IN");
        }

        // add PunchOut times
        for (PunchOut punchOut : punchOuts) {
            punchMap.put(punchOut.getPunchOutTime(), "OUT");
        }

        return punchMap;
    }

    public String calculateAverageWorkHours() {
        List<String> allWorkHours = attendanceRepository.findAllWorkHours();

        // calculate total work hours
        Duration totalWorkHours = Duration.ZERO;
        for (String workHours : allWorkHours) {
            String[] parts = workHours.split(":");
            long hours = Long.parseLong(parts[0]);
            long minutes = Long.parseLong(parts[1]);
            totalWorkHours = totalWorkHours.plusHours(hours).plusMinutes(minutes);
        }

        // calculate average work hours
        long totalEmployees = allWorkHours.size();
        Duration averageDuration = totalWorkHours.dividedBy(totalEmployees);

        // convert average duration to hours and minutes
        long averageHours = averageDuration.toHours();
        long averageMinutes = averageDuration.toMinutesPart();

        return String.format("%02d:%02d", averageHours, averageMinutes);
    }

    public List<AttendanceDTO> getAttendanceForMonth(Long employeeId, int month, int year) {

        Employee employee = employeeService.findEmployeeById(employeeId);

        List<AttendanceDTO> attendanceDTOs = new ArrayList<>();

        // validate the month parameter
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid month value. Month should be between 1 and 12.");
        }

        // retrieve attendances for the specified month
        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndDateMonth(employeeId, month);

        // calculate metrics and create AttendanceDTO for the month
        if (!attendances.isEmpty()) {
            String averageWorkHours = calculateAverageWorkHours(attendances);
            double totalWorkHours = calculateTotalWorkHours(attendances);
            int earlyIns = calculateEarlyIns(attendances);
            int lateOuts = calculateLateOuts(attendances);
            double leavesApplied = leaveService.getTotalLeavesAppliedForEmployeeInMonth(employeeId, month, LocalDate.now().getYear());
            double leavesApproved = leaveService.getTotalLeavesApprovedForEmployeeInMonth(employeeId, month, LocalDate.now().getYear());

            AttendanceDTO attendanceDTO = new AttendanceDTO();
            attendanceDTO.setEmployeeId(employeeId);
            attendanceDTO.setEmployeeName(employee.getFirstname()+" "+employee.getLastname());
            attendanceDTO.setTeamName(employee.getTeam().getName());
            attendanceDTO.setAverageWorkHours(averageWorkHours);
            attendanceDTO.setTotalWorkHours(formatTotalWorkHours(totalWorkHours));
            attendanceDTO.setEarlyIns(earlyIns);
            attendanceDTO.setLateOuts(lateOuts);
            attendanceDTO.setLeavesApplied((int) leavesApplied);
            attendanceDTO.setLeavesApproved((int) leavesApproved);
            attendanceDTOs.add(attendanceDTO);
        } else {
            // if no attendances found for the month, create an empty AttendanceDTO
            AttendanceDTO emptyDTO = createEmptyAttendanceDTO(employeeId, month);
            attendanceDTOs.add(emptyDTO);
        }

        return attendanceDTOs;
    }


    // method to create an empty AttendanceDTO with default values
    private AttendanceDTO createEmptyAttendanceDTO(Long employeeId, int month) {
        Employee employee = employeeService.findEmployeeById(employeeId);

        double leavesApplied = leaveService.getTotalLeavesAppliedForEmployeeInMonth(employeeId, month, LocalDate.now().getYear());
        double leavesApproved = leaveService.getTotalLeavesApprovedForEmployeeInMonth(employeeId, month, LocalDate.now().getYear());

        AttendanceDTO emptyDTO = new AttendanceDTO();
        emptyDTO.setEmployeeId(employeeId);
        emptyDTO.setEmployeeName(employee.getFirstname()+" "+employee.getLastname());
        emptyDTO.setTeamName(employee.getTeam().getName());
        emptyDTO.setAverageWorkHours("00:00");
        emptyDTO.setTotalWorkHours("00:00");
        emptyDTO.setEarlyIns(0);
        emptyDTO.setLateOuts(0);
        emptyDTO.setLeavesApplied((int) leavesApplied);
        emptyDTO.setLeavesApproved((int) leavesApproved);
        return emptyDTO;
    }


    private String calculateAverageWorkHours(List<Attendance> attendances) {
        double totalWorkMinutes = 0;
        int daysWithEntry = 0;

        for (Attendance attendance : attendances) {
            if (attendance.getWorkHours() != null) {
                String[] workHoursParts = attendance.getWorkHours().split(":");
                int hours = Integer.parseInt(workHoursParts[0]);
                int minutes = Integer.parseInt(workHoursParts[1]);
                totalWorkMinutes += (hours * 60) + minutes;
                daysWithEntry++;
            }
        }

        if (daysWithEntry == 0) {
            return "00:00"; // no entries for the month
        }

        double averageWorkMinutes = totalWorkMinutes / daysWithEntry;
        int averageHours = (int) (averageWorkMinutes / 60);
        int averageMinutes = (int) (averageWorkMinutes % 60);
        return String.format("%02d:%02d", averageHours, averageMinutes);
    }

    private double calculateTotalWorkHours(List<Attendance> attendances) {
        double totalWorkHours = 0;

        for (Attendance attendance : attendances) {
            if (attendance.getWorkHours() != null) {
                String[] workHoursParts = attendance.getWorkHours().split(":");
                int hours = Integer.parseInt(workHoursParts[0]);
                int minutes = Integer.parseInt(workHoursParts[1]);
                totalWorkHours += (hours + (minutes / 60.0));
            }
        }

        return totalWorkHours;
    }

    private String formatTotalWorkHours(double totalWorkHours) {
        int hours = (int) totalWorkHours;
        int minutes = (int) ((totalWorkHours - hours) * 60);
        return String.format("%02d:%02d", hours, minutes);
    }

    private int calculateEarlyIns(List<Attendance> attendances) {
        int earlyIns = 0;
        LocalTime thresholdTime = LocalTime.of(10, 0);

        for (Attendance attendance : attendances) {
            if (attendance.getFirstPunchIn() != null && attendance.getFirstPunchIn().toLocalTime().isBefore(thresholdTime)) {
                earlyIns++;
            }
        }

        return earlyIns;
    }

    private int calculateLateOuts(List<Attendance> attendances) {
        int lateOuts = 0;
        LocalTime thresholdTime = LocalTime.of(18, 0);

        for (Attendance attendance : attendances) {
            if (attendance.getLastPunchOut() != null && attendance.getLastPunchOut().toLocalTime().isAfter(thresholdTime)) {
                lateOuts++;
            }
        }

        return lateOuts;
    }

}