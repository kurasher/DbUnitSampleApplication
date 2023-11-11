-- DBの作成
create database db_books;

-- DBの選択
\connect db_books;

-- スキーマの作成
create schema books;

-- スキーマの選択
SET search_path = books;

-- テーブルの作成
create table book (
  id integer PRIMARY KEY NOT NULL,
  title varchar(100) NOT NULL,
  author varchar(10) NOT NULL,
  created timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
  updated timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL
);