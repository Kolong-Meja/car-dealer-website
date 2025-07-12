create table roles (
  id varchar(15) primary key not null,
  name varchar(50) unique not null,
  description text null,
  status varchar(20) null default 'active',
  last_edited_by varchar(100) null,
  created_at timestampz default current_timestamp,
  updated_at timestampz default current_timestamp,
  deleted_at timestampz
);

create index name_idx on roles(name);
create index status_idx on roles(status);