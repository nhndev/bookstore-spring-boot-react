import React from 'react';
import { Container } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import { useDispatch } from 'react-redux';
import { setLoading } from '../../redux/slice/app.slice';
import authService from '../../services/auth.service';

import './Auth.scss';

const validationSchema = Yup.object({
  fullName: Yup.string()
    .required('Vui lòng nhập họ tên')
    .max(50, 'Họ tên không được quá 50 ký tự'),
  email: Yup.string()
    .required('Vui lòng nhập email')
    .email('Email không hợp lệ'),
  password: Yup.string()
    .required('Vui lòng nhập mật khẩu')
    .min(6, 'Mật khẩu phải có ít nhất 6 ký tự'),
  confirmPassword: Yup.string()
    .required('Vui lòng xác nhận mật khẩu')
    .oneOf([Yup.ref('password')], 'Mật khẩu xác nhận không khớp'),
});

function RegisterPage() {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleSubmit = async (values, { setSubmitting }) => {
    dispatch(setLoading(true));
    try {
      const { fullName, email, password } = values;
      await authService.register({ fullName, email, password });
      navigate('/xac-minh-email', { state: { email } });
    } catch (error) {
      const errorMessage = error?.response?.data?.errorMessage;
      if (errorMessage) {
        toast.error(errorMessage);
      }
    } finally {
      dispatch(setLoading(false));
      setSubmitting(false);
    }
  };

  return (
    <div className="main auth-page">
      <Container>
        <div className="auth-wrapper">
          <h2 className="title text-center">ĐĂNG KÝ</h2>
          <Formik
            initialValues={{ fullName: '', email: '', password: '', confirmPassword: '' }}
            validationSchema={validationSchema}
            onSubmit={handleSubmit}
          >
            {({ isSubmitting }) => (
              <Form>
                <div className="form-group mt-3">
                  <Field
                    type="text"
                    name="fullName"
                    className="form-control"
                    placeholder="Họ và tên..."
                  />
                  <ErrorMessage name="fullName" component="div" className="text-danger mt-1" />
                </div>
                <div className="form-group mt-3">
                  <Field
                    type="email"
                    name="email"
                    className="form-control"
                    placeholder="Email..."
                  />
                  <ErrorMessage name="email" component="div" className="text-danger mt-1" />
                </div>
                <div className="form-group mt-3">
                  <Field
                    type="password"
                    name="password"
                    className="form-control"
                    placeholder="Mật khẩu..."
                    autoComplete="new-password"
                  />
                  <ErrorMessage name="password" component="div" className="text-danger mt-1" />
                </div>
                <div className="form-group mt-3">
                  <Field
                    type="password"
                    name="confirmPassword"
                    className="form-control"
                    placeholder="Xác nhận mật khẩu..."
                    autoComplete="new-password"
                  />
                  <ErrorMessage name="confirmPassword" component="div" className="text-danger mt-1" />
                </div>
                <button className="btn submit-btn" type="submit" disabled={isSubmitting}>
                  {isSubmitting ? 'Đang đăng ký...' : 'Đăng ký'}
                </button>
              </Form>
            )}
          </Formik>
          <p className="text-center">
            Đã có tài khoản?{' '}
            <Link to="/dang-nhap" style={{ color: '#0074da' }}>
              Đăng nhập tại đây
            </Link>
          </p>
        </div>
      </Container>
    </div>
  );
}

export default RegisterPage;
