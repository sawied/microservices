import React from 'react';

class Auth extends React.Component{
 render(){
     return (<div className="logon-form">
        <div className="logo-container">
	       <span className="title">Sign in to mobile banking</span>
	    </div>
     
		<form action="login" method="post">
        <div className="form-group">
        <label htmlFor="login_field">
          Username or email address
        </label>
        <input autoCapitalize="off" autoCorrect="off" autoFocus="autofocus" className="form-control input-block" id="login_field" name="username" tabIndex="1" type="text"/>
        </div>

         <div className="form-group">
        <label htmlFor="password">
          Password 
        </label>
        <input className="form-control form-control input-block" id="password" name="password" tabIndex="2" type="password"/>
        </div>
        
        <input className="btn btn-primary btn-block" data-disable-with="Signing in…" name="commit" tabIndex="3" value="Sign in" type="submit"/>
        
        </form>
  
     </div>)
 }
}

export default Auth;

