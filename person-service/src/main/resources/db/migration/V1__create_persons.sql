create table if not exists persons (
    id varchar(36) primary key,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    email varchar(254) null,
    created_at timestamp not null,
    updated_at timestamp not null
);

create index if not exists idx_persons_created_at on persons(created_at);
