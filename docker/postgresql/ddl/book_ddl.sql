create schema db_books;
create table db_books.book (
  id integer PRIMARY KEY NOT NULL,
  title varchar(100) NOT NULL,
  author varchar(10) NOT NULL,
  created timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL
);