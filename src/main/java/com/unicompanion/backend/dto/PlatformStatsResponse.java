package com.unicompanion.backend.dto;

import java.util.Map;

public class PlatformStatsResponse {
    private long totalStudents;
    private long totalAdmins;
    private long totalMasterAdmins;
    private long totalUnis;
    private long activeUnis;
    private Map<String, UniversityStat> universityStats;

    public PlatformStatsResponse() {
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(long totalAdmins) {
        this.totalAdmins = totalAdmins;
    }

    public long getTotalMasterAdmins() {
        return totalMasterAdmins;
    }

    public void setTotalMasterAdmins(long totalMasterAdmins) {
        this.totalMasterAdmins = totalMasterAdmins;
    }

    public long getTotalUnis() {
        return totalUnis;
    }

    public void setTotalUnis(long totalUnis) {
        this.totalUnis = totalUnis;
    }

    public long getActiveUnis() {
        return activeUnis;
    }

    public void setActiveUnis(long activeUnis) {
        this.activeUnis = activeUnis;
    }

    public Map<String, UniversityStat> getUniversityStats() {
        return universityStats;
    }

    public void setUniversityStats(Map<String, UniversityStat> universityStats) {
        this.universityStats = universityStats;
    }

    public static class UniversityStat {
        private long students;
        private long admins;

        public UniversityStat(long students, long admins) {
            this.students = students;
            this.admins = admins;
        }

        public long getStudents() {
            return students;
        }

        public void setStudents(long students) {
            this.students = students;
        }

        public long getAdmins() {
            return admins;
        }

        public void setAdmins(long admins) {
            this.admins = admins;
        }
    }
}
