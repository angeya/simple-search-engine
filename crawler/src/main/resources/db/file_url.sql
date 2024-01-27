create table file_url
(
    id          varchar(32) not null comment '主键',
    urlInfo         varchar(256) unique comment '网页url 唯一索引',
    file_type   varchar(32) comment '文件类型',
    create_time datetime comment '创建时间',
    primary key (id)
);