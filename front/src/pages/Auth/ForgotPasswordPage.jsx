import React, { useState, useRef, useEffect } from 'react';
import { Container } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import { useDispatch } from 'react-redux';
import { setLoading } from '../../redux/slice/app.slice';
import authService from '../../services/auth.service';

import './Auth.scss';

const COOLDOWN_SECONDS = 60;

const validationSchema = Yup.object({
  email: Yup.string()
    .required('Vui lòng nhập email')
    .email('Email không hợp lệ'),
});

function ForgotPasswordPage() {
  const dispatch = useDispatch();
  const [sent, setSent] = useState(false);
  const [submittedEmail, setSubmittedEmail] = useState('');
  const [cooldown, setCooldown] = useState(0);
  const timerRef = useRef(null);

  useEffect(() => {
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, []);

  const startCooldown = () => {
    setCooldown(COOLDOWN_SECONDS);
    timerRef.current = setInterval(() => {
      setCooldown((prev) => {
        if (prev <= 1) {
          clearInterval(timerRef.current);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  };

  const handleSubmit = async (values, { setSubmitting }) => {
    dispatch(setLoading(true));
    try {
      await authService.forgotPassword({ email: values.email });
    } catch {
      // Security: always show sent view regardless
    } finally {
      dispatch(setLoading(false));
      setSubmitting(false);
      setSubmittedEmail(values.email);
      setSent(true);
      startCooldown();
    }
  };

  const handleResend = async () => {
    if (!submittedEmail || cooldown > 0) return;
    dispatch(setLoading(true));
    try {
      await authService.forgotPassword({ email: submittedEmail });
      toast.success('Đã gửi lại email đặt lại mật khẩu!');
      startCooldown();
    } catch {
      // Security: no reveal
    } finally {
      dispatch(setLoading(false));
    }
  };

  if (sent) {
    return (
      <div className="main auth-page">
        <Container>
          <div className="auth-wrapper text-center">
            <h2 className="title">QUÊN MẬT KHẨU</h2>
            <p className="mt-4">Đã gửi email đặt lại mật khẩu.</p>
            <p>Vui lòng kiểm tra hộp thư của <strong>{submittedEmail}</strong>.</p>
            <div className="mt-4">
              {cooldown > 0 ? (
                <span className="resend-cooldown">Gửi lại sau {cooldown}s</span>
              ) : (
                <button className="btn btn-outline-secondary" onClick={handleResend}>
                  Gửi lại email
                </button>
              )}
            </div>
            <div className="mt-3">
              <Link to="/dang-nhap">Quay lại đăng nhập</Link>
            </div>
          </div>
        </Container>
      </div>
    );
  }

  return (
    <div className="main auth-page">
      <Container>
        <div className="auth-wrapper">
          <h2 className="title text-center">QUÊN MẬT KHẨU</h2>
          <Formik
            initialValues={{ email: '' }}
            validationSchema={validationSchema}
            onSubmit={handleSubmit}
          >
            {({ isSubmitting }) => (
              <Form>
                <div className="form-group mt-3">
                  <Field
                    type="email"
                    name="email"
                    className="form-control"
                    placeholder="Email..."
                  />
                  <ErrorMessage name="email" component="div" className="text-danger mt-1" />
                </div>
                <button className="btn submit-btn" type="submit" disabled={isSubmitting}>
                  {isSubmitting ? 'Đang gửi...' : 'Gửi email đặt lại mật khẩu'}
                </button>
              </Form>
            )}
          </Formik>
          <p className="text-center mt-2">
            <Link to="/dang-nhap">Quay lại đăng nhập</Link>
          </p>
        </div>
      </Container>
    </div>
  );
}

export default ForgotPasswordPage;
