create or replace table avatars
(
    avatar_id  int auto_increment
        primary key,
    avatar_url varchar(200) not null
);

create or replace table cars_pictures
(
    car_picture_id  int auto_increment
        primary key,
    car_picture_url varchar(200) not null
);

create or replace table roles
(
    role_id   int auto_increment
        primary key,
    role_name varchar(20) not null
);

create or replace table travel_statuses
(
    travel_status_id int auto_increment
        primary key,
    status_name      varchar(20) not null
);

create or replace table users
(
    user_id                  int auto_increment
        primary key,
    username                 varchar(20)                               not null,
    password                 varchar(25)                               not null,
    first_name               varchar(20)                               not null,
    last_name                varchar(20)                               not null,
    email                    varchar(50)                               not null,
    phone_number             varchar(10)                               null,
    created                  datetime      default current_timestamp() null,
    is_deleted               tinyint(1)    default 0                   not null,
    is_verified              tinyint(1)    default 0                   not null,
    is_blocked               tinyint(1)    default 0                   not null,
    average_passenger_rating double(11, 2) default 0.00                not null,
    average_driver_rating    double(11, 2) default 0.00                not null,
    car_picture_id           int                                       null,
    constraint users_pk_2
        unique (username),
    constraint users_cars_pictures_car_picture_id_fk
        foreign key (car_picture_id) references cars_pictures (car_picture_id)
);

create or replace table avatars_users
(
    avatar_id int null,
    user_id   int null,
    constraint avatars_users_avatars_avatar_id_fk
        foreign key (avatar_id) references avatars (avatar_id),
    constraint avatars_users_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create or replace table travels
(
    travel_id              int auto_increment
        primary key,
    starting_point         varchar(200)                         not null,
    ending_point           varchar(200)                         not null,
    departure_time         datetime                             not null,
    free_seats             int                                  not null,
    created                datetime default current_timestamp() null,
    user_id                int                                  not null,
    starting_point_address varchar(200)                         not null,
    ending_point_address   varchar(200)                         not null,
    starting_point_city    varchar(50)                          null,
    ending_point_city      varchar(50)                          null,
    ride_duration          int                                  not null,
    estimated_arrival_time datetime                             null,
    distance               int                                  null,
    travel_status_id       int                                  null,
    constraint travels_travel_statuses_travel_status_id_fk
        foreign key (travel_status_id) references travel_statuses (travel_status_id),
    constraint travels_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create or replace table feedback
(
    feedback_id      int auto_increment
        primary key,
    user_author_id   int                                   not null,
    user_receiver_id int                                   not null,
    rating           int                                   not null,
    type             varchar(30)                           null,
    created          timestamp default current_timestamp() not null,
    travel_id        int                                   null,
    constraint feedback_travels_travel_id_fk
        foreign key (travel_id) references travels (travel_id),
    constraint feedback_users_user_id_fk
        foreign key (user_author_id) references users (user_id),
    constraint feedback_users_user_id_fk2
        foreign key (user_receiver_id) references users (user_id)
);

create or replace table comments_for_feedback
(
    comment_id  int auto_increment
        primary key,
    content     varchar(200) null,
    feedback_id int          not null,
    constraint comment_for_feedback_feedback_feedback_id_fk
        foreign key (feedback_id) references feedback (feedback_id)
);

create or replace table travel_comments
(
    travel_comment_id      int auto_increment
        primary key,
    travel_comment_content varchar(100) not null,
    travel_id              int          null,
    constraint travel_comments_pk_2
        unique (travel_id),
    constraint travel_comments_travels_travel_id_fk
        foreign key (travel_id) references travels (travel_id)
);

create or replace index travels_cities_city_id_fk
    on travels (starting_point_city);

create or replace index travels_cities_city_id_fk2
    on travels (ending_point_city);

create or replace table travels_users_applied
(
    travel_id int null,
    user_id   int null,
    constraint travels_users_applied_travels_travel_id_fk
        foreign key (travel_id) references travels (travel_id),
    constraint travels_users_applied_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create or replace table travels_users_approved
(
    travel_id int null,
    user_id   int null,
    constraint travels_users_approved_travels_travel_id_fk
        foreign key (travel_id) references travels (travel_id),
    constraint travels_users_approved_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create or replace table users_roles
(
    user_id int null,
    role_id int not null,
    constraint users_roles_roles_role_id_fk
        foreign key (role_id) references roles (role_id),
    constraint users_roles_users_user_id_fk
        foreign key (user_id) references users (user_id)
);
