create table if not exists system_version (
    id integer primary key,
    created_at timestamptz not null default now()
);

create table if not exists service_deployment (
    system_version integer not null references system_version(id),
    service_name varchar(255) not null,
    version integer not null,
    primary key (system_version, service_name),
    constraint service_name_not_blank check (length(trim(service_name)) > 0),
    constraint version_positive check (version > 0)
);

create index idx_service_deployment_service_version
    on service_deployment (service_name, system_version desc);