create table users (
  id varchar(36) primary key not null,
  fullname varchar(100) not null,
  bio varchar(250) null,
  email varchar(100) unique not null,
  password varchar(50) not null,
  phone_number varchar(16) unique not null,
  address varchar(250) null,
  account_status varchar(20) null default 'active',
  active_status varchar(20) null default 'offline',
  avatar_url text null,
  password_change_at timestamptz,
  last_login_at timestamptz,
  last_edited_by varchar(100) null,
  created_at timestamptz default current_timestamp,
  updated_at timestamptz default current_timestamp,
  deleted_at timestamptz
);

comment on column users.bio is 'User biography.';
comment on column users.avatar_url is 'User avatar or image URL link.';

create index fullname_idx on users(fullname);
create index email_idx on users(email);
create index phone_number_idx on users(phone_number);
create index account_status_idx on users(account_status);