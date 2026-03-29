CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS rrhh_employees (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    identification VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone VARCHAR(30),
    address VARCHAR(200),
    area VARCHAR(100) NOT NULL,
    dependency VARCHAR(100) NOT NULL,
    job_title VARCHAR(120) NOT NULL,
    contract_type VARCHAR(60) NOT NULL,
    employment_status VARCHAR(40) NOT NULL,
    hire_date DATE NOT NULL,
    termination_date DATE,
    birth_date DATE,
    professional_title VARCHAR(120),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS rrhh_employee_contracts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    contract_number VARCHAR(50) NOT NULL UNIQUE,
    contract_type VARCHAR(60) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    current_contract BOOLEAN NOT NULL DEFAULT FALSE,
    salary NUMERIC(14,2) NOT NULL CHECK (salary >= 0),
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_employee_contract_dates
        CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE IF NOT EXISTS rrhh_employee_files_v1 (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    file_type VARCHAR(60) NOT NULL,
    file_name VARCHAR(160) NOT NULL,
    file_url VARCHAR(255),
    issue_date DATE,
    expiry_date DATE,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_employee_file_dates
        CHECK (expiry_date IS NULL OR issue_date IS NULL OR expiry_date >= issue_date)
);

CREATE TABLE IF NOT EXISTS rrhh_employee_leaves (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    leave_type VARCHAR(50) NOT NULL,
    absence_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days NUMERIC(8,2) NOT NULL CHECK (days >= 0),
    status VARCHAR(40) NOT NULL,
    reason VARCHAR(300),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_employee_leave_dates
        CHECK (end_date >= start_date)
);

CREATE TABLE IF NOT EXISTS rrhh_employee_actions_v1 (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    action_type VARCHAR(60) NOT NULL,
    effective_date DATE NOT NULL,
    status VARCHAR(40) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS rrhh_vacancies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(30) NOT NULL UNIQUE,
    title VARCHAR(140) NOT NULL,
    area VARCHAR(100) NOT NULL,
    dependency VARCHAR(100) NOT NULL,
    stage VARCHAR(50) NOT NULL,
    status VARCHAR(40) NOT NULL,
    open_date DATE NOT NULL,
    close_date DATE,
    budgeted_salary NUMERIC(14,2) CHECK (budgeted_salary IS NULL OR budgeted_salary >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_vacancy_dates
        CHECK (close_date IS NULL OR close_date >= open_date)
);

CREATE TABLE IF NOT EXISTS rrhh_candidates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vacancy_id UUID NOT NULL REFERENCES rrhh_vacancies(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    identification VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL,
    phone VARCHAR(30),
    stage VARCHAR(50) NOT NULL,
    status VARCHAR(40) NOT NULL,
    applied_at DATE NOT NULL,
    score NUMERIC(8,2) CHECK (score IS NULL OR score >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS rrhh_candidate_tests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES rrhh_candidates(id),
    vacancy_id UUID REFERENCES rrhh_vacancies(id),
    test_type VARCHAR(60) NOT NULL,
    test_name VARCHAR(160) NOT NULL,
    scheduled_at TIMESTAMP,
    completed_at TIMESTAMP,
    score NUMERIC(8,2) CHECK (score IS NULL OR score >= 0),
    result VARCHAR(40) NOT NULL,
    evaluator VARCHAR(120),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_candidate_test_dates
        CHECK (completed_at IS NULL OR scheduled_at IS NULL OR completed_at >= scheduled_at)
);

CREATE TABLE IF NOT EXISTS rrhh_interviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vacancy_id UUID NOT NULL REFERENCES rrhh_vacancies(id),
    candidate_id UUID NOT NULL REFERENCES rrhh_candidates(id),
    stage VARCHAR(50) NOT NULL,
    status VARCHAR(40) NOT NULL,
    scheduled_at TIMESTAMP NOT NULL,
    interviewer VARCHAR(120) NOT NULL,
    notes VARCHAR(500),
    score NUMERIC(8,2) CHECK (score IS NULL OR score >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS rrhh_onboarding (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT REFERENCES rrhh_employees(id),
    candidate_id UUID REFERENCES rrhh_candidates(id),
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(40) NOT NULL,
    owner VARCHAR(100) NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_onboarding_dates
        CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_rrhh_onboarding_links
        CHECK (employee_id IS NOT NULL OR candidate_id IS NOT NULL)
);

CREATE TABLE IF NOT EXISTS rrhh_training_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT REFERENCES rrhh_employees(id),
    area VARCHAR(100) NOT NULL,
    title VARCHAR(140) NOT NULL,
    description VARCHAR(500),
    start_date DATE NOT NULL,
    end_date DATE,
    hours NUMERIC(8,2) NOT NULL CHECK (hours >= 0),
    cost NUMERIC(14,2) CHECK (cost IS NULL OR cost >= 0),
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_training_dates
        CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE IF NOT EXISTS rrhh_performance_reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    period VARCHAR(40) NOT NULL,
    score NUMERIC(8,2) NOT NULL CHECK (score >= 0),
    reviewer VARCHAR(120) NOT NULL,
    review_date DATE NOT NULL,
    status VARCHAR(40) NOT NULL,
    comments VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS rrhh_career_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    goal VARCHAR(160) NOT NULL,
    status VARCHAR(40) NOT NULL,
    start_date DATE NOT NULL,
    target_date DATE,
    milestones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_career_plan_dates
        CHECK (target_date IS NULL OR target_date >= start_date)
);

CREATE TABLE IF NOT EXISTS rrhh_mentoring (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    mentor_name VARCHAR(120) NOT NULL,
    coach_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(40) NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_mentoring_dates
        CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE IF NOT EXISTS rrhh_payrolls (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    gross_amount NUMERIC(14,2) NOT NULL CHECK (gross_amount >= 0),
    total_benefits NUMERIC(14,2) NOT NULL CHECK (total_benefits >= 0),
    total_deductions NUMERIC(14,2) NOT NULL CHECK (total_deductions >= 0),
    net_amount NUMERIC(14,2) NOT NULL CHECK (net_amount >= 0),
    overtime_hours NUMERIC(8,2) NOT NULL CHECK (overtime_hours >= 0),
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_payroll_dates
        CHECK (period_end >= period_start)
);

CREATE TABLE IF NOT EXISTS rrhh_payroll_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_id UUID NOT NULL REFERENCES rrhh_payrolls(id) ON DELETE CASCADE,
    item_code VARCHAR(40),
    item_name VARCHAR(140) NOT NULL,
    item_type VARCHAR(30) NOT NULL,
    quantity NUMERIC(12,2) CHECK (quantity IS NULL OR quantity >= 0),
    unit_amount NUMERIC(14,2) CHECK (unit_amount IS NULL OR unit_amount >= 0),
    amount NUMERIC(14,2) NOT NULL CHECK (amount >= 0),
    taxable BOOLEAN NOT NULL DEFAULT FALSE,
    notes VARCHAR(300),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS rrhh_benefits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT REFERENCES rrhh_employees(id),
    benefit_type VARCHAR(60) NOT NULL,
    name VARCHAR(140) NOT NULL,
    cost NUMERIC(14,2) NOT NULL CHECK (cost >= 0),
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_benefit_dates
        CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE IF NOT EXISTS rrhh_incentives (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT REFERENCES rrhh_employees(id),
    incentive_type VARCHAR(60) NOT NULL,
    title VARCHAR(160) NOT NULL,
    amount NUMERIC(14,2) NOT NULL CHECK (amount >= 0),
    granted_date DATE NOT NULL,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS rrhh_climate_surveys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(160) NOT NULL,
    area VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(40) NOT NULL,
    participation_rate NUMERIC(5,2) NOT NULL CHECK (participation_rate >= 0),
    satisfaction_score NUMERIC(5,2) NOT NULL CHECK (satisfaction_score >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_climate_survey_dates
        CHECK (end_date >= start_date)
);

CREATE TABLE IF NOT EXISTS rrhh_climate_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    survey_id UUID NOT NULL REFERENCES rrhh_climate_surveys(id) ON DELETE CASCADE,
    result_date DATE NOT NULL,
    dimension VARCHAR(100) NOT NULL,
    favorable_score NUMERIC(5,2) CHECK (favorable_score IS NULL OR favorable_score >= 0),
    neutral_score NUMERIC(5,2) CHECK (neutral_score IS NULL OR neutral_score >= 0),
    unfavorable_score NUMERIC(5,2) CHECK (unfavorable_score IS NULL OR unfavorable_score >= 0),
    respondents INTEGER CHECK (respondents IS NULL OR respondents >= 0),
    observations TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS rrhh_wellbeing_programs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(160) NOT NULL,
    area VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(40) NOT NULL,
    participation_rate NUMERIC(5,2) NOT NULL CHECK (participation_rate >= 0),
    cost NUMERIC(14,2) NOT NULL CHECK (cost >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_wellbeing_dates
        CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE IF NOT EXISTS rrhh_conflict_cases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id BIGINT REFERENCES rrhh_employees(id),
    title VARCHAR(160) NOT NULL,
    description TEXT,
    status VARCHAR(40) NOT NULL,
    responsible VARCHAR(120) NOT NULL,
    opened_at DATE NOT NULL,
    resolved_at DATE,
    resolution TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT chk_rrhh_conflict_dates
        CHECK (resolved_at IS NULL OR resolved_at >= opened_at)
);

CREATE TABLE IF NOT EXISTS rrhh_audits_v1 (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    audit_type VARCHAR(60) NOT NULL,
    title VARCHAR(160) NOT NULL,
    findings TEXT,
    action_plan TEXT,
    status VARCHAR(40) NOT NULL,
    audit_date DATE NOT NULL,
    responsible VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS rrhh_policies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(40) NOT NULL UNIQUE,
    title VARCHAR(160) NOT NULL,
    version VARCHAR(30) NOT NULL,
    effective_date DATE NOT NULL,
    status VARCHAR(40) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS rrhh_safety_trainings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(160) NOT NULL,
    area VARCHAR(100) NOT NULL,
    training_date DATE NOT NULL,
    hours NUMERIC(8,2) NOT NULL CHECK (hours >= 0),
    attendees INTEGER NOT NULL CHECK (attendees >= 0),
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

CREATE TABLE IF NOT EXISTS rrhh_legal_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_type VARCHAR(60) NOT NULL,
    title VARCHAR(160) NOT NULL,
    reporting_period VARCHAR(40) NOT NULL,
    generated_at TIMESTAMP NOT NULL,
    due_date DATE,
    status VARCHAR(40) NOT NULL,
    responsible VARCHAR(120) NOT NULL,
    file_url VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'system'
);

CREATE INDEX IF NOT EXISTS idx_rrhh_employees_area ON rrhh_employees(area);
CREATE INDEX IF NOT EXISTS idx_rrhh_employees_dependency ON rrhh_employees(dependency);
CREATE INDEX IF NOT EXISTS idx_rrhh_employees_contract_type ON rrhh_employees(contract_type);
CREATE INDEX IF NOT EXISTS idx_rrhh_employees_status ON rrhh_employees(employment_status);
CREATE INDEX IF NOT EXISTS idx_rrhh_employees_hire_date ON rrhh_employees(hire_date);

CREATE INDEX IF NOT EXISTS idx_rrhh_employee_contracts_employee ON rrhh_employee_contracts(employee_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_rrhh_employee_current_contract
    ON rrhh_employee_contracts(employee_id)
    WHERE current_contract = TRUE;

CREATE INDEX IF NOT EXISTS idx_rrhh_employee_files_employee ON rrhh_employee_files_v1(employee_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_employee_leaves_employee ON rrhh_employee_leaves(employee_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_employee_actions_employee ON rrhh_employee_actions_v1(employee_id);

CREATE INDEX IF NOT EXISTS idx_rrhh_vacancies_area ON rrhh_vacancies(area);
CREATE INDEX IF NOT EXISTS idx_rrhh_vacancies_status ON rrhh_vacancies(status);
CREATE INDEX IF NOT EXISTS idx_rrhh_vacancies_stage ON rrhh_vacancies(stage);

CREATE INDEX IF NOT EXISTS idx_rrhh_candidates_vacancy ON rrhh_candidates(vacancy_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_candidates_stage ON rrhh_candidates(stage);
CREATE INDEX IF NOT EXISTS idx_rrhh_candidates_status ON rrhh_candidates(status);

CREATE INDEX IF NOT EXISTS idx_rrhh_candidate_tests_candidate ON rrhh_candidate_tests(candidate_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_candidate_tests_vacancy ON rrhh_candidate_tests(vacancy_id);

CREATE INDEX IF NOT EXISTS idx_rrhh_interviews_candidate ON rrhh_interviews(candidate_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_interviews_vacancy ON rrhh_interviews(vacancy_id);

CREATE INDEX IF NOT EXISTS idx_rrhh_onboarding_employee ON rrhh_onboarding(employee_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_onboarding_candidate ON rrhh_onboarding(candidate_id);

CREATE INDEX IF NOT EXISTS idx_rrhh_training_employee ON rrhh_training_plans(employee_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_performance_employee ON rrhh_performance_reviews(employee_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_career_employee ON rrhh_career_plans(employee_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_mentoring_employee ON rrhh_mentoring(employee_id);

CREATE INDEX IF NOT EXISTS idx_rrhh_payrolls_employee ON rrhh_payrolls(employee_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_payroll_items_payroll ON rrhh_payroll_items(payroll_id);

CREATE INDEX IF NOT EXISTS idx_rrhh_benefits_employee ON rrhh_benefits(employee_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_incentives_employee ON rrhh_incentives(employee_id);

CREATE INDEX IF NOT EXISTS idx_rrhh_climate_results_survey ON rrhh_climate_results(survey_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_conflicts_employee ON rrhh_conflict_cases(employee_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_safety_training_date ON rrhh_safety_trainings(training_date);
CREATE INDEX IF NOT EXISTS idx_rrhh_legal_reports_type ON rrhh_legal_reports(report_type);
