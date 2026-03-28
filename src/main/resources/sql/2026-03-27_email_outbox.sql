-- =======================================
-- EPMAPAT ERP - modulo de correos
-- Fecha: 2026-03-27
-- Motor objetivo: PostgreSQL
-- =======================================

CREATE TABLE IF NOT EXISTS public.email_account (
    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    code varchar(50) NOT NULL,
    name varchar(150) NOT NULL,
    provider varchar(100) NULL,
    from_address varchar(320) NOT NULL,
    from_name varchar(150) NULL,
    reply_to varchar(320) NULL,
    transport_type varchar(20) NOT NULL DEFAULT 'SMTP',
    host varchar(150) NULL,
    port integer NULL,
    protocol varchar(20) NULL DEFAULT 'smtp',
    security_type varchar(20) NULL,
    auth_required boolean NOT NULL DEFAULT true,
    username varchar(320) NULL,
    password varchar(500) NULL,
    api_url varchar(500) NULL,
    api_auth_header varchar(100) NULL,
    api_auth_scheme varchar(50) NULL,
    api_key varchar(500) NULL,
    active boolean NOT NULL DEFAULT true,
    is_default boolean NOT NULL DEFAULT false,
    default_for_type varchar(30) NULL,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    CONSTRAINT email_account_pk PRIMARY KEY (id),
    CONSTRAINT email_account_code_uk UNIQUE (code),
    CONSTRAINT email_account_transport_chk CHECK (transport_type IN ('SMTP', 'API_HTTP')),
    CONSTRAINT email_account_security_chk CHECK (security_type IN ('NONE', 'STARTTLS', 'SSL_TLS')),
    CONSTRAINT email_account_default_for_type_chk CHECK (default_for_type IN ('DOC_ELECTRONICO', 'NOTIFICACION', 'CUSTOM') OR default_for_type IS NULL)
);

CREATE TABLE IF NOT EXISTS public.email_message (
    id uuid NOT NULL,
    type varchar(30) NOT NULL,
    status varchar(20) NOT NULL,
    to_recipients varchar(4000) NOT NULL,
    cc_recipients varchar(4000) NULL,
    bcc_recipients varchar(4000) NULL,
    subject varchar(500) NOT NULL,
    body_html text NULL,
    body_text text NULL,
    correlation_id varchar(200) NULL,
    email_account_id bigint NULL,
    from_address varchar(320) NULL,
    attempts integer NOT NULL DEFAULT 0,
    last_error varchar(2000) NULL,
    created_at timestamptz NOT NULL,
    sent_at timestamptz NULL,
    CONSTRAINT email_message_pk PRIMARY KEY (id),
    CONSTRAINT email_message_email_account_fk FOREIGN KEY (email_account_id)
        REFERENCES public.email_account (id),
    CONSTRAINT email_message_type_chk CHECK (type IN ('DOC_ELECTRONICO', 'NOTIFICACION', 'CUSTOM')),
    CONSTRAINT email_message_status_chk CHECK (status IN ('PENDING', 'SENT', 'FAILED', 'CANCELLED')),
    CONSTRAINT email_message_attempts_chk CHECK (attempts >= 0)
);

CREATE TABLE IF NOT EXISTS public.email_attachment (
    id uuid NOT NULL,
    email_id uuid NOT NULL,
    filename varchar(255) NOT NULL,
    content_type varchar(255) NOT NULL,
    size bigint NOT NULL,
    storage_ref varchar(800) NOT NULL,
    sha256 varchar(64) NULL,
    CONSTRAINT email_attachment_pk PRIMARY KEY (id),
    CONSTRAINT email_attachment_email_fk FOREIGN KEY (email_id)
        REFERENCES public.email_message (id)
        ON DELETE CASCADE,
    CONSTRAINT email_attachment_size_chk CHECK (size >= 0)
);

CREATE TABLE IF NOT EXISTS public.email_blacklist (
    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    type varchar(20) NOT NULL,
    value varchar(320) NOT NULL,
    reason varchar(500) NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    CONSTRAINT email_blacklist_pk PRIMARY KEY (id),
    CONSTRAINT email_blacklist_type_value_uk UNIQUE (type, value),
    CONSTRAINT email_blacklist_type_chk CHECK (type IN ('DOMAIN', 'HOST', 'EMAIL'))
);

CREATE INDEX IF NOT EXISTS email_message_status_attempts_created_idx
    ON public.email_message (status, attempts, created_at);

CREATE INDEX IF NOT EXISTS email_message_correlation_id_idx
    ON public.email_message (correlation_id);

CREATE INDEX IF NOT EXISTS email_message_account_id_idx
    ON public.email_message (email_account_id);

CREATE INDEX IF NOT EXISTS email_attachment_email_id_idx
    ON public.email_attachment (email_id);

CREATE INDEX IF NOT EXISTS email_blacklist_active_idx
    ON public.email_blacklist (active);

CREATE UNIQUE INDEX IF NOT EXISTS email_account_default_for_type_uidx
    ON public.email_account (default_for_type)
    WHERE default_for_type IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS email_account_general_default_uidx
    ON public.email_account ((1))
    WHERE is_default = true;
