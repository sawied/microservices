import React from 'react';
import AppViewer from './app-viewer';

class Apps extends React.Component{
    render(){
        let {applications} = this.props;

        let appItmes = applications.map((app,index)=>(<AppViewer key={index} data={app}/>));

        return (<div className="contont-body">
        <h3>Applications list</h3>
        <div className="row">
          {appItmes}
        </div>
        </div>)
    }
}

export default Apps;
