import React, { memo } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Container } from 'react-bootstrap';
import { BsCart2, BsPerson } from 'react-icons/bs';
import './Header.scss';
import SearchBar from '../SearchBar';
import { useDispatch, useSelector } from 'react-redux';
import { setUser } from '../../../redux/slice/auth.slice';

function Header() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const currentUser = useSelector((state) => state.auth);

  const onClickLogout = async () => {
    dispatch(setUser({}));
    localStorage.removeItem('accessToken');
    navigate({ pathname: '/' });
  };

  return (
    <header className="header">
      <div className="header-center">
        <Container>
          <div className="d-flex align-items-center">
            {/* <NavBarMobile /> */}
            <Link to="/">
              <h1 className="title me-5">BookStore</h1>
            </Link>
            <div className="search">
              <SearchBar />
            </div>
            {/* <NavBar /> */}

            <div className="d-flex align-items-center right">
              {currentUser && currentUser?.fullName ? (
                <div className="d-flex align-items-center user">
                  <p className="me-3">Nhan Ngo</p>
                  <img className="avatar" src={currentUser?.avatarUrl} alt="" />

                  <div className="popup">
                    {currentUser.role === 0 && (
                      <>
                        <div className="item">
                          <Link className="link" to="/tai-khoan">
                            Tài khoản của tôi
                          </Link>
                        </div>
                      </>
                    )}
                    {currentUser.role > 0 && (
                      <>
                        <div className="item">
                          <Link className="link" to="/admin">
                            Quản lý BookStore
                          </Link>
                        </div>
                      </>
                    )}
                    <div className="item">
                      <p className="link" onClick={onClickLogout} to="">
                        Đăng xuất
                      </p>
                    </div>
                  </div>
                </div>
              ) : (
                <div className="icon">
                  <Link to="/dang-nhap">
                    <BsPerson />
                    <p>Đăng nhập</p>
                  </Link>
                </div>
              )}

              <div className="icon">
                <Link to="/gio-hang">
                  <BsCart2 />
                  <p>Giỏ hàng</p>
                  {/* <span className={styles.count}>{cart.list.length}</span> */}
                </Link>
              </div>
            </div>
          </div>
        </Container>
      </div>
      <div className="search-mobile">
        <Container>{/* <Search /> */}</Container>
      </div>
    </header>
  );
}

export default memo(Header);
