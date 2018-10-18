import React from 'react';

class LogonForm extends React.Component{

    constructor(props){
        super(props);
        this.doAuthHandler =this.props.doAuth;
        this.state={username:'',password:''};
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
       
      }

      

 render(){
     
    let {error} = this.props;
    let username = this.state.username;
    let password = this.state.password;

     return (<div className="logon-form">
        <div className="logo-container">
	       <span className="title">Sign in to mobile banking</span>
	    </div>
         
		<form action="login" method="post" onSubmit={this.handleSubmit}>
         
        <div>{error}</div>
        
        <div className="form-group">
        <label htmlFor="login_field">
          Username or email address
        </label>
        <input onChange={this.handleChange} value={username} autoCapitalize="off" autoCorrect="off" autoFocus="autofocus" className="form-control input-block" id="login_field" name="username" tabIndex="1" type="text"/>
        </div>

         <div className="form-group">
        <label htmlFor="password">
          Password 
        </label>
        <input onChange={this.handleChange} value={password} className="form-control form-control input-block" id="password" name="password" tabIndex="2" type="password"/>
        </div>
        
        <input className="btn btn-primary btn-block"  data-disable-with="Signing in…" name="commit" tabIndex="3" value="Sign in" type="submit"/>
        
        </form>
  
     </div>)
 }


 handleChange= (event) =>{
    let target = event.target;
    let value = target.value;
    let name = target.name;
    this.setState({
        [name]: value
      });

 }

 handleSubmit = (e) => {
     if(this.state.username.trim()&&this.state.password.trim()){
         this.doAuthHandler(this.state.username,this.state.password);
     }
    e.preventDefault();
}





}

export default LogonForm;

