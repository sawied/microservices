import React from 'react';
import Error from './error';
import Navigatior from './navigator';

class App extends React.Component{
    
    constructor(props){
        super(props);   
      }
      
    render(){
        //if the children component isn't logon ,append header navigation 
            return (<>
                 <Error/>
                 <div className="navigation">
                         <Navigatior/>
                 </div>
                <div className="app-context-body">
                 {this.props.children || "Welcome to your Inbox"}
            </div></>)
        
    }
    
}

export default App;