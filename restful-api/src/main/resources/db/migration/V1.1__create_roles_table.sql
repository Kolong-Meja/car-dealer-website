create table roles (
  id varchar(20) primary key not null,
  name varchar(50) unique not null,
  description text null,
  status varchar(20) null default 'active',
  last_edited_by varchar(100) null,
  created_at timestamptz default current_timestamp,
  updated_at timestamptz default current_timestamp,
  deleted_at timestamptz
);

create index role_name_idx on roles(name);
create index role_status_idx on roles(status);