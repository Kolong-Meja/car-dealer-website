create table permissions (
  id varchar(20) primary key not null,
  name varchar(100) unique not null,
  description text null,
  status varchar(20) null default 'active',
  last_edited_by varchar(100) null,
  created_at timestamptz default current_timestamp,
  updated_at timestamptz default current_timestamp,
  deleted_at timestamptz
);

create index permission_name_idx on permissions(name);
create index permission_status_idx on permissions(status);