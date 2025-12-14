import React from 'react';
import { Outlet } from 'react-router-dom';
import AdminSideBar from '../components/ui/SideBar/AdminSideBar';

function AdminLayout() {
  return (
    <>
      <AdminSideBar />
      <div className="management-wrapper">
        <Outlet />
      </div>
    </>
  );
}

export default AdminLayout;
