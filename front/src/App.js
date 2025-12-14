import React, { useEffect } from 'react';
import { Routes, Route, useLocation, useNavigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';

import DefaultLayout from './layouts/DefaultLayout';
import AdminLayout from './layouts/AdminLayout';

import HomePage from './pages/HomePage';
import LoginPage from './pages/Auth/LoginPage';

import BookListPage from './pages/management/book/BookListPage';
import CategoryManagementPage from './pages/management/category/CategoryManagementPage';

import { useDispatch, useSelector } from 'react-redux';
import { setUser } from './redux/slice/auth.slice';
import userService from './services/user.service';
import Loading from './components/ui/Loading';
import ConfirmDialog from './components/ui/ConfirmDialog';
import AuthorManagementPage from './pages/management/author/AuthorManagementPage';

function App() {
  const location = useLocation();
  const navigate = useNavigate();

  const currentUser = useSelector((state) => state.auth);
  const dispatch = useDispatch();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data } = await userService.getProfile();
        const {
          id,
          email,
          fullName,
          phoneNumber,
          role,
          avatarUrl,
          permissions,
        } = data;
        dispatch(
          setUser({
            id,
            email,
            fullName,
            phoneNumber,
            role,
            avatarUrl,
            permissions,
          })
        );
      } catch (error) {
        console.log(error);
      }
    };
    const token = localStorage.getItem('accessToken');
    if (token) {
      if (location.pathname.includes('/dang-nhap')) {
        navigate({ pathname: '/' });
      }
      if (!currentUser.id) {
        fetchData();
      }
    }
  }, [dispatch, currentUser, navigate, location.pathname]);

  return (
    <div className="App">
      <ToastContainer />
      <Loading />
      <ConfirmDialog />
      <Routes>
        <Route path="/" element={<DefaultLayout />}>
          <Route exact path="/" element={<HomePage />} />
          <Route path="/dang-nhap" element={<LoginPage />} />
        </Route>

        <Route path="/management" element={<AdminLayout />}>
          <Route path="book" element={<BookListPage />} />
          <Route path="category" element={<CategoryManagementPage />} />
          <Route path="author" element={<AuthorManagementPage />} />
        </Route>
      </Routes>
    </div>
  );
}

export default App;
