import React from 'react';
import Navigatior from './navigator';
import OAuth from './oauth';
import Error from './error';
import { connect } from 'react-redux';
import store from 'store';

class App extends React.Component{
    
    constructor(props){
        super(props);
       
      }
      componentDidMount(){
          
      }

    render(){
        let {authenticated}=this.props;
        if(!authenticated){
            return <><Error/><OAuth/></>
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
const mapStateToProps = state => {

return {
    authenticated:state.getIn(['auth','authenticated']),
    user_name:state.getIn(['auth','user_name']),
    authorities:state.getIn(['auth','authorities']),
    email:state.getIn(['auth','email'])
  }
}
export default connect(mapStateToProps)(App);