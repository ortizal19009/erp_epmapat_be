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
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_employee_contracts (
    id UUID PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    contract_number VARCHAR(50) NOT NULL UNIQUE,
    contract_type VARCHAR(60) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    current_contract BOOLEAN NOT NULL DEFAULT FALSE,
    salary NUMERIC(14,2) NOT NULL,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_employee_files_v1 (
    id UUID PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    file_type VARCHAR(60) NOT NULL,
    file_name VARCHAR(160) NOT NULL,
    file_url VARCHAR(255),
    issue_date DATE,
    expiry_date DATE,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_employee_leaves (
    id UUID PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    leave_type VARCHAR(50) NOT NULL,
    absence_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days NUMERIC(8,2) NOT NULL,
    status VARCHAR(40) NOT NULL,
    reason VARCHAR(300),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_employee_actions_v1 (
    id UUID PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    action_type VARCHAR(60) NOT NULL,
    effective_date DATE NOT NULL,
    status VARCHAR(40) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_vacancies (
    id UUID PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    title VARCHAR(140) NOT NULL,
    area VARCHAR(100) NOT NULL,
    dependency VARCHAR(100) NOT NULL,
    stage VARCHAR(50) NOT NULL,
    status VARCHAR(40) NOT NULL,
    open_date DATE NOT NULL,
    close_date DATE,
    budgeted_salary NUMERIC(14,2),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_candidates (
    id UUID PRIMARY KEY,
    vacancy_id UUID NOT NULL REFERENCES rrhh_vacancies(id),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    identification VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL,
    phone VARCHAR(30),
    stage VARCHAR(50) NOT NULL,
    status VARCHAR(40) NOT NULL,
    applied_at DATE NOT NULL,
    score NUMERIC(8,2),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_interviews (
    id UUID PRIMARY KEY,
    vacancy_id UUID NOT NULL REFERENCES rrhh_vacancies(id),
    candidate_id UUID NOT NULL REFERENCES rrhh_candidates(id),
    stage VARCHAR(50) NOT NULL,
    status VARCHAR(40) NOT NULL,
    scheduled_at TIMESTAMP NOT NULL,
    interviewer VARCHAR(120) NOT NULL,
    notes VARCHAR(500),
    score NUMERIC(8,2),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_onboarding (
    id UUID PRIMARY KEY,
    employee_id BIGINT REFERENCES rrhh_employees(id),
    candidate_id UUID REFERENCES rrhh_candidates(id),
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(40) NOT NULL,
    owner VARCHAR(100) NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_training_plans (
    id UUID PRIMARY KEY,
    employee_id BIGINT REFERENCES rrhh_employees(id),
    area VARCHAR(100) NOT NULL,
    title VARCHAR(140) NOT NULL,
    description VARCHAR(500),
    start_date DATE NOT NULL,
    end_date DATE,
    hours NUMERIC(8,2) NOT NULL,
    cost NUMERIC(14,2),
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_performance_reviews (
    id UUID PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    period VARCHAR(40) NOT NULL,
    score NUMERIC(8,2) NOT NULL,
    reviewer VARCHAR(120) NOT NULL,
    review_date DATE NOT NULL,
    status VARCHAR(40) NOT NULL,
    comments VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_career_plans (
    id UUID PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    goal VARCHAR(160) NOT NULL,
    status VARCHAR(40) NOT NULL,
    start_date DATE NOT NULL,
    target_date DATE,
    milestones TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_mentoring (
    id UUID PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    mentor_name VARCHAR(120) NOT NULL,
    coach_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(40) NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_payrolls (
    id UUID PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES rrhh_employees(id),
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    gross_amount NUMERIC(14,2) NOT NULL,
    total_benefits NUMERIC(14,2) NOT NULL,
    total_deductions NUMERIC(14,2) NOT NULL,
    net_amount NUMERIC(14,2) NOT NULL,
    overtime_hours NUMERIC(8,2) NOT NULL,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_benefits (
    id UUID PRIMARY KEY,
    employee_id BIGINT REFERENCES rrhh_employees(id),
    benefit_type VARCHAR(60) NOT NULL,
    name VARCHAR(140) NOT NULL,
    cost NUMERIC(14,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_incentives (
    id UUID PRIMARY KEY,
    employee_id BIGINT REFERENCES rrhh_employees(id),
    incentive_type VARCHAR(60) NOT NULL,
    title VARCHAR(160) NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    granted_date DATE NOT NULL,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_climate_surveys (
    id UUID PRIMARY KEY,
    title VARCHAR(160) NOT NULL,
    area VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(40) NOT NULL,
    participation_rate NUMERIC(5,2) NOT NULL,
    satisfaction_score NUMERIC(5,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_wellbeing_programs (
    id UUID PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    area VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(40) NOT NULL,
    participation_rate NUMERIC(5,2) NOT NULL,
    cost NUMERIC(14,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_conflict_cases (
    id UUID PRIMARY KEY,
    employee_id BIGINT REFERENCES rrhh_employees(id),
    title VARCHAR(160) NOT NULL,
    description TEXT,
    status VARCHAR(40) NOT NULL,
    responsible VARCHAR(120) NOT NULL,
    opened_at DATE NOT NULL,
    resolved_at DATE,
    resolution TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_audits_v1 (
    id UUID PRIMARY KEY,
    audit_type VARCHAR(60) NOT NULL,
    title VARCHAR(160) NOT NULL,
    findings TEXT,
    action_plan TEXT,
    status VARCHAR(40) NOT NULL,
    audit_date DATE NOT NULL,
    responsible VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_policies (
    id UUID PRIMARY KEY,
    code VARCHAR(40) NOT NULL UNIQUE,
    title VARCHAR(160) NOT NULL,
    version VARCHAR(30) NOT NULL,
    effective_date DATE NOT NULL,
    status VARCHAR(40) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rrhh_safety_trainings (
    id UUID PRIMARY KEY,
    title VARCHAR(160) NOT NULL,
    area VARCHAR(100) NOT NULL,
    training_date DATE NOT NULL,
    hours NUMERIC(8,2) NOT NULL,
    attendees INTEGER NOT NULL,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_rrhh_employees_area ON rrhh_employees(area);
CREATE INDEX IF NOT EXISTS idx_rrhh_employees_dependency ON rrhh_employees(dependency);
CREATE INDEX IF NOT EXISTS idx_rrhh_vacancies_area ON rrhh_vacancies(area);
CREATE INDEX IF NOT EXISTS idx_rrhh_candidates_vacancy ON rrhh_candidates(vacancy_id);
CREATE INDEX IF NOT EXISTS idx_rrhh_payrolls_employee ON rrhh_payrolls(employee_id);
