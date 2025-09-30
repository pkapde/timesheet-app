package com.timesheet.controller;

import com.timesheet.dto.ActivityDTO;
import com.timesheet.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/recent/{userId}")
    public ResponseEntity<?> getRecentActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<ActivityDTO> activities = activityService.getRecentActivities(userId, limit);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            System.err.println("Error fetching recent activities: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to fetch recent activities: " + e.getMessage());
        }
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<?> getManagerActivities(
            @PathVariable Long managerId,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<ActivityDTO> activities = activityService.getManagerActivities(managerId, limit);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            System.err.println("Error fetching manager activities: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to fetch manager activities: " + e.getMessage());
        }
    }
}
