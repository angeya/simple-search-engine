create table crawler_data_record
(
    code varchar(32) primary key comment '编码 主键',
    value bigint comment '记录值',
    text_value varchar(64) comment '文本值',
    modify_time timestamp default current_timestamp() on update current_timestamp() comment '修改时间',
    create_time timestamp default current_timestamp() comment '创建时间'
) comment '爬虫数据记录';