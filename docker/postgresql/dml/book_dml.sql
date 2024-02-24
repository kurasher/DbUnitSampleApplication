-- DBの選択
\connect db_books;

-- スキーマの選択
SET search_path = books;

-- データの挿入
-- insert into book (id, title, author) values (1, '営繕かるかや怪異譚', '小野不由美');
-- insert into book (id, title, author) values (2, '営繕かるかや怪異譚 その弐', '小野不由美');
-- insert into book (id, title, author) values (3, '営繕かるかや怪異譚 その参', '小野不由美');

insert into book (title, author) values ('営繕かるかや怪異譚', '小野不由美');
insert into book (title, author) values ('営繕かるかや怪異譚 その弐', '小野不由美');
insert into book (title, author) values ('営繕かるかや怪異譚 その参', '小野不由美');