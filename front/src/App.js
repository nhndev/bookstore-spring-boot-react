import React, { useEffect } from 'react';

import { useDispatch, useSelector } from 'react-redux';
import { Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';

import ConfirmDialog from './components/ui/ConfirmDialog';
import ProtectedRoute from './components/auth/ProtectedRoute';
import Loading from './components/ui/Loading';
import AdminLayout from './layouts/AdminLayout';
import DefaultLayout from './layouts/DefaultLayout';
import LoginPage from './pages/Auth/LoginPage';
import RegisterPage from './pages/Auth/RegisterPage';
import EmailVerificationPage from './pages/Auth/EmailVerificationPage';
import ForgotPasswordPage from './pages/Auth/ForgotPasswordPage';
import ResetPasswordPage from './pages/Auth/ResetPasswordPage';
import ErrorPage from './pages/ErrorPage';
import HomePage from './pages/HomePage';
import AuthorManagementPage from './pages/management/author/AuthorManagementPage';
import BookManagementPage from './pages/management/book/BookManagementPage';
import CategoryManagementPage from './pages/management/category/CategoryManagementPage';
import PublisherManagementPage from './pages/management/publisher/PublisherManagementPage';
import { setUser } from './redux/slice/auth.slice';
import userService from './services/user.service';

function App() {
  const location = useLocation();
  const navigate = useNavigate();

  const currentUser = useSelector((state) => state.auth);
  const errorPage = useSelector((state) => state.app.errorPage);
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
          }),
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

  if (errorPage.show) {
    return (
      <>
        <ToastContainer />
        <ErrorPage />
      </>
    );
  }

  return (
    <div className="App">
      <ToastContainer />
      <Loading />
      <ConfirmDialog />
      <Routes>
        <Route path="/" element={<DefaultLayout />}>
          <Route exact path="/" element={<HomePage />} />
          <Route path="/dang-nhap" element={<LoginPage />} />
          <Route path="/dang-ki" element={<RegisterPage />} />
          <Route path="/xac-minh-email" element={<EmailVerificationPage />} />
          <Route path="/quen-mat-khau" element={<ForgotPasswordPage />} />
          <Route path="/dat-lai-mat-khau" element={<ResetPasswordPage />} />
        </Route>

        <Route path="/management" element={
          <ProtectedRoute>
            <AdminLayout />
          </ProtectedRoute>
        }>
          <Route path="book" element={<BookManagementPage />} />
          <Route path="category" element={<CategoryManagementPage />} />
          <Route path="author" element={<AuthorManagementPage />} />
          <Route path="publisher" element={<PublisherManagementPage />} />
        </Route>
      </Routes>
    </div>
  );
}

export default App;
