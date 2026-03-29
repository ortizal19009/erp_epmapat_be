package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DashboardResponse {
    long activeEmployees;
    long recentHires;
    long recentTerminations;
    BigDecimal rotationRate;
    BigDecimal absenteeismRate;
    long openVacancies;
    BigDecimal averageHiringDays;
    BigDecimal hiringCost;
    BigDecimal averagePerformanceScore;
    BigDecimal trainingHours;
    BigDecimal trainedPercentage;
    BigDecimal monthlyPayrollCost;
    BigDecimal overtimeHours;
    long activeBenefits;
    BigDecimal jobSatisfaction;
    BigDecimal wellbeingParticipation;
    long reportedConflicts;
    long resolvedConflicts;
}
