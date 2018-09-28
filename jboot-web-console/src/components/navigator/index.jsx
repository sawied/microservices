import React from 'react';
import { NavLink } from 'react-router-dom';
export default ()=>(
  <nav className="navbar navbar-expand-lg  navbar-custom">
  <button className="navbar-toggler" type="button">
    <span className="navbar-toggler-icon"></span>
  </button>
  <div className="collapse navbar-collapse" id="navbarTogglerDemo03">
    <ul className="navbar-nav mr-auto mt-2 mt-lg-0">
      <li className="nav-item active">
        <NavLink className="nav-link" activeClassName="active" exact  to="/"  >Dashboard <span className="sr-only">(current)</span></NavLink>
      </li>
      <li className="nav-item">
        <NavLink className="nav-link" activeClassName="active" to="/logger">Logger</NavLink>
      </li>
      <li className="nav-item">
        <NavLink className="nav-link" activeClassName="active" to="/monitor">Monitor</NavLink>
      </li>
    </ul>
    <span className="navbar-text">
      <a>User Profile</a>
      <a>Notification</a>
    </span>
  </div>
</nav>
)