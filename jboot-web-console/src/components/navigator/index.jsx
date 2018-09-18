import React from 'react';
export default ()=>(
  <nav className="navbar navbar-expand-lg navbar-light navbar-custom">
  <button className="navbar-toggler" type="button">
    <span className="navbar-toggler-icon"></span>
  </button>
  <a className="navbar-brand" href="#">Web Console</a>

  <div className="collapse navbar-collapse" id="navbarTogglerDemo03">
    <ul className="navbar-nav mr-auto mt-2 mt-lg-0">
      <li className="nav-item active">
        <a className="nav-link" href="#dashboard">Dashboard <span className="sr-only">(current)</span></a>
      </li>
      <li className="nav-item">
        <a className="nav-link" href="#logger">Logger</a>
      </li>
      <li className="nav-item">
        <a className="nav-link" href="#">Monitor</a>
      </li>
    </ul>
    <span className="navbar-text">
      <a>User Profile</a>
      <a>Notification</a>
    </span>
  </div>
</nav>
)