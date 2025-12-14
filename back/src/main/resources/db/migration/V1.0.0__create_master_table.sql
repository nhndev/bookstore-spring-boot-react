CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ----------------------------
-- Table structure for bs_permissions
-- ----------------------------
DROP TABLE IF EXISTS public.bs_permissions;
CREATE TABLE public.bs_permissions (
    "id" uuid NOT NULL DEFAULT uuid_generate_v4(),
    "description" varchar(1000) NOT NULL,
    "permission_no" varchar(50) NOT NULL,
    "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE public.bs_permissions ADD CONSTRAINT "bs_permissions_pkey" PRIMARY KEY ("id");
ALTER TABLE public.bs_permissions ADD CONSTRAINT "bs_permissions_permission_no_unique" UNIQUE ("permission_no");

-- ----------------------------
-- Table structure for bs_roles
-- ----------------------------
DROP TABLE IF EXISTS public.bs_roles;
CREATE TABLE public.bs_roles (
    "id" uuid NOT NULL DEFAULT uuid_generate_v4(),
    "role_no" varchar(50) NOT NULL,
    "name" varchar(255),
    "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE public.bs_roles ADD CONSTRAINT "bs_roles_pkey" PRIMARY KEY ("id");
ALTER TABLE public.bs_roles ADD CONSTRAINT "bs_roles_role_no_unique" UNIQUE ("role_no");

-- ----------------------------
-- Table structure for bs_role_permission
-- ----------------------------
DROP TABLE IF EXISTS public.bs_role_permission;
CREATE TABLE public.bs_role_permission (
      "role_id" uuid,
      "permission_id" uuid,
      "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
      "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE public.bs_role_permission ADD CONSTRAINT "bs_bs_role_permission_pkey" PRIMARY KEY ("role_id", "permission_id");
ALTER TABLE public.bs_role_permission ADD CONSTRAINT "fk_role_permission_permission" FOREIGN KEY ("permission_id") REFERENCES public.bs_permissions ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE public.bs_role_permission ADD CONSTRAINT "fk_role_permission_role" FOREIGN KEY ("role_id") REFERENCES public.bs_roles ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Table structure for bs_users
-- ----------------------------
DROP TABLE IF EXISTS public.bs_users;
CREATE TABLE public.bs_users (
    "id" uuid NOT NULL DEFAULT uuid_generate_v4(),
    "email" varchar(50) NOT NULL,
    "full_name" varchar(50) NOT NULL,
    "phone_number" varchar(20),
    "password" varchar(255) NOT NULL,
    "avatar_url" varchar(255),
    "reset_password_code" varchar(64),
    "verification_code" varchar(64),
    "status" int NOT NULL DEFAULT 0,
    "role_id" uuid NOT NULL,
    "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE public.bs_users ADD CONSTRAINT "bs_users_pkey" PRIMARY KEY ("id");
ALTER TABLE public.bs_users ADD CONSTRAINT "bs_users_email_unique" UNIQUE ("email");
ALTER TABLE public.bs_users ADD CONSTRAINT "bs_users_phone_number_unique" UNIQUE ("phone_number");
ALTER TABLE public.bs_users ADD CONSTRAINT "fk_users_role" FOREIGN KEY ("role_id") REFERENCES public.bs_roles ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Table structure for bs_categories
-- ----------------------------
DROP TABLE IF EXISTS public.bs_categories;
CREATE TABLE public.bs_categories (
    "id" uuid NOT NULL DEFAULT uuid_generate_v4(),
    "name" varchar(255) NOT NULL,
    "slug" varchar(255) NOT NULL,
    "image_url" varchar(255),
    "parent_id" uuid,
    "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE public.bs_categories ADD CONSTRAINT "bs_categories_pkey" PRIMARY KEY ("id");
ALTER TABLE public.bs_categories ADD CONSTRAINT "bs_categories_slug_unique" UNIQUE ("slug");
ALTER TABLE public.bs_categories ADD CONSTRAINT "fk_categories_parent" FOREIGN KEY ("parent_id") REFERENCES public.bs_categories ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Table structure for bs_publishers
-- ----------------------------
DROP TABLE IF EXISTS public.bs_publishers;
CREATE TABLE public.bs_publishers (
    "id" uuid NOT NULL DEFAULT uuid_generate_v4(),
    "name" varchar(255) NOT NULL,
    "slug" varchar(255) NOT NULL,
    "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE public.bs_publishers ADD CONSTRAINT "bs_publishers_pkey" PRIMARY KEY ("id");
ALTER TABLE public.bs_publishers ADD CONSTRAINT "bs_publishers_slug_unique" UNIQUE ("slug");

-- ----------------------------
-- Table structure for bs_authors
-- ----------------------------
DROP TABLE IF EXISTS public.bs_authors;
CREATE TABLE public.bs_authors (
    "id" uuid NOT NULL DEFAULT uuid_generate_v4(),
    "name" varchar(255) NOT NULL,
    "slug" varchar(255) NOT NULL,
    "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE public.bs_authors ADD CONSTRAINT "bs_authors_pkey" PRIMARY KEY ("id");
ALTER TABLE public.bs_authors ADD CONSTRAINT "bs_authors_slug_unique" UNIQUE ("slug");

-- ----------------------------
-- Table structure for bs_books
-- ----------------------------
DROP TABLE IF EXISTS public.bs_books;
CREATE TABLE public.bs_books (
    "id" uuid NOT NULL,
    "description" varchar(1000),
    "discount" int4,
    "image_url" varchar(255),
    "language" varchar(255),
    "layout" varchar(255),
    "name" varchar(255) NOT NULL,
    "page_count" int4,
    "price" int8 NOT NULL,
    "publish_year" int4,
    "quantity" int4 NOT NULL DEFAULT 0,
    "size" varchar(255),
    "slug" varchar(255) NOT NULL,
    "weight" int4,
    "category_id" uuid,
    "publisher_id" uuid,
    "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE public.bs_books ADD CONSTRAINT "bs_books_pkey" PRIMARY KEY ("id");
ALTER TABLE public.bs_books ADD CONSTRAINT "bs_books_slug_unique" UNIQUE ("slug");
ALTER TABLE public.bs_books ADD CONSTRAINT "fk_authors_categories" FOREIGN KEY ("category_id") REFERENCES public.bs_categories ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE public.bs_books ADD CONSTRAINT "fk_authors_publishers" FOREIGN KEY ("publisher_id") REFERENCES public.bs_publishers ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Table structure for bs_book_image
-- ----------------------------
DROP TABLE IF EXISTS public.bs_book_image;
CREATE TABLE public.bs_book_image (
    "id" uuid NOT NULL,
    "public_id" varchar(255),
    "url" varchar(255) NOT NULL,
    "book_id" uuid,
    "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE public.bs_book_image ADD CONSTRAINT "bs_book_image_pkey" PRIMARY KEY ("id");
ALTER TABLE public.bs_book_image ADD CONSTRAINT "fk_book_image_books" FOREIGN KEY ("book_id") REFERENCES public.bs_books ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Table structure for bs_author_book
-- ----------------------------
DROP TABLE IF EXISTS public.bs_author_book;
CREATE TABLE public.bs_author_book (
    "author_id" uuid NOT NULL,
    "book_id" uuid NOT NULL,
    "is_main" bool NOT NULL DEFAULT false,
    "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE public.bs_author_book ADD CONSTRAINT "bs_author_book_pkey" PRIMARY KEY ("author_id", "book_id");
ALTER TABLE public.bs_author_book ADD CONSTRAINT "fk_author_book_author" FOREIGN KEY ("author_id") REFERENCES public.bs_authors ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE public.bs_author_book ADD CONSTRAINT "fk_author_book_book" FOREIGN KEY ("book_id") REFERENCES public.bs_books ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
