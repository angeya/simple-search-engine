create table url_info
(
    id bigint auto_increment primary key comment '主键 自增',
    url varchar(256) not null unique comment 'url内容',
    create_time timestamp default current_timestamp() comment '创建时间'
) comment 'url队列表';