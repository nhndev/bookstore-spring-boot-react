import React from 'react';
import { Container } from 'react-bootstrap';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import { useDispatch } from 'react-redux';
import { setLoading } from '../../redux/slice/app.slice';
import authService from '../../services/auth.service';

import './Auth.scss';

const validationSchema = Yup.object({
  newPassword: Yup.string()
    .required('Vui lòng nhập mật khẩu mới')
    .min(6, 'Mật khẩu phải có ít nhất 6 ký tự'),
  confirmPassword: Yup.string()
    .required('Vui lòng xác nhận mật khẩu')
    .oneOf([Yup.ref('newPassword')], 'Mật khẩu xác nhận không khớp'),
});

function ResetPasswordPage() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const resetPasswordCode = searchParams.get('resetPasswordCode');
  const email = searchParams.get('email');

  if (!resetPasswordCode || !email) {
    return (
      <div className="main auth-page">
        <Container>
          <div className="auth-wrapper text-center">
            <h2 className="title">ĐẶT LẠI MẬT KHẨU</h2>
            <p className="mt-4 text-danger">Link không hợp lệ hoặc đã hết hạn.</p>
            <Link to="/dang-nhap" className="btn btn-primary mt-3">
              Quay lại đăng nhập
            </Link>
          </div>
        </Container>
      </div>
    );
  }

  const handleSubmit = async (values, { setSubmitting }) => {
    dispatch(setLoading(true));
    try {
      await authService.resetPassword({
        email,
        resetPasswordCode,
        newPassword: values.newPassword,
      });
      toast.success('Đặt lại mật khẩu thành công!');
      navigate('/dang-nhap');
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
          <h2 className="title text-center">ĐẶT LẠI MẬT KHẨU</h2>
          <Formik
            initialValues={{ newPassword: '', confirmPassword: '' }}
            validationSchema={validationSchema}
            onSubmit={handleSubmit}
          >
            {({ isSubmitting }) => (
              <Form>
                <div className="form-group mt-3">
                  <Field
                    type="password"
                    name="newPassword"
                    className="form-control"
                    placeholder="Mật khẩu mới..."
                    autoComplete="new-password"
                  />
                  <ErrorMessage name="newPassword" component="div" className="text-danger mt-1" />
                </div>
                <div className="form-group mt-3">
                  <Field
                    type="password"
                    name="confirmPassword"
                    className="form-control"
                    placeholder="Xác nhận mật khẩu mới..."
                    autoComplete="new-password"
                  />
                  <ErrorMessage name="confirmPassword" component="div" className="text-danger mt-1" />
                </div>
                <button className="btn submit-btn" type="submit" disabled={isSubmitting}>
                  {isSubmitting ? 'Đang xử lý...' : 'Đặt lại mật khẩu'}
                </button>
              </Form>
            )}
          </Formik>
        </div>
      </Container>
    </div>
  );
}

export default ResetPasswordPage;
