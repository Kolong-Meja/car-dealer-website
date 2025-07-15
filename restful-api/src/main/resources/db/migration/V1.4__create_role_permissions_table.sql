create table role_permissions (
  role_id varchar(20) not null,
  permission_id varchar(20) not null,
  
  constraint pk_role_permissions primary key(role_id, permission_id),
  constraint fk_role_id foreign key (role_id) references roles(id) on delete cascade on update cascade,
  constraint fk_permission_id foreign key (permission_id) references permissions(id) on delete cascade on update cascade
);