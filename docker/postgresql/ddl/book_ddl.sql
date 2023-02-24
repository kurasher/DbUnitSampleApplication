create schema db_books;
create table db_books.book (
  id integer PRIMARY KEY,
  title varchar(100),
  author varchar(10)
);