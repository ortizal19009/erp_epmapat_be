-- =======================================
-- Migracion para cuentas SMTP administrables
-- Ejecutar si ya creaste email_message/email_attachment previamente
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
    security_type varchar(20) NULL DEFAULT 'STARTTLS',
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
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT email_account_pk PRIMARY KEY (id),
    CONSTRAINT email_account_code_uk UNIQUE (code),
    CONSTRAINT email_account_transport_chk CHECK (transport_type IN ('SMTP', 'API_HTTP')),
    CONSTRAINT email_account_security_chk CHECK (security_type IN ('NONE', 'STARTTLS', 'SSL_TLS')),
    CONSTRAINT email_account_default_for_type_chk CHECK (default_for_type IN ('DOC_ELECTRONICO', 'NOTIFICACION', 'CUSTOM') OR default_for_type IS NULL)
);

CREATE TABLE IF NOT EXISTS public.email_blacklist (
    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    type varchar(20) NOT NULL,
    value varchar(320) NOT NULL,
    reason varchar(500) NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT email_blacklist_pk PRIMARY KEY (id),
    CONSTRAINT email_blacklist_type_value_uk UNIQUE (type, value),
    CONSTRAINT email_blacklist_type_chk CHECK (type IN ('DOMAIN', 'HOST', 'EMAIL'))
);

ALTER TABLE public.email_account
    ADD COLUMN IF NOT EXISTS transport_type varchar(20) NOT NULL DEFAULT 'SMTP';

ALTER TABLE public.email_account
    ADD COLUMN IF NOT EXISTS api_url varchar(500) NULL;

ALTER TABLE public.email_account
    ADD COLUMN IF NOT EXISTS api_auth_header varchar(100) NULL;

ALTER TABLE public.email_account
    ADD COLUMN IF NOT EXISTS api_auth_scheme varchar(50) NULL;

ALTER TABLE public.email_account
    ADD COLUMN IF NOT EXISTS api_key varchar(500) NULL;

ALTER TABLE public.email_account
    ALTER COLUMN host DROP NOT NULL;

ALTER TABLE public.email_account
    ALTER COLUMN port DROP NOT NULL;

ALTER TABLE public.email_account
    ALTER COLUMN protocol DROP NOT NULL;

ALTER TABLE public.email_account
    ALTER COLUMN security_type DROP NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'email_account_transport_chk'
    ) THEN
        ALTER TABLE public.email_account
            ADD CONSTRAINT email_account_transport_chk
            CHECK (transport_type IN ('SMTP', 'API_HTTP'));
    END IF;
END $$;

ALTER TABLE public.email_message
    ADD COLUMN IF NOT EXISTS email_account_id bigint NULL;

ALTER TABLE public.email_message
    ADD COLUMN IF NOT EXISTS from_address varchar(320) NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'email_message_email_account_fk'
    ) THEN
        ALTER TABLE public.email_message
            ADD CONSTRAINT email_message_email_account_fk
            FOREIGN KEY (email_account_id)
            REFERENCES public.email_account (id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS email_message_account_id_idx
    ON public.email_message (email_account_id);

CREATE INDEX IF NOT EXISTS email_blacklist_active_idx
    ON public.email_blacklist (active);

CREATE UNIQUE INDEX IF NOT EXISTS email_account_default_for_type_uidx
    ON public.email_account (default_for_type)
    WHERE default_for_type IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS email_account_general_default_uidx
    ON public.email_account ((1))
    WHERE is_default = true;

-- Ejemplos base:
-- INSERT INTO public.email_account
-- (code, name, provider, from_address, from_name, transport_type, host, port, protocol, security_type, auth_required, username, password, active, is_default, default_for_type, created_at, updated_at)
-- VALUES
-- ('FACTURACION', 'Cuenta Facturacion', 'ElasticEmail', 'facturacion@tu-dominio.com', 'Facturacion', 'SMTP', 'smtp.elasticemail.com', 2525, 'smtp', 'STARTTLS', true, 'facturacion@tu-dominio.com', 'CAMBIAR_PASSWORD', true, false, 'DOC_ELECTRONICO', now(), now()),
-- ('NOTIFICACIONES', 'Cuenta Notificaciones', 'Gmail', 'notificaciones@tu-dominio.com', 'Notificaciones', 'SMTP', 'smtp.gmail.com', 587, 'smtp', 'STARTTLS', true, 'notificaciones@tu-dominio.com', 'CAMBIAR_APP_PASSWORD', true, false, 'NOTIFICACION', now(), now()),
-- ('CMANGINET_API', 'Cuenta API CmanGinet', 'CMANGINET', 'api@tu-dominio.com', 'API CmanGinet', 'API_HTTP', NULL, NULL, NULL, NULL, false, NULL, NULL, true, true, NULL, now(), now());
--
-- UPDATE public.email_account
-- SET api_url = 'https://tu-endpoint-api-mail',
--     api_auth_header = 'x-api-key',
--     api_auth_scheme = NULL,
--     api_key = 'CAMBIAR_API_KEY'
-- WHERE code = 'CMANGINET_API';
