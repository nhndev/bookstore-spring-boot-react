import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { clearErrorPage } from '../redux/slice/app.slice';

const ERROR_CONFIG = {
  401: {
    title: 'Chưa xác thực',
    defaultMessage: 'Bạn cần đăng nhập để truy cập trang này.',
    showLoginLink: true
  },
  403: {
    title: 'Truy cập bị từ chối',
    defaultMessage: 'Bạn không có quyền truy cập trang này.',
    showLoginLink: false
  },
  500: {
    title: 'Lỗi hệ thống',
    defaultMessage: 'Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.',
    showLoginLink: false
  }
};

function ErrorPage() {
  const dispatch = useDispatch();
  const { status, message } = useSelector((state) => state.app.errorPage);
  const config = ERROR_CONFIG[status] || ERROR_CONFIG[500];
  const displayMessage = message || config.defaultMessage;

  const handleGoBack = () => {
    dispatch(clearErrorPage());
    window.history.back();
  };

  const handleReload = () => {
    dispatch(clearErrorPage());
    window.location.reload();
  };

  const handleClearAndGoLogin = () => {
    dispatch(clearErrorPage());
    window.location.href = '/dang-nhap';
  };

  return (
    <div
      className="d-flex flex-column align-items-center justify-content-center"
      style={{ minHeight: '100vh', background: 'rgb(250, 250, 246)' }}
    >
      <div className="text-center">
        <h1
          className="fw-bold mb-2"
          style={{ fontSize: '120px', color: '#dee2e6' }}
        >
          {status || 500}
        </h1>
        <h4 className="fw-bold mb-3">{config.title}</h4>
        <p className="text-muted mb-4">{displayMessage}</p>
        <div className="d-flex gap-3 justify-content-center flex-wrap">
          <button
            type="button"
            className="btn btn-outline-secondary px-4"
            onClick={handleGoBack}
          >
            Quay lại
          </button>
          {config.showLoginLink ? (
            <button
              type="button"
              className="btn btn-primary px-4"
              style={{
                backgroundColor: 'var(--orange-color)',
                borderColor: 'var(--orange-color)'
              }}
              onClick={handleClearAndGoLogin}
            >
              Đăng nhập
            </button>
          ) : (
            <button
              type="button"
              className="btn btn-primary px-4"
              style={{
                backgroundColor: 'var(--orange-color)',
                borderColor: 'var(--orange-color)'
              }}
              onClick={handleReload}
            >
              Tải lại trang
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default ErrorPage;
