create table web_page_raw_data
(
    id          varchar(32) not null comment '主键',
    info_id     varchar(32) comment '网页信息id',
    content     longtext comment '网页原始内容',
    create_time datetime comment '创建时间',
    primary key (id)
);