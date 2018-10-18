import React from 'react';
import Navigatior from './navigator';
import Auth from './auth';
import Error from './error';
import { connect } from 'react-redux';

class App extends React.Component{
    
    constructor(props){
        super(props);
       
      }

    render(){
        let {authenticated}=this.props;
        if(!authenticated){
            return <><Error/><Auth/></>
        }
        else{
            return (<>
                <div className="navigation">
                         <Navigatior/>
                 </div>
                <div className="app-context-body">
                 {this.props.children || "Welcome to your Inbox"}
            </div></>)
        }
    }
    
}
const mapStateToProps = state => ({
    authenticated:state.getIn(['auth','authenticated']),
    user_name:state.getIn(['auth','user_name']),
    authorities:state.getIn(['auth','authorities']),
    email:state.getIn(['auth','email'])
  })
export default connect(mapStateToProps)(App);