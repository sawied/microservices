import React from 'react';
import Navigatior from './navigator';
import Auth from './auth';

class App extends React.Component{
    
    constructor(props){
        super(props);
        this.authentication=this.props.authentication;
      }

    render(){
        if(!this.authentication){
            return <Auth/>
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
export default App;