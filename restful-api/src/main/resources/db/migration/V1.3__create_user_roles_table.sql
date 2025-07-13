create table user_roles (
  user_id varchar(36) not null,
  role_id varchar(36) not null,
  
  constraint pk_user_roles primary key(user_id, role_id),
  constraint fk_user_id foreign key (user_id) references users(id) on delete cascade on update cascade,
  constraint fk_role_id foreign key (role_id) references roles(id) on delete cascade on update cascade
);