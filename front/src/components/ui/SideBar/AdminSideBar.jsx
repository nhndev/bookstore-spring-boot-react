import React from 'react';
import { Link, NavLink } from 'react-router-dom';
// import { useSelector } from 'react-redux';

// import logo from '../../../assets/images/logo.png';
// import authApi from '../../../api/authApi';
// import { logout } from '../../../redux/actions/auth';
// import { destroy } from '../../../redux/actions/cart';
import { routes } from '../../../routes';
import domUtil from '../../../utils/dom.util';
import './AdminSideBar.scss';

function AdminSideBar() {
  // const navigate = useNavigate()
  // const dispatch = useDispatch()
  // const { role } = useSelector((state) => state.auth);

  const handleLogout = async () => {
    // const resultLogout = await authApi.logout()
    // console.log(resultLogout)
    // dispatch(logout())
    // dispatch(destroy())
    // const token = localStorage.getItem('accessToken')
    // if (token) {
    //   localStorage.removeItem('accessToken')
    // }
    // navigate({ pathname: '/' })
  };
  return (
    <div className="admin-sidebar-container">
      <div className="logo">
        <Link to="/">
          {/* <img src={logo} alt="" /> */}
          <span>BookStore</span>
        </Link>
      </div>
      <div className="sidebar">
        <ul className="nav-list">
          {routes.map((item) => {
            return (
              <li className="nav-item mt-1" key={item.name}>
                <NavLink
                  data-bs-toggle={item.children && "collapse"}
                  data-bs-target={`#${domUtil.buildAminSidebarItemId(item.name)}`}
                  aria-expanded="true"
                  aria-controls={item.name}
                  className="nav-link collapsed has-dropdown"
                  to={item.path}
                  end={item.exactly}
                >
                  <span>{item?.title}</span>
                </NavLink>
                {item.children && item.children.length > 0 && (
                  <ul
                    key={item.path}
                    id={domUtil.buildAminSidebarItemId(item.name)}
                    className="sidebar-dropdown list-unstyled collapse"
                  >
                    {item.children.map((children) => {
                      return (
                        <li className="nav-item mt-1" key={children?.name}>
                          <NavLink
                            className="nav-link"
                            to={children?.path}
                            end={children?.exactly}
                          >
                            <span>{children?.title}</span>
                          </NavLink>
                        </li>
                      );
                    })}
                  </ul>
                )}
              </li>
            );
          })}
        </ul>
        <ul className="bottom">
          <li className="nav-item" onClick={handleLogout}>
            <p className="nav-link">
              <span>Đăng xuất</span>
            </p>
          </li>
        </ul>
      </div>
    </div>
  );
}

export default AdminSideBar;
