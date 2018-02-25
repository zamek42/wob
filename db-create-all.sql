create table order_ (
  orderid                       bigint auto_increment not null,
  buyername                     varchar(50) not null,
  buyeremail                    varchar(50) not null,
  orderdate                     date not null,
  ordertotalvalue               float not null,
  address                       varchar(255) not null,
  postcode                      integer,
  constraint pk_order_ primary key (orderid)
);

