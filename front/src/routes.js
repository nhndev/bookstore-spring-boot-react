export const routes = [
  {
    title: 'Tổng quan',
    name: 'Dashboard',
    path: '/management',
    exactly: true,
  },
  {
    title: 'Quản lý sách',
    path: '/management/book',
    name: 'BookManagement',
    children: [
       {
        title: 'Sách',
        name: 'BookManagement',
        path: '/management/book',
       },
       {
        title: 'Danh mục sách',
        name: 'CategoryManagement',
        path: '/management/category',
       },
       {
        title: 'Danh mục tác giả',
        name: 'AuthorManagement',
        path: '/management/author',
       },
       {
        title: 'Nhà xuất bản',
        name: 'PublisherManagement',
        path: '/management/publisher',
       },
    ],
  },
  {
    title: 'Quản lý đơn hàng',
    name: 'OrderManagement',
    path: '/management/order',
  },
  {
    title: 'Mã giảm giá',
    name: 'VoucherManagement',
    path: '/management/voucher',
  },
  {
    title: 'Khách hàng',
    name: 'CustomerManagement',
    path: '/management/customer',
  },
  {
    title: 'Nhân viên',
    name: 'StaffManagement',
    path: '/management/staff',
  },
];
