package com.example.backend;

import com.example.backend.entities.LeaveType;
import com.example.backend.entities.User;
import com.example.backend.repositories.LeaveTypeRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.LeaveTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LeaveTypeServiceTest {

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LeaveTypeService leaveTypeService;

    private User activeUser;

    private final String VACATION = "Vacation";
    private final String SICKLEAVE= "Sick Leave";
    private final String UNPAID = "Unpaid";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        activeUser = new User();
        activeUser.setId(1);
        activeUser.setIsActive(true);
    }

    @Test
    void getAllLeaveTypesForUser() {
        LeaveType vacationLeave = new LeaveType(VACATION, 15, activeUser);
        LeaveType sickLeave = new LeaveType(SICKLEAVE, 185, activeUser);
        LeaveType unpaidLeave = new LeaveType(UNPAID, 75, activeUser);
        List<LeaveType> leaveTypeList = List.of(vacationLeave, sickLeave, unpaidLeave);

        Map<String, Integer> expected = new HashMap<>();
        expected.put(VACATION, 15);
        expected.put(SICKLEAVE, 185);
        expected.put(UNPAID, 75);

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(leaveTypeRepository.findByUserId(1)).thenReturn(leaveTypeList);

        Map<String, Integer> result = leaveTypeService.getAllLeaveTypesForUser(1);

        assertEquals(3, result.size());
        assertEquals(expected, result);

    }

    @Test
    void testGetOrCreateLeaveType_ReturnsExisting() {
        LeaveType leaveType = new LeaveType(VACATION, 21, activeUser);
        when(leaveTypeRepository.findByUserIdAndName(1, VACATION)).thenReturn(Optional.of(leaveType));

        LeaveType result = leaveTypeService.getOrCreateLeaveType(1, VACATION, 21);

        assertEquals(leaveType, result);
    }

    @Test
    void testGetOrCreateLeaveType_CreatesNew() {
        when(leaveTypeRepository.findByUserIdAndName(1, VACATION)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(leaveTypeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeaveType result = leaveTypeService.getOrCreateLeaveType(1, VACATION, 21);

        assertEquals(VACATION, result.getName());
        assertEquals(21, result.getBalanceDays());

        verify(leaveTypeRepository, times(1)).save(any());
      }

    @Test
    void testIsUserActive_True() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));

        assertTrue(leaveTypeService.isUserActive(1));
    }

    @Test
    void testIsUserActive_False_NoUser() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertFalse(leaveTypeService.isUserActive(2));
    }

    @Test
    void testGetVacationBalance_ActiveUser() {
        LeaveType leaveType = new LeaveType(VACATION, 21, activeUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(leaveTypeRepository.findByUserIdAndName(1, VACATION)).thenReturn(Optional.of(leaveType));

        int balance = leaveTypeService.getVacationBalance(1);
        assertEquals(21, balance);
    }

    @Test
    void testGetVacationBalance_InactiveUser() {
        activeUser.setIsActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));

        int balance = leaveTypeService.getVacationBalance(1);
        assertEquals(-1, balance);
    }

    @Test
    void testSetVacationBalance_Success() {
        LeaveType leaveType = new LeaveType(VACATION, 21, activeUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(leaveTypeRepository.findByUserIdAndName(1, VACATION)).thenReturn(Optional.of(leaveType));

        ResponseEntity<?> response = leaveTypeService.setVacationBalance(1, 25);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(25, leaveType.getBalanceDays());
    }

    @Test
    void testSetVacationBalance_UserInactive() {
        activeUser.setIsActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));

        ResponseEntity<?> response = leaveTypeService.setVacationBalance(1, 25);
        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void testSetVacationBalance_NoLeaveType() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(leaveTypeRepository.findByUserIdAndName(1, VACATION)).thenReturn(Optional.empty());

        ResponseEntity<?> response = leaveTypeService.setVacationBalance(1, 25);
        assertEquals(400, response.getStatusCodeValue());
    }
}
