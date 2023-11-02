create table web_page_info
(
    id           varchar(32) not null comment '主键',
    title        varchar(128) comment '网页标题',
    url          varchar(256) unique comment '网页url 唯一索引',
    smart_content text comment '智能文本内容',
    create_time   datetime comment '创建时间',
    primary key (id)
);