import React, { useState } from 'react';
import { Button, Container, Modal } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../../services/auth.service';
import { useDispatch } from 'react-redux';
import { setUser } from '../../redux/slice/auth.slice';

import './Auth.scss';

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [loading, setLoading] = useState(false);

  const [showModal, setShowModal] = useState(false);

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const onSubmitLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const { data } = await authService.login({ email, password });
      const { accessToken, user } = data;
      localStorage.setItem('accessToken', accessToken);
      const { id, fullName, phoneNumber, role, avatarUrl, permissions } = user;
      dispatch(
        setUser({ id, email, fullName, phoneNumber, role, avatarUrl, permissions })
      );
      navigate({ pathname: '/' })
    } catch (error) {
      // TODO
    } finally {
      setLoading(false);
    }
  };

  const handleSendEmail = async () => {
    // try {
    //   const { error } = await authApi.requestActiveAccount({email})
    //   if (!error) {
    //     alert("Vui lòng kiểm tra email để kích hoạt tài khoản!")
    //     setShowModal(false)
    //   }
    // } catch (error) {
    //   console.log(error)
    // }
  };

  return (
    <div className="main login-page">
      <Modal size="lg" show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Thông báo</Modal.Title>
        </Modal.Header>
        <Modal.Body>Tài khoản của bạn chưa được xác minh.</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Hủy
          </Button>
          <Button variant="danger" onClick={handleSendEmail}>
            Gửi lại Email
          </Button>
        </Modal.Footer>
      </Modal>
      <Container>
        <div className="auth-wrapper">
          <h2 className="title text-center">ĐĂNG NHẬP</h2>
          <form className="form-login" onSubmit={onSubmitLogin}>
            <div className="form-group mt-3">
              <input
                required
                type="text"
                name="email"
                className="form-control"
                placeholder="Email..."
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div className="form-group mt-3">
              <input
                required
                type="password"
                name="password"
                className="form-control"
                autoComplete="on"
                placeholder="Mật khẩu..."
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
            <div className="mt-3">
              <Link className="forgot-password" to="/quen-mat-khau">
                Quên mật khẩu?
              </Link>
            </div>
            <button className="btn submit-btn" disabled={loading}>
              {loading ? 'Đăng nhập...' : 'Đăng nhập'}
            </button>
          </form>
          <p className="text-center">
            Bạn chưa có tài khoản?{' '}
            <Link to="/dang-ki" style={{ color: '#0074da' }}>
              Đăng ký tại đây
            </Link>
          </p>
        </div>
      </Container>
    </div>
  );
}

export default LoginPage;
