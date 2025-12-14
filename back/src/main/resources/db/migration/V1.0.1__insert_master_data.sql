DO $$
DECLARE
    uuid_val UUID;
BEGIN
    uuid_val := uuid_generate_v4();
    INSERT INTO public.bs_roles (id, role_no, name) VALUES (uuid_val, 'SUPER_ADMIN', 'Quản trị viên');

    INSERT INTO public.bs_users (id, email, full_name, status, phone_number, password, role_id)
    VALUES (uuid_generate_v4(), 'superadmin@gmail.com', 'Super Admin', 1, '0868123456', '$2a$10$0Hl4mzigDkQM6m.9phQQb.0/ifAN4a6j7vde7PIMJJ9YJXyF/9ybi', uuid_val);
END $$;

INSERT INTO public.bs_roles (id, role_no, name) VALUES (uuid_generate_v4(), 'CUSTOMER', 'Khách hàng');

INSERT INTO public.bs_permissions (id, description, permission_no) VALUES (uuid_generate_v4(), 'Quyền thêm danh mục sách', 'BOOK_CATEGORY_CREATE');
INSERT INTO public.bs_permissions (id, description, permission_no) VALUES (uuid_generate_v4(), 'Quyền sửa danh mục sách', 'BOOK_CATEGORY_UPDATE');
INSERT INTO public.bs_permissions (id, description, permission_no) VALUES (uuid_generate_v4(), 'Quyền xoá danh mục sách', 'BOOK_CATEGORY_DELETE');

INSERT INTO public.bs_permissions (id, description, permission_no) VALUES (uuid_generate_v4(), 'Quyền thêm sách', 'BOOK_CREATE');
INSERT INTO public.bs_permissions (id, description, permission_no) VALUES (uuid_generate_v4(), 'Quyền sửa sách', 'BOOK_UPDATE');
INSERT INTO public.bs_permissions (id, description, permission_no) VALUES (uuid_generate_v4(), 'Quyền xoá sách', 'BOOK_DELETE');