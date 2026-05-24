import React, { useEffect, useState, useRef } from 'react';
import { Container } from 'react-bootstrap';
import { Link, useLocation, useSearchParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import authService from '../../services/auth.service';

import './Auth.scss';

const COOLDOWN_SECONDS = 300;

function EmailVerificationPage() {
  const location = useLocation();
  const [searchParams] = useSearchParams();

  const verificationCode = searchParams.get('verificationCode');
  const emailFromUrl = searchParams.get('email');
  const emailFromState = location.state?.email;

  const isAutoVerify = !!(verificationCode && emailFromUrl);
  const email = emailFromUrl || emailFromState;

  const [verifyStatus, setVerifyStatus] = useState('loading'); // 'loading' | 'success' | 'error'
  const [cooldown, setCooldown] = useState(0);
  const timerRef = useRef(null);

  useEffect(() => {
    if (!isAutoVerify) return;

    const verify = async () => {
      try {
        await authService.verifyEmail({ verificationCode, email: emailFromUrl });
        setVerifyStatus('success');
      } catch {
        setVerifyStatus('error');
      }
    };

    verify();
  }, [isAutoVerify, verificationCode, emailFromUrl]);

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

  const formatCooldown = (seconds) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return m > 0 ? `${m}:${String(s).padStart(2, '0')}` : `${s}s`;
  };

  const handleResend = async () => {
    if (!email || cooldown > 0) return;
    try {
      await authService.resendEmailVerification({ email });
      toast.success('Đã gửi lại email xác minh!');
      startCooldown();
    } catch (error) {
      const errorMessage = error?.response?.data?.errorMessage;
      if (errorMessage) {
        toast.error(errorMessage);
      }
    }
  };

  if (!isAutoVerify) {
    return (
      <div className="main verify-email-page">
        <Container>
          <div className="auth-wrapper text-center">
            <div className="status-icon">📧</div>
            <h2>Xác minh email</h2>
            <p className="mt-3">
              Chúng tôi đã gửi email xác minh đến <strong>{email}</strong>.
            </p>
            <p>Vui lòng kiểm tra hộp thư và nhấp vào liên kết xác minh.</p>
            <div className="mt-4">
              {cooldown > 0 ? (
                <span className="resend-cooldown">Gửi lại sau {formatCooldown(cooldown)}</span>
              ) : (
                <button className="btn btn-outline-secondary" onClick={handleResend}>
                  Gửi lại email
                </button>
              )}
            </div>
          </div>
        </Container>
      </div>
    );
  }

  if (verifyStatus === 'loading') {
    return (
      <div className="main verify-email-page">
        <Container>
          <div className="auth-wrapper text-center">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Đang xác minh...</span>
            </div>
            <p className="mt-3">Đang xác minh tài khoản...</p>
          </div>
        </Container>
      </div>
    );
  }

  if (verifyStatus === 'success') {
    return (
      <div className="main verify-email-page">
        <Container>
          <div className="auth-wrapper text-center">
            <div className="status-icon success">✓</div>
            <h2>Xác minh thành công!</h2>
            <p className="mt-3">Tài khoản của bạn đã được kích hoạt.</p>
            <Link to="/dang-nhap" className="btn btn-primary mt-3">
              Đăng nhập ngay
            </Link>
          </div>
        </Container>
      </div>
    );
  }

  return (
    <div className="main verify-email-page">
      <Container>
        <div className="auth-wrapper text-center">
          <div className="status-icon error">✗</div>
          <h2>Xác minh thất bại</h2>
          <p className="mt-3">Liên kết xác minh không hợp lệ hoặc đã hết hạn.</p>
          <div className="mt-4">
            {cooldown > 0 ? (
              <span className="resend-cooldown">Gửi lại sau {formatCooldown(cooldown)}</span>
            ) : (
              <button className="btn btn-outline-danger" onClick={handleResend}>
                Gửi lại email xác minh
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

export default EmailVerificationPage;
